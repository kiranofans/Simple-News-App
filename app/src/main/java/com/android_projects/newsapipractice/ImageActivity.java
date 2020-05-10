package com.android_projects.newsapipractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ActivityImageBinding;
import com.bumptech.glide.Glide;

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
    }

    private void getSerializable(){
        if(!isObjNull()){
            configActionBar();
            Glide.with(this).load(articleMod.getUrlToImage()).into(imgBinding.fullImageView);
        }
    }

    private void configActionBar(){
        setSupportActionBar(imgBinding.imgFragmentToolbar);
        getSupportActionBar().setTitle(articleMod.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
