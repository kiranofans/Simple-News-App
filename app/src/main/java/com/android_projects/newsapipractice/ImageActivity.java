package com.android_projects.newsapipractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ActivityImageBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import static com.android_projects.newsapipractice.LoginActivity.googleIdToken;
import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;

public class ImageActivity extends AppCompatActivity {
    private final String TAG = ImageActivity.class.getSimpleName();

    private ActivityImageBinding imgBinding;
    private Article articleMod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBinding= DataBindingUtil.setContentView(this,R.layout.activity_image);
        articleMod = (Article)getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);

        getSerializable();
        imgBottomButtons();
    }

    private void getSerializable(){
        if(!isObjNull()){
            configActionBar();
            Glide.with(this).load(articleMod.getUrlToImage()).into(imgBinding.fullImageView);
        }
    }

    private void imgBottomButtons(){
        imgBinding.imgBottomNav.imgBottomNavShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isObjNull()){
                    shareImage();
                }
            }
        });
        imgBinding.imgBottomNav.imgBottomNavDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(ImageActivity.this);
                downloadImage(account);
            }
        });
    }

    private void configActionBar(){
        setSupportActionBar(imgBinding.imgFragmentToolbar);
        getSupportActionBar().setTitle(articleMod.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void downloadImage(GoogleSignInAccount account){
        if(account!=null){
            Toast.makeText(getApplicationContext(),"Start downloading "+account.getIdToken(), Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Access Token is OK\n"+account.getIdToken());
        }else{
            //Got the idToken on May 24, 2020; will check this on another day
            Toast.makeText(getApplicationContext(),
                    "Sorry, You have to sign in to use this feature", Toast.LENGTH_LONG).show();
        }
        //download
    }

    private void shareImage(){
        Intent imgShareIntent = new Intent(Intent.ACTION_SEND);//same as intent.setAction();
        Uri imgUri = Uri.parse(articleMod.getUrlToImage());
        imgShareIntent.setType("*/*");
        imgShareIntent.putExtra(Intent.EXTRA_SUBJECT,articleMod.getPublishedAt());
        imgShareIntent.putExtra(Intent.EXTRA_TEXT,articleMod.getTitle()+"\n"+articleMod.getUrlToImage());
        imgShareIntent.putExtra(Intent.EXTRA_STREAM,imgUri);
        startActivity(Intent.createChooser(imgShareIntent,"Share Image Via"));
    }
    private boolean isObjNull(){
        if(articleMod==null){
            Log.d(TAG,"Article object is null");
            return true;
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();//set the back arrow onClick event
        return true;
    }
}
