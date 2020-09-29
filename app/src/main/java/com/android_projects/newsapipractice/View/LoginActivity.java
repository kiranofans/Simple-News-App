package com.android_projects.newsapipractice.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.android_projects.newsapipractice.BuildConfig;
import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import static com.google.android.gms.common.Scopes.DRIVE_APPFOLDER;

public class LoginActivity extends BaseActivity {
    private final String TAG = LoginActivity.class.getSimpleName();

    private ActivityLoginBinding loginBinding;

    //Google sign in
    private GoogleSignInOptions gso;
    public static GoogleSignInClient googleSignInClient;
    private String googleClientID;
    private final int RC_GOOGLE_SIGN_IN = 202;
    public String googleIdToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("User Sign In");

        googleLoginOnclick();
    }

    private void googleLoginOnclick() {
        googleClientID = BuildConfig.GOOGLE_SERVER_CLIENT_ID_DEBUG;
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
    }

    public void handleGoogleSignInResult(Task<GoogleSignInAccount> completeTask) {
        try {
            GoogleSignInAccount googleAccount = completeTask.getResult(ApiException.class);
            if (googleAccount.getIdToken() != null) {
                utility.showDebugLog(TAG, "Result OK. Username: " +
                        googleAccount.getDisplayName());
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

    }

    private void onLoggedInWithGoogle(GoogleSignInAccount account, boolean isSignedIn) {
        if (account != null && isSignedIn) {
            Intent googleCredentialIntent = new Intent(this, MyAccountActivity.class);
            googleCredentialIntent.putExtra("GOOGLE_CREDENTIALS", account);

            startActivity(googleCredentialIntent);
            this.finish();
        }
    }

    @Override
    public void onNetworkConnectionChanged(Boolean isConnected) {
        if (!isConnected) {
            utility.showToastMsg(getApplicationContext(),
                    "No Internet connection", Toast.LENGTH_LONG);
        }
    }
}
