package com.android_projects.newsapipractice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;
import com.android_projects.newsapipractice.databinding.ActivityArticleBinding;
import com.bumptech.glide.Glide;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;

public class ArticleActivity extends BaseActivity {

    private ActivityArticleBinding mBinding;

    private NewsArticleMod newsArticleMod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_article);

        getObjectExtra();

    }

    private void configureToolbar(Article articleObj){
        //Set the back arrow button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(articleObj.getSource().getName());
        if(isAuthorProvided(articleObj)){//if author is null
            getSupportActionBar().setSubtitle("Author: "+articleObj.getAuthor());
        }else{
            getSupportActionBar().setSubtitle(getString(R.string.article_author_not_available));
        }
    }

    private void getObjectExtra(){
        Article object = (Article) getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);

        mBinding.articleTvContent.setText(object.getContent());
        mBinding.articleTvTitle.setText(object.getTitle());

        configureToolbar(object);
        Glide.with(this).load(object.getUrlToImage()).into(mBinding.articleImgViewContent);

        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
        int buildLength = strBuilder.length();
        strBuilder.append(object.getUrl());

        if(isContentEmpty(object) && mBinding.articleTvContent!=null){
            mBinding.articleTvContent.setText(object.getDescription());
            if(object.getDescription()==null||object.getDescription()==""){
                mBinding.articleTvContent.setText(R.string.article_content_unavailable);
            }
        }else{
            mBinding.articleTvContent.setVisibility(View.VISIBLE);
        }

        mBinding.articleTvSourceLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(object.getUrl())));
            }
        });
    }

    private void setImgOnClick(){
        mBinding.articleImgViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    private boolean isContentEmpty(Article obj){
        if(obj.getContent()==null || obj.getContent()==""){
            return true;
        }
        return false;
    }
    private boolean isAuthorProvided(Article obj){
        if(obj.getAuthor()!=null || obj.getAuthor()!=""){
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
