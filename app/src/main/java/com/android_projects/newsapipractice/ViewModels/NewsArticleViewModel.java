package com.android_projects.newsapipractice.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android_projects.newsapipractice.BuildConfig;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;
import com.android_projects.newsapipractice.data.Repository.NewsArticleRepository;
import com.android_projects.newsapipractice.network.Retrofit2Client;
import com.android_projects.newsapipractice.network.RetrofitApiService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;


public class NewsArticleViewModel extends AndroidViewModel {
    private NewsArticleRepository repository;
    private Call<NewsArticleMod> callApiData;

    private MutableLiveData<List<Article>> articleLiveData = new MutableLiveData<>();

    private RetrofitApiService apiService = Retrofit2Client.getRetrofitService();
    private Map<String, String> requestPramsMap = new HashMap<String, String>();

    //Parameters
    private final String PARAMS_DOMAINS = "foxnews.com,wsj.com,nytimes.com,ctvnews.ca,bbc.co.uk,techcrunch.com,engadget.com";
    private final String API_KEY = BuildConfig.NEWS_API_KEY;
    private final String SORT_BY_POPULARITY = "popularity";

    public NewsArticleViewModel(@NonNull Application application) {
        super(application);

        //Initialize or create a new repository instance
        repository = new NewsArticleRepository(application);
    }

    public void getArticleListEverything(int page, String sortBy) {
        requestPramsMap.put("sortBy", sortBy);
        requestPramsMap.put("domains", PARAMS_DOMAINS);
        requestPramsMap.put("language", "en");
        callApiData = apiService.getEverything("Bearer " +
                API_KEY, requestPramsMap, 100, page);//100 is the maximum no matter how many pages
        //so need to add a footer when news meets 100 articles

        repository.getMutableLiveData(callApiData, data -> articleLiveData.setValue(data));
    }

    public void getArticleListPopular(int page) {
        requestPramsMap.put("sortBy", SORT_BY_POPULARITY);
        requestPramsMap.put("domains", PARAMS_DOMAINS);
        requestPramsMap.put("language", "en");
        callApiData = apiService.getEverything("Bearer " +
                API_KEY, requestPramsMap, 100, page);//100 is the maximum no matter how many pages
        //so need to add a footer when news meets 100 articles

        repository.getMutableLiveData(callApiData, data -> articleLiveData.setValue(data));
    }

    public void getArticleListTopHeadlines(int page, String sortBy, String countryCode) {
        requestPramsMap.clear();
        requestPramsMap.put("sortBy", sortBy);
        requestPramsMap.put("country", countryCode);
        callApiData = apiService.getTopHeadlines("Bearer " +
                API_KEY, requestPramsMap, 100, page);
        repository.getMutableLiveData(callApiData, data -> articleLiveData.setValue(data));
    }

    public void getAllArticles(int page, String sortBy, String countryCode) {
        getArticleListEverything(page, sortBy);
        getArticleListPopular(page);
        getArticleListTopHeadlines(page, sortBy, countryCode);
    }

    public MutableLiveData<List<Article>> getArticleLiveData() {
        return articleLiveData;
    }

}
