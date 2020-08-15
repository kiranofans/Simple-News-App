package com.android_projects.newsapipractice.View;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.View.Fragments.MyAccountSettingsFragment;
import com.android_projects.newsapipractice.databinding.ActivityMyAccountBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class MyAccountActivity extends AppCompatActivity {
    private final String TAG = MyAccountActivity.class.getSimpleName();

    private ActivityMyAccountBinding accountBinding;
    private GoogleSignInAccount googleSignInAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_account);

        //Google Account extra
        googleSignInAccount = getIntent().getParcelableExtra("GOOGLE_CREDENTIALS");
        getGoogleAccountData(googleSignInAccount);
        setCollapsedToolbar();

        displayFragment(new MyAccountSettingsFragment());
        // logoutButton();
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

    private void displayFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.account_setting_frame, fragment);
            fragTrans.commit();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();//set the back arrow onClick event
        return true;
    }
}
