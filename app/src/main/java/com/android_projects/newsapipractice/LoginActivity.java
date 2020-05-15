package com.android_projects.newsapipractice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.android_projects.newsapipractice.ViewModels.LoginViewModel;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.databinding.ActivityLoginBinding;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();
    private ActivityLoginBinding loginBinding;
    private LoginViewModel loginViewModel;

    private final String EMAIL = "email";
    private AccessToken accessToken;
    //private AccessTokenTracker accessTokenTracker;

    private CallbackManager fbCallbackMgr;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        loginBinding= DataBindingUtil.setContentView(this,R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        getSupportActionBar().setTitle("Login");
        facebookLogin();

    }
    private void facebookLogin(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        accessToken=AccessToken.getCurrentAccessToken();

        loginBinding.buttonFacebookLogin.setPermissions(Arrays.asList(EMAIL,"public_profile"));
        startActivity(new Intent(LoginActivity.this, SettingsActivity.class));

        fbCallbackMgr = CallbackManager.Factory.create();
        checkLoginStatus();
        fbLoginCallback();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbCallbackMgr.onActivityResult(requestCode,resultCode,data);
    }

    private void fbLoginCallback(){
        loginBinding.buttonFacebookLogin.registerCallback(fbCallbackMgr, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                GraphRequestAsyncTask graphRequestAsyncTask = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        LoginManager.getInstance().logOut();

                    }
                }).executeAsync();
                Profile profile= Profile.getCurrentProfile();
                loginViewModel.loadUserProfile(accessToken);
                startActivity(new Intent(LoginActivity.this, SettingsActivity.class));

                Log.d(TAG, loginResult.toString() + " Logged in successfully");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook sign in process canceled");
                Toast.makeText(LoginActivity.this, "Facebook Login Canceled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Facebook sign in error");
            }
        });
    }
    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken == null){
                Log.d(TAG,"Logged Out facebook account");;
            }else{
                //make avatar visible
            }
        }
    };
    private void checkLoginStatus(){
        boolean isLoggedInToFB = accessToken!=null && !accessToken.isExpired();

        accessTokenTracker.startTracking();
        if(isLoggedInToFB){
            loginViewModel.loadUserProfile(accessToken).observe(this, new Observer<List<String>>() {
                @Override
                public void onChanged(List<String> strings) {
                    Log.d(TAG, isLoggedInToFB + " Logged in successfully");
                    loginBinding.buttonGoogleLogin.setText("Is logged in: "+strings.get(1));
                    Toast.makeText(getApplicationContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            //make imageview gone
        }
    }
}
