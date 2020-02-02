package com.android_projects.newsapipractice;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;
import com.android_projects.newsapipractice.databinding.ActivityArticleBinding;
import com.android_projects.newsapipractice.network.HttpHelper;
import com.android_projects.newsapipractice.network.HttpRequestClient;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;
import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_SOURCE_ID;
import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_SOURCE_NAME;
import static com.android_projects.newsapipractice.network.APIConstants.API_KEY;
import static com.android_projects.newsapipractice.network.APIConstants.BASE_URL;
import static com.android_projects.newsapipractice.network.APIConstants.ENDPOINT_EVERYTHING;

public class ArticleActivity extends AppCompatActivity {

    private ActivityArticleBinding mBinding;

    private NewsArticleMod newsArticleMod;
    private Article articleMod,articleMod1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_article);

        getObjectExtra();

        articleMod = new Article();

    }

    private void getObjectExtra(){
        Article object = (Article) getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);

        mBinding.articleTvContentTitle.setText(object.getTitle());
        mBinding.articleAuthorTv.setText(object.getAuthor());
        mBinding.articleTvContent.setText(object.getContent());
        mBinding.articleTvDate.setText(object.getPublishedAt());

        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
        int buildLength = strBuilder.length();
        strBuilder.append(object.getUrl());
        //strBuilder.setSpan(new StyleSpan());
        mBinding.articleTvSourceLink.setText("Source: "+object.getUrl());

        Glide.with(this).load(object.getUrlToImage()).into(mBinding.articleImgViewContent);

    }

    class AsyncTaskLoadArticle extends AsyncTask<Void, Void, String> {
        private HttpRequestClient client;
        @Override
        protected String doInBackground(Void... voids) {
            client = new HttpRequestClient();
            return client.requestGETString(BASE_URL + ENDPOINT_EVERYTHING +
                    "?bitcoin&apiKey="+API_KEY);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String sourceID = getIntent().getExtras().getString(EXTRA_KEY_SOURCE_ID);
            String sourceName = getIntent().getExtras().getString(EXTRA_KEY_SOURCE_NAME);
            HttpHelper.fromJson(result, Article.class);

            JSONObject jsonObj = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            try {
                jsonArray = jsonObj.getJSONArray("articles");

                for(int i = 0; i<jsonArray.length();i++){
                    JSONObject arrayObj= jsonArray.getJSONObject(i);

                    String author = arrayObj.getString("author");
                    String publishDate = arrayObj.getString("publishedAt");
                    String content = arrayObj.getString("content");
                    JSONObject source = arrayObj.getJSONObject("source");
                    Object sourceIdObj = source.get("id");
                    String sourceNameObj = source.getString("name");

                    if(sourceID.equals(sourceIdObj) && sourceName.equals(sourceNameObj)){
                        mBinding.articleAuthorTv.setText(author);
                        mBinding.articleTvDate.setText(publishDate);
                        mBinding.articleTvContent.setText(content);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
