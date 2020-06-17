package com.android_projects.newsapipractice.View;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ActivityImageBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;

public class ImageActivity extends BaseActivity {
    private final String TAG = ImageActivity.class.getSimpleName();

    //Views
    private ActivityImageBinding imgBinding;

    //Download Manager & image share
    private BroadcastReceiver onDownloadCompleteReceiver = null;
    private final String IMG_DATE_FORMAT = "ddMMyyy_HHmm";
    private String imgURL;
    private ShareDialog fbShareDialog;

    //Google sign in
    private GoogleSignInAccount account;

    private Article articleMod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBinding = DataBindingUtil.setContentView(this, R.layout.activity_image);
        articleMod = (Article) getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);
        fbShareDialog = new ShareDialog(this);
        imgURL = articleMod.getUrlToImage();

        getSerializable();
        imgBottomButtons();
    }

    private void getSerializable() {
        if (!isArticleObjNull()) {
            configActionBar();
            Glide.with(this).load(imgURL).into(imgBinding.fullImageView);
            Log.d(TAG, "Image URL: " + articleMod.getUrlToImage());
        }
    }

    private void imgBottomButtons() {
        imgBinding.imgBottomNav.imgBottomNavShare.setOnClickListener((View view) -> {
            if (!isArticleObjNull()) {
                shareImage();
            }
        });

        imgBinding.imgBottomNav.imgBottomNavFacebook.setOnClickListener((View v) -> {
            shareImgWithFB(fbShareDialog);
        });
        imgBinding.imgBottomNav.imgBottomNavDownload.setOnClickListener((View v) -> {
            startDownloadingImg();
        });
    }

    private void configActionBar() {
        setSupportActionBar(imgBinding.imgFragmentToolbar);
        getSupportActionBar().setTitle(articleMod.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void downloadImage() {
        String imgFileName = "IMG_" + utility.getLocalDateTime
                (IMG_DATE_FORMAT) + ".jpg";
        //Pictures folder in Internal Storage
        String destinationPath = Environment.DIRECTORY_PICTURES;
        Uri url = Uri.parse(imgURL);

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
        // Pictures/NewsApp/IMG_[publishAtDate].jpg

        long downloadId = downloadMgr.enqueue(downloadRqst);
        onDownloadCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId == id) {
                    String downloadText = "Image for the article '" + articleMod.getTitle() +
                            "' download completed!" + "\nSaved image to " + mDir;

                    utility.showToastMsg(context, downloadText, Toast.LENGTH_LONG);
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
            utility.showToastMsg(getApplicationContext(), "Start downloading...", Toast.LENGTH_SHORT);
            downloadImage();
        } else {
            utility.showToastMsg(getApplicationContext(),
                    "Sorry, You have to sign in to use this feature", Toast.LENGTH_LONG);
        }
    }

    private void shareImage(){
        Glide.with(this).asBitmap().skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate()
                .load(imgURL).into(new CustomTarget<Bitmap>(){

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                imgShareIntent(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                Log.d(TAG,placeholder.toString());
            }
        });
    }
    private void imgShareIntent(Bitmap bitmap) {
        Uri bmpUri = getLocalBitmapUri(bitmap);

        Intent imgShareIntent = new Intent(Intent.ACTION_SEND);//same as intent.setAction();
        imgShareIntent.putExtra(Intent.EXTRA_SUBJECT, articleMod.getPublishedAt());
        imgShareIntent.putExtra(Intent.EXTRA_TEXT, articleMod.getTitle() + "\n" + articleMod.getUrl());
        imgShareIntent.putExtra(Intent.EXTRA_STREAM,bmpUri);
        imgShareIntent.setType("image/*");
        imgShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(imgShareIntent,"Share Opportunity"));
    }

    private void shareImgWithFB(ShareDialog shareDialog) {
        ShareLinkContent shareImgLinkContent = new ShareLinkContent.Builder()
                .setQuote(articleMod.getTitle()).setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#" + articleMod.getSource().getName()).build())
                .setContentUrl(Uri.parse(imgURL)).build();

        if (shareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(shareImgLinkContent);
        }
    }

    private boolean isArticleObjNull() {
        if (articleMod == null) {
            Log.d(TAG, "Article object is null");
            return true;
        }
        return false;
    }

    private Uri getLocalBitmapUri(Bitmap bitmap) {
        Uri bmpUri = null;
        String cachePath = this.getExternalCacheDir().getAbsolutePath();
        /* Need to use getExternalStoragePublicDirectory(ENVIRONMENT.DIRECTORY_PICTURES) to avoid
        ErrnoException: open failed: ENOENT (No such file or directory) if storing img to DIRECTORY_PICTURES */

        //store file to /storage/emulated/0/Android/data/...
        File localPath = new File(cachePath,
                "share_img_" +System.currentTimeMillis()+".png");
        FileOutputStream outStream = null;

        if (!localPath.exists()){
            MediaScannerConnection.scanFile(this, new String[]{localPath.toString()}
                    , null, (String s, Uri uri) ->{
                        Log.i(TAG,"Scanned"+s+":");
                        Log.i(TAG,"-> uri = "+uri);
                        //In case some phones won't work
                    });

            //Calling getParentFile() because the '.png' is a directory, and mkdir is enough for my case
            localPath.getParentFile().mkdir();
        }
        try {
            Log.d(TAG, "Saving img to " + localPath.getAbsolutePath());
            outStream = new FileOutputStream(localPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();
            // PNG is a lossless format, the compression factor (100) is ignored
            bmpUri=Uri.fromFile(localPath);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage() + "\nCause: " + e.getCause());
        }
        return bmpUri;
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
}
