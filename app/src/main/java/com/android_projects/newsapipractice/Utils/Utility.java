package com.android_projects.newsapipractice.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.android_projects.newsapipractice.BuildConfig;
import com.android_projects.newsapipractice.data.Models.Article;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {
    private final String TAG = Utility.class.getSimpleName();

    public boolean isLoggedInWithGoogle(Context context){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            Log.d(TAG,"Already logged in with Google");
            return true;
        } else {
            Log.d(TAG, "Not logged in yet");
            return false;
        }
    }

    public void handleGoogleSignInResult(Task<GoogleSignInAccount> completeTask/*, GoogleSignInResult result*/){
        try{
            GoogleSignInAccount googleAccount = completeTask.getResult(ApiException.class);
            if(googleAccount.getIdToken()!=null){
                Log.d(TAG,"Sign in result ok");
                //Update UI
            }


        }catch (ApiException e){
            // The ApiException status code indicates the detailed failure reason.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //Update UI

        }
    }
    public boolean isLoggedInWithFB( AccessToken accessToken){
        if(accessToken==null){
            return false;
        }
        Log.d(TAG,"Already logged in with Facebook");
        return true;
    }

    public void showToastMessage(Context context,String message,int length){
        Toast.makeText(context, message,length).show();
    }
    public void shareArticles(Article obj,Context context,String shareStr){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        //Pass your sharing content to the "putExtra" method of the Intent class
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,obj.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT,obj.getTitle()+" "+obj.getUrl());
        context.startActivity(Intent.createChooser(shareIntent,shareStr));
    }
    public String imgFileDateTimeConversion(String dateTimePattern){
        return new SimpleDateFormat(dateTimePattern).format(new Date());
    }
    public String articleDateTimeConversion(String publishDateTime){
        //Need to convert to local time firs
        return new SimpleDateFormat("EEEE, dd MMMM yyyy").format(publishDateTime);
    }

    public void showWriteExternalStorageRational(Activity activityContext, String[] permissionArray,int requestCode){
        new AlertDialog.Builder(activityContext).setTitle("Location permission denied")
                .setMessage("You have to allow this permission to view Local News content").setCancelable(false)
                .setNegativeButton("STILL DENY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(activityContext,permissionArray,requestCode);
                dialogInterface.dismiss();
            }
        }).show();
    }
    private void openSetting(Context context){
        Intent settingIntent = new Intent();
        settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID,null);
        settingIntent.setData(uri);
        settingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(settingIntent);
    }

    public void dialogToOpenSetting(Context context,String title, String message){
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(false)
                .setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("GO TO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openSetting(context);
                dialogInterface.dismiss();
            }
        }).show();
    }


    public boolean hasAllLocationPermGranted(int[] grantResults){
        for(int grantResult:grantResults){
            if(grantResult==PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }
    public boolean hasLocationPermGranted(int requestCode, String[] permissions, int[] grantResults){
        for(int grantResult:grantResults){
            if(grantResult==PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }
    public boolean hasWriteExternalPermGranted(int[] grantResults){
        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }

   /* public int getPermissionResult(int requestCode){
        if(requestCode==100){
            boolean isLocationGranted
        }
    }*/
}
