package com.android_projects.newsapipractice.network;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import static com.android_projects.newsapipractice.network.APIConstants.ENDPOINT_EVERYTHING;
import static com.android_projects.newsapipractice.network.APIConstants.ENDPOINT_TOP_HEADLINES;

public interface RetrofitApiService {
    String ENDPOINT_EVERYTHING ="v2/everything";

    @GET(ENDPOINT_EVERYTHING)
    Call<NewsArticleMod> getEverything(@Header("Authorization")String authorization, @Query("language") String language,
                                       @Query("domains") String domains, @Query("pageSize") int pageSize,
                                @Query("sortBy") String sort_by, @Query("page")int pageNumber);

   /* @GET(ENDPOINT_TOP_HEADLINES)
    Call<NewsArticleMod> getTopHeadlines(Header("Authorization")String authorization, @Query("language") String language,
                        @Query("q") String q, @Query("pageSize") int pageSize,
    @Query("sortBy") String sort_by, @Query("page")int pageNumber);*/
}
