package com.android_projects.newsapipractice.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android_projects.newsapipractice.data.GoogleApiModel.Web;
import com.android_projects.newsapipractice.data.Repository.GoogleSignInRepo;
import com.android_projects.newsapipractice.network.Retrofit2Client;
import com.android_projects.newsapipractice.network.RetrofitApiService;

public class GoogleSignInViewModel extends AndroidViewModel {
    private final String TAG = GoogleSignInViewModel.class.getSimpleName();

    private MutableLiveData<Web> credentialsLiveData = new MutableLiveData<>();
    private RetrofitApiService apiService = Retrofit2Client.getRetrofitService();

    private GoogleSignInRepo googleSignInRepo;
    private String email,avatarURL;

    public GoogleSignInViewModel(@NonNull Application application){
        super(application);
        googleSignInRepo = new GoogleSignInRepo(application);
    }

    public void getGoogleAccountCredentials(String authToken,String secrete){

    }

    public MutableLiveData<Web> getCredentialsLiveData(){
        return credentialsLiveData;
    }
}
