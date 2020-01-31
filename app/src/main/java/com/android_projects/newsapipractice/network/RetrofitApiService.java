package com.android_projects.newsapipractice.network;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import static com.android_projects.newsapipractice.network.APIConstants.ENDPOINT_EVERYTHING;


public interface RetrofitApiService {

    @GET(ENDPOINT_EVERYTHING)
    Call<NewsArticleMod> getEverything(@Header("Authorization")String authorization,
                                       @Query("q") String q, @Query("pageSize") int pageSize,
                                @Query("sortBy") String sort_by, @Query("page")int pageNumber);
}
