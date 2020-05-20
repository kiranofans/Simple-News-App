package com.android_projects.newsapipractice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.android_projects.newsapipractice.ViewModels.LoginViewModel;
import com.android_projects.newsapipractice.databinding.ActivityFacebookLoginBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FacebookLoginActivity extends AppCompatActivity {
    private final String TAG = FacebookLoginActivity.class.getSimpleName();
    private ActivityFacebookLoginBinding  loginBinding;
    private LoginViewModel loginViewModel;

    private final String EMAIL = "email";
    private AccessToken accessToken;
    //private AccessTokenTracker accessTokenTracker;

    private CallbackManager fbCallbackMgr;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        loginBinding= DataBindingUtil.setContentView(this,R.layout.activity_facebook_login);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //facebookLogin();

    }
    /*private void facebookLogin(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        accessToken=AccessToken.getCurrentAccessToken();

        loginBinding.buttonFacebookLogin.setPermissions(Arrays.asList(EMAIL,"public_profile"));
        startActivity(new Intent(FacebookLoginActivity.this, SettingsActivity.class));

        fbCallbackMgr = CallbackManager.Factory.create();
        checkLoginStatus();
        fbLoginCallback();
    }*/

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbCallbackMgr.onActivityResult(requestCode,resultCode,data);
    }*/

    /*private void fbLoginCallback(){
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
                startActivity(new Intent(FacebookLoginActivity.this, SettingsActivity.class));

                Log.d(TAG, loginResult.toString() + " Logged in successfully");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook sign in process canceled");
                Toast.makeText(FacebookLoginActivity.this, "Facebook Login Canceled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Facebook sign in error");
            }
        });
    }*/
    /*AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken == null){
                Log.d(TAG,"Logged Out facebook account");;
            }else{
                //make avatar visible
            }
        }
    };*/
    /*private void checkLoginStatus(){
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
    }*/
}
