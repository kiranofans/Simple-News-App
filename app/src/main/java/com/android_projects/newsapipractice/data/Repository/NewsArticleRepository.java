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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsArticleRepository {
    /**
     * Create a Repository class to interacting to LiveData
     * <p>
     * A data store for all of the application. It's the complete
     * data model for the app, which provides simple data modification & retrieval APIs
     **/
    private final String TAG = NewsArticleRepository.class.getSimpleName();

    //Use mutableLiveData to fetch, sync, and persisting from different data sources
    private MutableLiveData<List<Article>> mutableLiveData = new MutableLiveData<>();

    private Application _application;

    public NewsArticleRepository(Application application) {
        //Initialize application context for the view model
        _application = application;
    }

    // Performing Api calls here
    public MutableLiveData<List<Article>> getMutableLiveData(Call<NewsArticleMod> callEverything,
                                                             OnArticleDataReceivedCallback dataReceivedCallback) {
       //Enqueue is Asynchronous
        callEverything.enqueue(new Callback<NewsArticleMod>() {
            @Override
            public void onResponse(Call<NewsArticleMod> call, Response<NewsArticleMod> response) {
                NewsArticleMod newsArticles= response.body();
                if(newsArticles!= null){
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
}
