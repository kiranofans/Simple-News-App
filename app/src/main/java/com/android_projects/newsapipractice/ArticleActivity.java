package com.android_projects.newsapipractice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.View;
import android.view.Window;

import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;
import com.android_projects.newsapipractice.databinding.ActivityArticleBinding;
import com.bumptech.glide.Glide;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;

public class ArticleActivity extends BaseActivity {
    private final String TAG = ArticleActivity.class.getSimpleName();

    private ActivityArticleBinding mBinding;
    private NewsArticleMod newsArticleMod;

    private Article articleObj;

    private final long FADE_DEFAULT_TIME= 1000;
    private final long MOVE_DEFAULT_TIME = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_article);
        articleObj = (Article) getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);

        setImgOnClick();
        getObjectExtra();

    }

    private void configureToolbar(Article articleObj){
        //Set the back arrow button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(articleObj.getSource().getName());
        if(isAuthorProvided(articleObj)){//if author is null
            getSupportActionBar().setSubtitle("By "+articleObj.getAuthor());
        }else{
            getSupportActionBar().setSubtitle(getString(R.string.article_author_not_available));
        }
    }

    private void getObjectExtra(){
        mBinding.articleTvContent.setText(articleObj.getContent());
        mBinding.articleTvTitle.setText(articleObj.getTitle());
        configureToolbar(articleObj);
        Glide.with(this).load(articleObj.getUrlToImage()).into(mBinding.articleImgViewContent);

        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
        int buildLength = strBuilder.length();
        strBuilder.append(articleObj.getUrl());

        if(isContentEmpty(articleObj) && mBinding.articleTvContent!=null){
            mBinding.articleTvContent.setText(articleObj.getDescription());
            if(articleObj.getDescription()==null||articleObj.getDescription()==""){
                mBinding.articleTvContent.setText(R.string.article_content_unavailable);
            }
        }else{
            mBinding.articleTvContent.setVisibility(View.VISIBLE);
        }

        mBinding.articleTvSourceLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(articleObj.getUrl())));
            }
        });
    }

    private void setImgOnClick(){
        mBinding.articleImgViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performTransition();
            }
        });
    }

    private void performTransition(){
        if(isDestroyed())return;

        ImageFragment imgFragment = new ImageFragment();
        String transName = ViewCompat.getTransitionName(mBinding.articleRootView);

        //Exit activity transition
        Fade exitFade = new Fade();
        exitFade.setDuration(FADE_DEFAULT_TIME);
        getWindow().setExitTransition(exitFade);
        //getWindow().setAllowEnterTransitionOverlap(true);

        TransitionSet enterTransSet = new TransitionSet();
        enterTransSet.addTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.move));
        enterTransSet.setDuration(MOVE_DEFAULT_TIME);
        enterTransSet.setStartDelay(FADE_DEFAULT_TIME);
        imgFragment.setSharedElementEnterTransition(enterTransSet);

        Fade enterFade = new Fade();
        enterFade.setDuration(FADE_DEFAULT_TIME);
        imgFragment.setEnterTransition(enterFade);
        imgFragment.setSharedElementReturnTransition(new Fade());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addSharedElement(mBinding.articleImgViewContent,transName)
                .replace(R.id.article_root_view,imgFragment).addToBackStack(null)
                .commit();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
