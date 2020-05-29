package com.android_projects.newsapipractice.Utils;

import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class Utility {
    private final String TAG = Utility.class.getSimpleName();

    public boolean isLoggedIn(GoogleSignInAccount alreadyLoggedInAccount){
        if (alreadyLoggedInAccount != null) {
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
                /*Toast.makeText(getApplicationContext(),"Result OK. Username: "+
                        googleAccount.getDisplayName(),Toast.LENGTH_SHORT).show();*/
                Log.d(TAG,"Sign in result ok");
               // onLoggedInWithGoogle(googleAccount,true);
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
}
