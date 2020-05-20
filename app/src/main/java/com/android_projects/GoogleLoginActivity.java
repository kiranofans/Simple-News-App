package com.android_projects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.databinding.ActivityGoogleLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

public class GoogleLoginActivity extends AppCompatActivity {
    private final String TAG = com.android_projects.GoogleLoginActivity.class.getSimpleName();

    private ActivityGoogleLoginBinding googleLoginBinding;
    private GoogleSignInOptions gso;
    private GoogleSignInClient googleSignInClient;

    private final String googleClientSecret = "UHuUK6ih36dp1Bam_C8OxKqF";
    private final String googleClientID = "6880028977-254005n4n79i68ebkr8v53jiar34cfm3.apps.googleusercontent.com";
    private final int RC_GOOGLE_SIGN_IN=202;

    //private GoogleSignInClient
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleLoginBinding = DataBindingUtil.setContentView(this,R.layout.activity_google_login);

        googleLogin();
    }

    private void googleLogin(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        //Build a GoogleSignInClient with the options specified by gso
        googleSignInClient = GoogleSignIn.getClient(this,gso);

        googleLoginBinding.buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent,RC_GOOGLE_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_GOOGLE_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            Toast.makeText(getApplicationContext(),"Logged in successfully",Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completeTask){
        try{
            GoogleSignInAccount googleAccount = completeTask.getResult(ApiException.class);
            //Update UI
        }catch (ApiException e){
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //Update UI
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);

    }
}
