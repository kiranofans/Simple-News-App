package com.android_projects.newsapipractice.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Repository.NewsArticleRepository;

import java.util.List;

public class NewsArticleViewModel extends AndroidViewModel {
    private NewsArticleRepository repository;

    public NewsArticleViewModel(@NonNull Application application) {
        super(application);

        //Initialize or create a new repository instance
        repository = new NewsArticleRepository(application);
    }

    /**This will be called in recyclerview*/
    public LiveData<List<Article>> getArticles(){
        return repository.getMutableLiveData();
    }

}
