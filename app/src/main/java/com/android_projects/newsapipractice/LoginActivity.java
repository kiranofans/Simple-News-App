package com.android_projects.newsapipractice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android_projects.newsapipractice.ViewModels.LoginViewModel;
import com.android_projects.newsapipractice.databinding.ActivityLoginBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;

import static com.google.android.gms.common.Scopes.DRIVE_APPFOLDER;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private final String TAG = LoginActivity.class.getSimpleName();

    private ActivityLoginBinding loginBinding;
    private static String username,email, userID,userAvatarUrl="None";

    //Google sign in
    private GoogleSignInOptions gso;
    private GoogleSignInClient googleSignInClient;
    private GoogleApiClient googleApiClient;
    private final String googleClientSecret = "ssMGge9ukL0GS4bf4BqD-P8B";

    //Google Server Client id
    private final String googleClientID = "927157329080-u3kmqqlngtk79d0vj1pt8kq5mt5ce8lb.apps.googleusercontent.com";
    private final int RC_GOOGLE_SIGN_IN=202;
    private String googleIdToken;

    //Facebook log in
    private final int RC_FACEBOOK_LOG_IN=201;
    private AccessToken facebookAccessToken;
    private LoginViewModel fbLoginViewModel;
    private final String PARAM_EMAIL = "email";
    private CallbackManager fbCallbackMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding= DataBindingUtil.setContentView(this,R.layout.activity_login);
        fbLoginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        googleLogin();
        facebookLogin();
    }

    private void facebookLogin(){
        fbCallbackMgr = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(fbCallbackMgr, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"Logged in to Facebook account");
                facebookAccessToken=loginResult.getAccessToken();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Login with Facebook canceled",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        loginBinding.buttonFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent fbIntent = ;
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "user_friends"));
                //startActivityForResult(fbIntent,RC_FACEBOOK_LOG_IN);
            }
        });
    }

    private void googleLogin(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(googleClientID).requestServerAuthCode(googleClientID)
                .requestScopes(new Scope(DRIVE_APPFOLDER)).requestEmail().build();

        //GoogleApiClient deprecated: to call detailed permission intent
        /*googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage
                (this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();*/

        //Build a GoogleSignInClient with the options specified by gso
        googleSignInClient = GoogleSignIn.getClient(this,gso);

        loginBinding.buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
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
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task,result);
            Toast.makeText(getApplicationContext(),"Logged in successfully",
                    Toast.LENGTH_SHORT).show();

        }
        if(fbCallbackMgr.onActivityResult(requestCode,resultCode,data)){
            return;
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completeTask, GoogleSignInResult result){
        try{
            GoogleSignInAccount googleAccount = completeTask.getResult(ApiException.class);
            if(result.isSuccess()){
                Toast.makeText(getApplicationContext(),"Result OK. Username: "+
                        googleAccount.getDisplayName(),Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Sign in result ok");
                onLoggedInWithGoogle(googleAccount,true);
                //Update UI
            }


        }catch (ApiException e){
            // The ApiException status code indicates the detailed failure reason.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //Update UI

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount alreadyLoggedGoogleAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(alreadyLoggedGoogleAccount != null){
            Toast.makeText(getApplicationContext(),"Already logged in with Google",
                    Toast.LENGTH_SHORT).show();
            onLoggedInWithGoogle(alreadyLoggedGoogleAccount,true);
        }else{
            Toast.makeText(getApplicationContext(),"Not logged in "+
                    GoogleSignIn.getLastSignedInAccount(this),Toast.LENGTH_SHORT).show();

            Log.d(TAG,"Not logged in yet");
        }

        if(facebookAccessToken!=null){
            onLoggedInWithFacebook(true);
            Toast.makeText(getApplicationContext(),"Already logged in with Facebook",
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Not logged in with Facebook ",Toast.LENGTH_SHORT).show();
        }

    }

    private void onLoggedInWithGoogle(GoogleSignInAccount account, boolean isSignedIn){
        if(isSignedIn){
            Intent googleCredentialIntent = new Intent(this,MyAccountActivity.class);
            googleCredentialIntent.putExtra("GOOGLE_CREDENTIALS",account);
            startActivity(googleCredentialIntent);
            this.finish();
        }
    }

    private void onLoggedInWithFacebook(boolean isLoggedIn){
        if(isLoggedIn){
            Intent fbLoggedInIntent = new Intent(this,MyAccountActivity.class);
            startActivity(fbLoggedInIntent);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Google connection error has occurred: "+
                connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}
