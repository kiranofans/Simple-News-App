package com.android_projects.newsapipractice.ViewModels;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GoogleLoginViewModel extends AndroidViewModel {
    private final String TAG = GoogleLoginViewModel.class.getSimpleName();

    //private String email,username,avatarUrl;
    private MutableLiveData<List<String>> allInfoList;
    private List<String> profileInfoList;

    public GoogleLoginViewModel(@NonNull Application application) {
        super(application);
        allInfoList = new MutableLiveData<>();
    }

    public LiveData<List<String>> setProfileInfoList(String email,String username,String avatarUrl){
        profileInfoList =new ArrayList<>();
        profileInfoList.add(email);
        profileInfoList.add(username);
        profileInfoList.add(avatarUrl);

        allInfoList.setValue(profileInfoList);
        return allInfoList;
    }

    public LiveData<List<String>> loadGoogleAccountInfo(String idTokenStr){
        GoogleSignInAccount googleAccount= GoogleSignIn.getLastSignedInAccount(getApplication());
        if(idTokenStr==null){
            Log.d(TAG,"You are not logged in");
        }
        Log.d(TAG,"You are logged in with Google");
        return setProfileInfoList(googleAccount.getEmail(),googleAccount.getDisplayName(),
                googleAccount.getPhotoUrl().toString());
    }

    public LiveData<List<String>> loadFbUserProfile(AccessToken newAccessToken){
        profileInfoList = new ArrayList<>();
        GraphRequest graphRequest = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.d("Request",response.getRawResponse());
                    String email = object.getString("email");
                    String facebookId = object.getString("id");
                    String username = object.getString("username");
                    String picUrl = "http://graph.facebook.com/" + facebookId + "/picture?type=normal";

                    /* id.setValue(facebookId); emailAddress.setValue(email);
                    profilePicUrl.setValue(picUrl);

                    profileInfos.add(facebookId); profileInfos.add(email);
                    profileInfos.add(picUrl);*/

                    setProfileInfoList(email,username,picUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields","email,id,username");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();

        return allInfoList;
    }
   /* public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public LiveData<List<String>> getAllInfo() {
        return allInfoList;
    }

    public void setAllInfo(MutableLiveData<List<String>> allInfo) {
        this.allInfoList = allInfo;
    }*/
}
