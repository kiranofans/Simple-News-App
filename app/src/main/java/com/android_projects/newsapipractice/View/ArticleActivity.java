package com.android_projects.newsapipractice.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ActivityArticleBinding;
import com.bumptech.glide.Glide;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;

public class ArticleActivity extends BaseActivity {
    private final String TAG = ArticleActivity.class.getSimpleName();

    //Views
    private ActivityArticleBinding mBinding;

    private Article articleObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_article);
        articleObj = (Article) getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);

        getObjectExtra();
    }

    private void configureToolbar(Article articleObj) {
        //Set the back arrow button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(articleObj.getSource().getName());
        if (isAuthorEmpty(articleObj)) {//if author is null
            getSupportActionBar().setSubtitle(getString(R.string.article_author_not_available));
        } else {
            getSupportActionBar().setSubtitle("By " + articleObj.getAuthor());
        }
    }

    private void getObjectExtra() {
        mBinding.articleTvContent.setText(articleObj.getContent());
        mBinding.articleTvTitle.setText(articleObj.getTitle());
        configureToolbar(articleObj);
        Glide.with(this).load(articleObj.getUrlToImage()).into(mBinding.articleImgViewContent);

        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
        strBuilder.append(articleObj.getUrl());

        if (isContentEmpty(articleObj) && mBinding.articleTvContent != null) {
            mBinding.articleTvContent.setText(articleObj.getDescription());
            if (articleObj.getDescription() == null || articleObj.getDescription().equals("")) {
                mBinding.articleTvContent.setText(R.string.article_content_unavailable);
            }
        } else {
            mBinding.articleTvContent.setVisibility(View.VISIBLE);
        }

        mBinding.articleTvSourceLink.setOnClickListener((View v) -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(articleObj.getUrl())));
        });
    }

    private boolean isContentEmpty(Article obj) {
        if (obj.getContent() == null || obj.getContent().equals("")) {
            return true;
        }
        return false;
    }

    private boolean isAuthorEmpty(Article obj) {
        if (obj.getAuthor() == null || obj.getAuthor().equals("")) {
            return true;
        }
        return false;
    }

    @Override
    public void onNetworkConnectionChanged(Boolean isConnected) {
        utility.showNoNetworkUI(isConnected, mBinding.articleRootView,
                mBinding.noNetworkLayout.noNetworkContent);
    }
}
