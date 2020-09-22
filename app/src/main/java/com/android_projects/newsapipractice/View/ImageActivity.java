package com.android_projects.newsapipractice.View;

import android.app.AppOpsManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.View.Managers.PermissionManager;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ActivityImageBinding;
import com.android_projects.newsapipractice.databinding.AlertDialogLayoutBinding;
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
    private AlertDialogLayoutBinding alertDialogBinding;

    //Download Manager & image share
    private BroadcastReceiver onDownloadCompleteReceiver = null;
    private final String IMG_DATE_FORMAT = "ddMMyyy_HHmm";
    private String imgURL;
    private ShareDialog fbShareDialog;

    //Google sign in
    private GoogleSignInAccount account;

    private Article articleMod;

    private PermissionManager permMgr;
    private String[] writeStoragePerm;
    private boolean isWriteExternalPermGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imgBinding = DataBindingUtil.setContentView(this, R.layout.activity_image);
        articleMod = (Article) getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);
        fbShareDialog = new ShareDialog(this);
        imgURL = articleMod.getUrlToImage();
        permMgr = new PermissionManager(this);
        writeStoragePerm = new String[]{permMgr.externalStoragePermission};

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
            if (isWriteExternalPermGranted) {
                startDownloadingImg();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermission(writeStoragePerm);
                }
            }
        });
    }

    private void configActionBar() {
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(articleMod.getTitle());
        strBuilder.setSpan(new ForegroundColorSpan(Color.WHITE), 0,
                articleMod.getTitle().length() - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        CharSequence actionBarTitleChar = strBuilder;
        setSupportActionBar(imgBinding.imgFragmentToolbar);
        getSupportActionBar().setTitle(actionBarTitleChar);
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
                    getString(R.string.img_please_sign_in), Toast.LENGTH_LONG);
        }
    }

    private void shareImage() {
        Glide.with(this).asBitmap().skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate()
                .load(imgURL).into(new CustomTarget<Bitmap>() {

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                imgShareIntent(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                Log.d(TAG, placeholder.toString());
            }
        });
    }

    private void imgShareIntent(Bitmap bitmap) {
        Uri bmpUri = getLocalBitmapUri(bitmap);

        Intent imgShareIntent = new Intent(Intent.ACTION_SEND);//same as intent.setAction();
        imgShareIntent.putExtra(Intent.EXTRA_SUBJECT, articleMod.getPublishedAt());
        imgShareIntent.putExtra(Intent.EXTRA_TEXT, articleMod.getTitle() + "\n" + articleMod.getUrl());
        imgShareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        imgShareIntent.setType("image/*");
        imgShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(imgShareIntent, "Share Opportunity"));
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
                "share_img_" + System.currentTimeMillis() + ".png");
        FileOutputStream outStream = null;

        if (!localPath.exists()) {
            MediaScannerConnection.scanFile(this, new String[]{localPath.toString()}
                    , null, (String s, Uri uri) -> {
                        Log.i(TAG, "Scanned" + s + ":");
                        Log.i(TAG, "-> uri = " + uri);
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
            bmpUri = Uri.fromFile(localPath);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage() + "\nCause: " + e.getCause());
        }
        return bmpUri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && grantResults == null ||
                grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            utility.showDebugLog(TAG, "Write external storage permission not granted.");
        } else {
            utility.showDebugLog(TAG, "Write external storage permission granted!");
            startDownloadingImg();
        }
    }

    private void checkPermission(String[] permissions) {
        permMgr.checkPermission(this, permissions[0], new PermissionManager.PermissionRequestListener() {
            @Override
            public void onNeedPermission() {
                ActivityCompat.requestPermissions(ImageActivity.this, permissions, ALL_PERMISSIONS);
            }

            @Override
            public void onPermissionPreDenied() {
                String title = getString(R.string.perm_write_external_storage_denied_title);
                String message = getString(R.string.perm_rationale_external_storage_msg);

                showCustomAlertDialog(title, message);
            }

            @Override
            public void onPermissionPreDeniedWithNeverAskAgain() {
                openSetting(getString(R.string.perm_write_external_storage_denied_title),
                        getString(R.string.perm_go_to_settings_msg));
            }

            @Override
            public void onPermissionGranted() {
                utility.showDebugLog(TAG, "Write external storage permission granted!");
                imgBinding.imgBottomNav.imgBottomNavDownload.setOnClickListener((View v) -> {
                    startDownloadingImg();
                });
            }
        });
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

    private void showCustomAlertDialog(String title, String msg) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);

        alertDialogBinding = DataBindingUtil.inflate(inflater,
                R.layout.alert_dialog_layout, null, false);
        alertDialogBinding.alertDialogTitle.setText(title);
        alertDialogBinding.alertDialogMsg.setText(msg);
        alertDialogBinding.alertDialogPosBtn.setOnClickListener((View v) -> {
            ActivityCompat.requestPermissions(ImageActivity.this, writeStoragePerm, ALL_PERMISSIONS);
            if (isWriteExternalPermGranted) {
                startDownloadingImg();
            }
            alertDialog.dismiss();
        });
        alertDialogBinding.alertDialogNegBtn.setOnClickListener((View v) -> {
            alertDialog.dismiss();
        });
        alertDialog.setView(alertDialogBinding.getRoot());
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void openSetting(String title, String msg) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);

        alertDialogBinding = DataBindingUtil.inflate(inflater,
                R.layout.alert_dialog_layout, null, false);
        alertDialogBinding.alertDialogTitle.setText(title);
        alertDialogBinding.alertDialogMsg.setText(msg);
        alertDialogBinding.alertDialogPosBtn.setText(getString(R.string.perm_pos_setting));
        alertDialogBinding.alertDialogNegBtn.setText(getString(R.string.perms_neg_not_now));

        alertDialogBinding.alertDialogPosBtn.setOnClickListener((View v) -> {
            utility.openAppSettings(this);
            alertDialog.dismiss();
            if (utility.isSettingAccessEnabled(this, AppOpsManager.OPSTR_WRITE_EXTERNAL_STORAGE)) {
                startDownloadingImg();
            }
        });
        alertDialogBinding.alertDialogNegBtn.setOnClickListener((View v) -> {
            alertDialog.dismiss();
        });
        alertDialog.setView(alertDialogBinding.getRoot());
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
