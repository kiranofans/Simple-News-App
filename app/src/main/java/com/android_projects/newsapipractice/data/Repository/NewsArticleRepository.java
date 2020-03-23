package com.android_projects.newsapipractice.data.Repository;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;
import com.android_projects.newsapipractice.data.OnArticleDataReceivedCallback;
import com.android_projects.newsapipractice.network.Retrofit2Client;
import com.android_projects.newsapipractice.network.RetrofitApiService;

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
    private final String TAG = NewsArticleRepository.class.getSimpleName();
    private final String LANGUAGE_ENGLISH="en";
    private final String LANGUAGE_SPANISH = "es";
    private final String LANGUAGE_FRENCH = "fr";

    //Use mutableLiveData to fetch, sync, and persisting from different data sources
    private MutableLiveData<List<Article>> mutableLiveData = new MutableLiveData<>();

    private Application _application;
    private SortByTypes sortByTypes;
    private String domains = "foxnews.com,wsj.com,nytimes.com,ctvnews.ca,bbc.co.uk,techcrunch.com,engadget.com";

    public NewsArticleRepository(Application application) {
        //Initialize application context for the view model
        _application = application;
    }

    private RetrofitApiService apiService = Retrofit2Client.getRetrofitService();

    public enum SortByTypes{
        PUBLISHED_AT,POPULARITY,RELEVANCY
    }

    /**
     * Performing Api calls here
     * */
    public MutableLiveData<List<Article>> getMutableLiveData(Call<NewsArticleMod> callEverything, int page,String sortBy,
                                                             OnArticleDataReceivedCallback dataReceivedCallback) {

        callEverything = apiService.getEverything("Bearer "+API_KEY,LANGUAGE_ENGLISH,
                domains,
                50,sortBy,page);

        callEverything.enqueue(new Callback<NewsArticleMod>() {
            @Override
            public void onResponse(Call<NewsArticleMod> call, Response<NewsArticleMod> response) {
                NewsArticleMod newsArticles= response.body();

                if(newsArticles!= null){
                    //Use NewsArticleMod to get all articles,then assign the articles to List<Article>
                    /**
                     * I'm api calling through the NewsArticleMod
                     * class and put the article data into Article typed List**/
                    //articleList = newsArticles.getArticles();

                    //Callback to return data to live data
                    Log.d(TAG, "onResponse: ");
                    dataReceivedCallback.onDataReceived(newsArticles.getArticles());
                    //Convert the data source to mutable live data
                }
                Log.d("CHECK NULL", response.body()+" is null");

            }

            @Override
            public void onFailure(Call<NewsArticleMod> call, Throwable t) {
                Toast.makeText(_application,"Failed to sync data",Toast.LENGTH_LONG).show();
            }
        });

        return mutableLiveData; //Return the data source as mutable live data
    }

/*    public String getSortByType(){
        String sortBy="";
        switch (sortByTypes){
            case PUBLISHED_AT:
                sortBy=SORT_BY_PUBLISHED_AT;
            break;

            case POPULARITY:
                sortBy=SORT_BY_POPULARITY;
            break;
            case RELEVANCY:
                sortBy=SORT_BY_RELEVANCY;
            break;
        }
        return sortBy;
    }*/
}
