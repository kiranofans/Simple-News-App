package com.android_projects.newsapipractice.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.internal.connection.ConnectInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.android_projects.newsapipractice.network.ApiConstants.BASE_URL;

public class Retrofit2Client {

    private static Retrofit retrofit = null;

    public static synchronized RetrofitApiService getRetrofitService(){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30,TimeUnit.SECONDS);
        if(retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit.create(RetrofitApiService.class);
    }
}
