package com.android_projects.newsapipractice.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.Utils.Utility;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ActivityImageBinding;
import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.File;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;

public class ImageActivity extends AppCompatActivity {
    private final String TAG = ImageActivity.class.getSimpleName();

    private BroadcastReceiver onDownloadCompleteReceiver = null;
    private final int WRITE_EXTERNAL_STORAGE_RC=102;
    private Bitmap bitmapImg;
    private File mDir;
    private ShareDialog fbShareDialog;

    private GoogleSignInAccount account;

    private ActivityImageBinding imgBinding;
    private Article articleMod;

    private Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBinding= DataBindingUtil.setContentView(this, R.layout.activity_image);
        articleMod = (Article)getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);
        fbShareDialog = new ShareDialog(this);
        utility=new Utility();

        getSerializable();
        imgBottomButtons();
    }

    private void getSerializable(){
        if(!isObjNull()){
            configActionBar();
            Glide.with(this).load(articleMod.getUrlToImage()).into(imgBinding.fullImageView);
        }
    }

    private void imgBottomButtons(){
        imgBinding.imgBottomNav.imgBottomNavShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isObjNull()){
                    utility.shareArticles(articleMod,ImageActivity.this,"Share image via");
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
                downloadImgWithPermission();
            }
        });
    }

    private void configActionBar(){
        setSupportActionBar(imgBinding.imgFragmentToolbar);
        getSupportActionBar().setTitle(articleMod.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void downloadImage(){
        //Pictures folder in Internal Storage
        String destinationPath= Environment.DIRECTORY_PICTURES;
        Uri url = Uri.parse(articleMod.getUrlToImage());

        //the mDir is currently Pictures/NewsApp/
        File mDir = new File(destinationPath,"NewsApp/");
        DownloadManager downloadMgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

        if(!mDir.exists()){
            mDir.mkdir();
            Log.d(TAG,"Folder created at "+mDir);
            //Created the Pictures/NewsApp/ folder
        }
        DownloadManager.Request downloadRqst= new DownloadManager.Request(url);
        downloadRqst.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                DownloadManager.Request.NETWORK_MOBILE).setAllowedOverMetered(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverRoaming(false).setTitle("News App image download")
                .setDescription("Image Downloading...")
                .setDestinationInExternalPublicDir(mDir.toString(),"IMG_"+articleMod.getPublishedAt()+".jpg");
        //setDestinationInExternalPublicDir Saved the image to Pictures/NewsApp/ folder, so the image's final path is
        // Pictures/NewsApp/IMG_[publishAtDate].jpg

        long downloadId = downloadMgr.enqueue(downloadRqst);
        onDownloadCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                if(downloadId==id){
                    String downloadText = "Image for the article '"+articleMod.getTitle()+
                            "' download completed!"+"\nSaved image to "+mDir;

                    utility.showToastMessage(context,downloadText,Toast.LENGTH_LONG);
                    Log.d(TAG,"Image finally saved to "+mDir);
                    //the image finally saved to Pictures/NewsApp/IMG_[time].jpg
                    //The absolute path is: /Pictures/NewsApp/Img_[time].jpg
                }
            }
        };
        this.registerReceiver(onDownloadCompleteReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void downloadImgWithPermission(){
        account=GoogleSignIn.getLastSignedInAccount(this);
        String externalStoragePerm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String[] permissionType = {externalStoragePerm};
        boolean isGranted = ContextCompat.checkSelfPermission(this,externalStoragePerm)
                == PackageManager.PERMISSION_GRANTED;
        if(isGranted){
            if(account!=null){
                Log.d(TAG,"Access Token is OK\n"+account.getIdToken());
                utility.showToastMessage(getApplicationContext(),"Start downloading...",Toast.LENGTH_SHORT);
                downloadImage();
            }else{
                utility.showToastMessage(getApplicationContext(),
                        "Sorry, You have to sign in to use this feature",Toast.LENGTH_LONG);
            }
        }else{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                ActivityCompat.requestPermissions(this,permissionType,WRITE_EXTERNAL_STORAGE_RC);
            }
        }
    }

    private void shareImage(){
        //convertImgUrlToBitmap();

        Intent imgShareIntent = new Intent(Intent.ACTION_SEND);//same as intent.setAction();
        imgShareIntent.setType("text/*");
        imgShareIntent.putExtra(Intent.EXTRA_SUBJECT,articleMod.getPublishedAt());
        imgShareIntent.putExtra(Intent.EXTRA_TEXT,articleMod.getTitle()+"\n"+articleMod.getUrl());
        //Uri imgUri = Uri.parse(String.valueOf(mDir));
        //imgShareIntent.putExtra(Intent.EXTRA_TEXT,imgUri);
        startActivity(Intent.createChooser(imgShareIntent,"Share Image Via"));

    }
    private void shareImgWithFB(ShareDialog shareDialog){
        String newsSource = articleMod.getSource().getName();
        ShareLinkContent shareImgLinkContent = new ShareLinkContent.Builder()
                .setQuote(articleMod.getTitle()).setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#"+newsSource).build())
                .setContentUrl(Uri.parse(articleMod.getUrlToImage())).build();

        if(shareDialog.canShow(ShareLinkContent.class)){
            shareDialog.show(shareImgLinkContent);
        }
    }

    private void shareImgWithTwitter(){
        if(bitmapImg!=null){

        }

    }

    private boolean isObjNull(){
        if(articleMod==null){
            Log.d(TAG,"Article object is null");
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
        try{
            this.unregisterReceiver(onDownloadCompleteReceiver);
        }catch (IllegalArgumentException e){
            Log.d(TAG,e.getMessage()+"\nCause: "+e.getCause());
        }
    }
}
