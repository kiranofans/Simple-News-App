package com.android_projects.newsapipractice.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.android_projects.newsapipractice.BuildConfig;
import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.ViewModels.FacebookLoginViewModel;
import com.android_projects.newsapipractice.databinding.ActivityLoginBinding;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;

import static com.google.android.gms.common.Scopes.DRIVE_APPFOLDER;

public class LoginActivity extends BaseActivity {
    private final String TAG = LoginActivity.class.getSimpleName();

    private ActivityLoginBinding loginBinding;

    //Google sign in
    private GoogleSignInOptions gso;
    public static GoogleSignInClient googleSignInClient;
    private String googleClientID;
    private final int RC_GOOGLE_SIGN_IN = 202;
    public static String googleIdToken;

    //Facebook log in
    private final int RC_FACEBOOK_LOG_IN = 201;
    private boolean isLoggedInToFB = false;
    private AccessToken fbAccessToken;
    private FacebookLoginViewModel fbLoginViewModel;
    private final String PARAM_EMAIL = "email";
    private CallbackManager fbCallbackMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        fbLoginViewModel = new ViewModelProvider(this).get(FacebookLoginViewModel.class);

        googleLogin();
        facebookLogin();
    }

    private void facebookLogin() {
        fbAccessToken = AccessToken.getCurrentAccessToken();

        fbCallbackMgr = CallbackManager.Factory.create();

        //Facebook login callback
        LoginManager.getInstance().registerCallback(fbCallbackMgr, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Logged in to Facebook ic_account");
                fbAccessToken = loginResult.getAccessToken();
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

        loginBinding.buttonFacebookLogin.setOnClickListener((View v) -> {
            //Intent fbIntent = ;
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                    Arrays.asList("public_profile", "user_friends"));
            //startActivityForResult(fbIntent,RC_FACEBOOK_LOG_IN);
        });
    }

    private void googleLogin() {
        googleClientID= BuildConfig.GOOGLE_SERVER_CLIENT_ID_DEBUG;
        //If requestServerAuthCode() is called, you don't have to call requestIdToken
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(googleClientID)/*.requestServerAuthCode(googleClientID)*/
                .requestScopes(new Scope(DRIVE_APPFOLDER)).requestEmail().build();
        //DRIVE_APPFOLDER: Scope for accessing appfolder files from Google Drive.

        //Build a GoogleSignInClient with the options specified by gso
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        loginBinding.buttonGoogleLogin.setOnClickListener((View v) -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
            utility.showDebugLog(TAG, "Logged in succeed");

        }
        if (fbCallbackMgr.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    public void handleGoogleSignInResult(Task<GoogleSignInAccount> completeTask) {
        try {
            GoogleSignInAccount googleAccount = completeTask.getResult(ApiException.class);
            if (googleAccount.getIdToken() != null) {
                Toast.makeText(getApplicationContext(), "Result OK. Username: " +
                        googleAccount.getDisplayName(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Sign in result ok");
                onLoggedInWithGoogle(googleAccount, true);
                //Update UI
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //Update UI

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        onLoggedInWithGoogle(account, true);

        isLoggedInToFB = fbAccessToken != null && !fbAccessToken.isExpired();

        if (isLoggedInToFB) {
            onLoggedInWithFacebook(isLoggedInToFB);
            utility.showToastMsg(getApplicationContext(),
                    "Already logged in with Facebook", Toast.LENGTH_SHORT);
        } else {
            utility.showToastMsg(getApplicationContext(),
                    "Not logged in with Facebook ", Toast.LENGTH_SHORT);
        }

    }

    private void onLoggedInWithGoogle(GoogleSignInAccount account, boolean isSignedIn) {
        if (account != null && isSignedIn) {
            Intent googleCredentialIntent = new Intent(this, MyAccountActivity.class);
            googleCredentialIntent.putExtra("GOOGLE_CREDENTIALS", account);

            startActivity(googleCredentialIntent);
            this.finish();
        }
    }

    private void onLoggedInWithFacebook(boolean isLoggedIn) {
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    //update ui: logged out from facebook

                } else {
                    //ui visibility: Visible
                }
            }
        };
        accessTokenTracker.startTracking();
        if (isLoggedIn) {
            Intent fbLoggedInIntent = new Intent(this, MyAccountActivity.class);
            startActivity(fbLoggedInIntent);
        }
    }

    @Override
    public void onNetworkConnectionChanged(Boolean isConnected) {
        if(!isConnected){
            utility.showToastMsg(getApplicationContext(),
                    "No Internet connection", Toast.LENGTH_LONG);
        }
    }
}
