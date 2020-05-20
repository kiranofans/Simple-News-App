package com.android_projects.newsapipractice.data.Repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android_projects.newsapipractice.data.GoogleApiModel.GoogleSignInCredentials;
import com.android_projects.newsapipractice.data.GoogleApiModel.Web;
import com.android_projects.newsapipractice.data.GoogleSignInDataReceivedCallback;
import com.android_projects.newsapipractice.data.OnArticleDataReceivedCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleSignInRepo {
    private final String TAG = GoogleSignInRepo.class.getSimpleName();

    private MutableLiveData<Web> googleWebLiveData = new MutableLiveData<>();
    private Application _application;

    public GoogleSignInRepo(Application application){
        _application=application;
    }

    public MutableLiveData<Web> getMutableLiveData(Call<GoogleSignInCredentials> callCredentials,
                                                   GoogleSignInDataReceivedCallback dataReceivedCallback){
        callCredentials.enqueue(new Callback<GoogleSignInCredentials>() {
            @Override
            public void onResponse(Call<GoogleSignInCredentials> call, Response<GoogleSignInCredentials> response) {
                GoogleSignInCredentials googleSignInCredentialsMod = response.body();
                if(googleSignInCredentialsMod!=null){
                    Log.d(TAG, "onResponse: ");
                    dataReceivedCallback.onGoogleCredentialReceived(googleSignInCredentialsMod.getWeb());
                }
                Log.d("CHECK NULL", response.body()+" is null");
            }

            @Override
            public void onFailure(Call<GoogleSignInCredentials> call, Throwable t) {

            }
        });
        return  googleWebLiveData;
    }
}
