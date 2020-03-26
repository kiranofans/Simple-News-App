package com.android_projects.newsapipractice.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;
import com.android_projects.newsapipractice.data.Repository.NewsArticleRepository;
import com.android_projects.newsapipractice.network.Retrofit2Client;
import com.android_projects.newsapipractice.network.RetrofitApiService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

import static com.android_projects.newsapipractice.network.APIConstants.API_KEY;

public class NewsArticleViewModel extends AndroidViewModel {
    private NewsArticleRepository repository;
    private Call<NewsArticleMod> callApiData;

    private MutableLiveData<List<Article>> articleLiveData = new MutableLiveData<>();
    private RetrofitApiService apiService = Retrofit2Client.getRetrofitService();
    private Map<String,String> requestPramsMap = new HashMap<String,String>();

    private String domains = "foxnews.com,wsj.com,nytimes.com,ctvnews.ca,bbc.co.uk,techcrunch.com,engadget.com";
    String ENDPOINT_EVERYTHING ="v2/everything";
    String ENDPOINT_TOPHEADLINES="v2/topheadlines";
    String ENDPOINT_SOURCES="v2/sources";

    private final String LANGUAGE_ENGLISH="en";
    private final String LANGUAGE_SPANISH = "es";
    private final String LANGUAGE_FRENCH = "fr";

    public NewsArticleViewModel(@NonNull Application application) {
        super(application);

        //Initialize or create a new repository instance
        repository = new NewsArticleRepository(application);
    }

    public void getArticleListEverything(int page, String sortBy){
        callApiData = apiService.getEverything("Bearer "+API_KEY,requestPramsMap,50,page);
        requestPramsMap.put("sortBy",sortBy);
        requestPramsMap.put("domains",domains);
        requestPramsMap.put("language",LANGUAGE_ENGLISH);
        repository.getMutableLiveData(callApiData, data->articleLiveData.setValue(data));
    }

    public void getArticleListTopHeadlines(int page, String sortBy, String countryCode){
        requestPramsMap.put("sortBy",sortBy);
        requestPramsMap.put("country",countryCode);
        callApiData = apiService.getTopHeadlines("Bearer "+API_KEY,requestPramsMap,50,page);
        repository.getMutableLiveData(callApiData, data->articleLiveData.setValue(data));
    }

    public MutableLiveData<List<Article>> getArticleLiveData(){
        return articleLiveData;
    }

}
