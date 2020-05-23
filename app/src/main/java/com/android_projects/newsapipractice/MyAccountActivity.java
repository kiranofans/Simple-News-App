package com.android_projects.newsapipractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.android_projects.newsapipractice.databinding.ActivityMyAccountBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class MyAccountActivity extends AppCompatActivity {
    private ActivityMyAccountBinding accountBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountBinding= DataBindingUtil.setContentView(this,R.layout.activity_my_account);

        //Testing
        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra("GOOGLE_CREDENTIALS");
        Glide.with(this).load(googleSignInAccount.getPhotoUrl()).override(220,220).circleCrop()
                .into(accountBinding.accountImgviewUserAvatar);
    }
}
