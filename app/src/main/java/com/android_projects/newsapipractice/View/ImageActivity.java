package com.android_projects.newsapipractice.View;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.Utils.Utility;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ActivityImageBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;

public class ImageActivity extends AppCompatActivity {
    private final String TAG = ImageActivity.class.getSimpleName();

    private BroadcastReceiver onDownloadCompleteReceiver = null;
    private ShareDialog fbShareDialog;
    private GoogleSignInAccount account;

    private ActivityImageBinding imgBinding;
    private Article articleMod;

    private Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBinding = DataBindingUtil.setContentView(this, R.layout.activity_image);
        articleMod = (Article) getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);
        fbShareDialog = new ShareDialog(this);
        utility = new Utility();

        getSerializable();
        imgBottomButtons();
    }

    private void getSerializable() {
        if (!isArticleObjNull()) {
            configActionBar();
            Glide.with(this).load(articleMod.getUrlToImage()).into(imgBinding.fullImageView);
            Log.d(TAG, "Image URL: " + articleMod.getUrlToImage());
        }
    }

    private void imgBottomButtons() {
        imgBinding.imgBottomNav.imgBottomNavShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isArticleObjNull()) {
                    //utility.shareArticles(articleMod,ImageActivity.this,"Share image via");

                    String imgFilePath = Environment.DIRECTORY_DCIM;
                    File imgCacheDir = new File(imgFilePath,"NewsAppCache/");
                    shareImage(imgCacheDir);
                }
            }
        });

        imgBinding.imgBottomNav.imgBottomNavFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImgWithFB(fbShareDialog);
            }
        });
        imgBinding.imgBottomNav.imgBottomNavDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownloadingImg();
            }
        });
    }

    private void configActionBar() {
        setSupportActionBar(imgBinding.imgFragmentToolbar);
        getSupportActionBar().setTitle(articleMod.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void downloadImage() {
        String imgFileName = "IMG_"+utility.imgFileDateTimeConversion
                ("ddMMyyy_HHmm")+".jpg";
        //Pictures folder in Internal Storage
        String destinationPath = Environment.DIRECTORY_PICTURES;
        Uri url = Uri.parse(articleMod.getUrlToImage());

        //the mDir is currently Pictures/SimpleNewsApp/
        File mDir = new File(destinationPath, "SimpleNewsApp/");
        DownloadManager downloadMgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        if (!mDir.exists()) {
            mDir.mkdir();
            Log.d(TAG, "Folder created at " + mDir);
            //Created the Pictures/SimpleNewsApp/ folder
        }
        DownloadManager.Request downloadRqst = new DownloadManager.Request(url);
        downloadRqst.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                DownloadManager.Request.NETWORK_MOBILE).setAllowedOverMetered(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverRoaming(false).setTitle("News App image download")
                .setDescription("Image Downloading...")
                .setDestinationInExternalPublicDir(mDir.toString(), imgFileName);
        //setDestinationInExternalPublicDir Saved the image to Pictures/NewsApp/ folder, so the image's final path is
        // Pictures/NewsApp/IMG_[publishAtDate].png

        long downloadId = downloadMgr.enqueue(downloadRqst);
        onDownloadCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId == id) {
                    String downloadText = "Image for the article '" + articleMod.getTitle() +
                            "' download completed!" + "\nSaved image to " + mDir;

                    utility.showToastMessage(context, downloadText, Toast.LENGTH_LONG);
                    Log.d(TAG, "Image finally saved to " + mDir);
                    //the image finally saved to Pictures/SimpleNewsApp/IMG_[time].jpg
                    //The absolute path is: /Pictures/SimpleNewsApp/Img_[time].jpg
                }
            }
        };
        this.registerReceiver(onDownloadCompleteReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void startDownloadingImg() {
        account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            Log.d(TAG, "Access Token is OK\n" + account.getIdToken());
            utility.showToastMessage(getApplicationContext(), "Start downloading...", Toast.LENGTH_SHORT);
            downloadImage();
        } else {
            utility.showToastMessage(getApplicationContext(),
                    "Sorry, You have to sign in to use this feature", Toast.LENGTH_LONG);
        }
    }

    private void shareImage(final File imagePath) {
        String fileName = "IMG_"+utility.imgFileDateTimeConversion
                ("ddMMyyy_HHmm")+".png";
        Intent imgShareIntent = new Intent(Intent.ACTION_SEND);//same as intent.setAction();
        imgShareIntent.setType("*/*");
        imgShareIntent.putExtra(Intent.EXTRA_SUBJECT, articleMod.getPublishedAt());
        imgShareIntent.putExtra(Intent.EXTRA_TEXT, articleMod.getTitle() + "\n" + articleMod.getUrl());
        boolean isSuccess;
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1){

        }
        if(!imagePath.exists()){
            //Create folder first
            imagePath.mkdirs();
            isSuccess=imagePath.mkdirs();
            Log.d(TAG,"Is Folder Created: "+isSuccess);
        }
        Glide.with(getApplicationContext()).asBitmap().load(articleMod.getUrlToImage())
                .into(new CustomTarget<Bitmap>(100,80) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmapImg, @Nullable Transition<? super Bitmap> transition) {
                        //saveImageToBitmap(bitmapImg,imagePath);
                        try{
                            OutputStream outStream = new FileOutputStream(imagePath.getAbsolutePath());
                            bitmapImg.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                            Uri imgUri = Uri.fromFile(imagePath);
                            imgShareIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
                            startActivity(Intent.createChooser(imgShareIntent, "Share Image Via"));
                            outStream.close();

                        }catch (IOException e){
                            Log.d(TAG, e.getMessage() + "\nCause: " + e.getCause());
                        }

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.d(TAG, "Clear cache");
                    }
                });
    }

    private void shareImgWithFB(ShareDialog shareDialog) {
        String newsSource = articleMod.getSource().getName();
        ShareLinkContent shareImgLinkContent = new ShareLinkContent.Builder()
                .setQuote(articleMod.getTitle()).setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#" + newsSource).build())
                .setContentUrl(Uri.parse(articleMod.getUrlToImage())).build();

        if (shareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(shareImgLinkContent);
        }
    }

    private void shareImgWithTwitter() {


    }

    private boolean isArticleObjNull() {
        if (articleMod == null) {
            Log.d(TAG, "Article object is null");
            return true;
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();//set the back arrow onClick event
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            this.unregisterReceiver(onDownloadCompleteReceiver);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.getMessage() + "\nCause: " + e.getCause());
        }
    }

    private void saveImageToBitmap(Bitmap bitmap,File imgPath){
        boolean isSuccess = false;
        if(isSuccess){
            try{
                Log.d(TAG,"Saving img to "+imgPath.getAbsolutePath());
                OutputStream outStream = new FileOutputStream(imgPath.getAbsolutePath());
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.close();
                // PNG is a lossless format, the compression factor (100) is ignored

            } catch (IOException e) {
                Log.d(TAG, e.getMessage() + "\nCause: " + e.getCause());
            }
        }

    }
}
