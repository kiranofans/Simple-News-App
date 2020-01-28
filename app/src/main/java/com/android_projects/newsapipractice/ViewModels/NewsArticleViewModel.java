package com.android_projects.newsapipractice.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.android_projects.newsapipractice.data.Repository.NewsArticleRepository;

public class NewsArticleViewModel extends AndroidViewModel {
    public NewsArticleViewModel(@NonNull Application application) {
        super(application);
    }
    private NewsArticleRepository repository;

}
