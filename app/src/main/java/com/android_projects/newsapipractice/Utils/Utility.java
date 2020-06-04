package com.android_projects.newsapipractice.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android_projects.newsapipractice.data.Models.Article;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

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
}
