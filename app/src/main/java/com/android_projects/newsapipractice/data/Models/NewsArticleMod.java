package com.android_projects.newsapipractice.data.Models;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android_projects.newsapipractice.Adapter.BaseModel;
import com.android_projects.newsapipractice.Adapter.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NewsArticleMod implements BaseModel, Serializable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("totalResults")
    @Expose
    private Integer totalResults;
    @SerializedName("articles")
    @Expose
    private List<Article> articles = null;

    public NewsArticleMod(){}
    public NewsArticleMod(Article article){
        article.getAuthor();
        article.getContent();
        article.getPublishedAt();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    //Get recyclerView data type
    @Override
    public int getViewType() {
        return Constants.ViewType.ARTICLE_TYPE;
    }
}
