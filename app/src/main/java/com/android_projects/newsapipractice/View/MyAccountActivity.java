package com.android_projects.newsapipractice.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.databinding.ActivityMyAccountBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import static com.android_projects.newsapipractice.View.LoginActivity.googleSignInClient;

public class MyAccountActivity extends AppCompatActivity {
    private final String TAG = MyAccountActivity.class.getSimpleName();

    private ActivityMyAccountBinding accountBinding;
    private GoogleSignInAccount googleSignInAccount;
    //private GoogleLoginViewModel googleLoginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_account);
        //googleLoginViewModel = new ViewModelProvider(this).get(GoogleLoginViewModel.class);

        //Google Account extra
        googleSignInAccount = getIntent().getParcelableExtra("GOOGLE_CREDENTIALS");
        getGoogleAccountData(googleSignInAccount);
        setCollapsedToolbar();
        logoutButton();
    }

    private void setCollapsedToolbar() {
        setSupportActionBar(accountBinding.accountAppBar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ViewCompat.setTransitionName(accountBinding.accountAppBar.appBarLayout, "");
        accountBinding.accountAppBar.collapsingToolbar.
                setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        accountBinding.accountAppBar.collapsingToolbar.
                setCollapsedTitleTextColor(ContextCompat.getColor(this, android.R.color.black));

    }

    private void getGoogleAccountData(GoogleSignInAccount account) {
        String username = account.getDisplayName();
        //String email = account.getEmail();

        accountBinding.accountAppBar.accountDisplayName.setText(username);
        Glide.with(this).load(account.getPhotoUrl()).override(220, 220).circleCrop()
                .into(accountBinding.accountAppBar.accountImgviewAvatar);
    }

    private void logoutButton() {
        accountBinding.accountAppBar.accountBtnLogout.setOnClickListener((View v) -> {
            googleSignInClient.signOut().addOnCompleteListener((Task<Void> task) -> {
                Intent intent = new Intent(MyAccountActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        });
    }
}
