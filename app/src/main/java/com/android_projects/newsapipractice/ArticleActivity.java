package com.android_projects.newsapipractice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;
import com.android_projects.newsapipractice.databinding.ActivityArticleBinding;
import com.bumptech.glide.Glide;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;

public class ArticleActivity extends AppCompatActivity {

    private ActivityArticleBinding mBinding;

    private NewsArticleMod newsArticleMod;
    private Article articleMod = new Article();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_article);

        getObjectExtra();

    }

    private void configureToolbar(Article articleObj){
        getSupportActionBar().setTitle(articleObj.getTitle());
        getSupportActionBar().setSubtitle(articleObj.getAuthor());
    }
    private void getObjectExtra(){
        Article object = (Article) getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);

        mBinding.articleTvContentTitle.setText(object.getTitle());
        mBinding.articleAuthorTv.setText(object.getAuthor());
        mBinding.articleTvContent.setText(object.getContent());
        mBinding.articleTvDate.setText(object.getPublishedAt());

        configureToolbar(object);

        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
        int buildLength = strBuilder.length();
        strBuilder.append(object.getUrl());
        //strBuilder.setSpan(new StyleSpan());
        mBinding.articleTvSourceLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(object.getUrl())));
            }
        });

        Glide.with(this).load(object.getUrlToImage()).into(mBinding.articleImgViewContent);

    }

}
