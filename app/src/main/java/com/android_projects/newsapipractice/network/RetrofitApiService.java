package com.android_projects.newsapipractice.network;

import com.android_projects.newsapipractice.data.Models.NewsArticleMod;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import static com.android_projects.newsapipractice.network.ApiConstants.ENDPOINT_EVERYTHING;
import static com.android_projects.newsapipractice.network.ApiConstants.ENDPOINT_SOURCES;
import static com.android_projects.newsapipractice.network.ApiConstants.ENDPOINT_TOP_HEADLINES;

public interface RetrofitApiService {
    @GET(ENDPOINT_EVERYTHING)
    Call<NewsArticleMod> getEverything(@Header("Authorization")String authorization,
                                       @QueryMap Map<String,String> requestParamsMap,
                        @Query("pageSize") int pageSize, @Query("page")int pageNumber);

    @GET(ENDPOINT_TOP_HEADLINES)
    Call<NewsArticleMod> getTopHeadlines(@Header("Authorization")String authorization,
                                       @QueryMap Map<String,String> requestParamsMap,
                                       @Query("pageSize") int pageSize, @Query("page")int pageNumber);

    @GET(ENDPOINT_SOURCES)
    Call<NewsArticleMod> getSources(@Header("Authorization")String authorization,
                                    @QueryMap Map<String,String> requestPramasMap);

}
