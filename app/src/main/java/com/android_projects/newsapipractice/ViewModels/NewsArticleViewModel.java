package com.android_projects.newsapipractice.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;
import com.android_projects.newsapipractice.data.Repository.NewsArticleRepository;

import java.util.List;

import retrofit2.Call;

public class NewsArticleViewModel extends AndroidViewModel {
    private NewsArticleRepository repository;
    private Call<NewsArticleMod> callEverything;

    private MutableLiveData<List<Article>> articleLiveData = new MutableLiveData<>();

    public NewsArticleViewModel(@NonNull Application application) {
        super(application);

        //Initialize or create a new repository instance
        repository = new NewsArticleRepository(application);
    }

    public void getArticlesList(int page){
        repository.getMutableLiveData(callEverything,page, data->articleLiveData.setValue(data));
    }

    public MutableLiveData<List<Article>> getArticleLiveData(){
        return articleLiveData;
    }

}
