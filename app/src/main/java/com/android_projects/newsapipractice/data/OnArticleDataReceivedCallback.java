package com.android_projects.newsapipractice.data;

import com.android_projects.newsapipractice.data.Models.Article;

import java.util.List;

public interface OnArticleDataReceivedCallback {
    void onDataReceived(List<Article> articles);
}
