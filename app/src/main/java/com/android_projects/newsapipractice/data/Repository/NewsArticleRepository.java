package com.android_projects.newsapipractice.data.Repository;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;
import com.android_projects.newsapipractice.network.Retrofit2Client;
import com.android_projects.newsapipractice.network.RetrofitApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android_projects.newsapipractice.network.APIConstants.API_KEY;

public class NewsArticleRepository {
    /**
     * Create a Repository class to interacting to LiveData
     * <p>
     * A data store for all of the application. It's the complete
     * data model for the app, which provides simple data modification & retrieval APIs
     **/

    //Source data
    private List<Article> articleList = new ArrayList<>();

    //Use mutableLiveData to fetch, sync, and persisting from different data sources
    private MutableLiveData<List<Article>> mutableLiveData = new MutableLiveData<>();

    private Application _application;

    public NewsArticleRepository(Application application) {
        //Initialize application context for the view model
        _application = application;
    }

    /**
     * Performing Api calls here
     * */
    public MutableLiveData<List<Article>> getMutableLiveData() {

        RetrofitApiService apiService = Retrofit2Client.getRetrofitService();

        Call<NewsArticleMod> callEverything = apiService.getEverything(API_KEY,
                20,"publishedAt");

        callEverything.enqueue(new Callback<NewsArticleMod>() {
            @Override
            public void onResponse(Call<NewsArticleMod> call, Response<NewsArticleMod> response) {
                NewsArticleMod newsArticles= response.body();

                if(newsArticles!= null){
                    //Use NewsArticleMod to get all articles,then assign the articles to List<Article>
                    /**So I'm api calling through the NewsArticleMod
                     * class and put the article data into Article typed List**/
                    articleList = newsArticles.getArticles();

                    //Convert the data source to mutable live data
                    mutableLiveData.setValue(articleList);
                }
            }

            @Override
            public void onFailure(Call<NewsArticleMod> call, Throwable t) {
                Toast.makeText(_application,"Failed to sync data",Toast.LENGTH_LONG).show();
            }
        });

        return mutableLiveData; //Return the data source as mutable live data
    }
}
