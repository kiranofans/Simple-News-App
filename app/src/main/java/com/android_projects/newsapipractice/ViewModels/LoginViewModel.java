package com.android_projects.newsapipractice.ViewModels;

import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginViewModel extends ViewModel {
    private final String TAG = LoginViewModel.class.getSimpleName();

    private MutableLiveData<String> id,emailAddress,profilePicUrl;
    private MutableLiveData<List<String>> allInfo;

    private String email, facebookId, picUrl;

    private List<String> profileInfos;


    public LoginViewModel() {
        //Initialize MutableLiveData
        id = new MutableLiveData<>();
        emailAddress = new MutableLiveData<>();
        profilePicUrl = new MutableLiveData<>();
        allInfo = new MutableLiveData<>();
    }

    public void setProfileInfo(String fbid, String email, String picURL, List<String> profileInfos){
        id.setValue(fbid);
        emailAddress.setValue(email);
        profilePicUrl.setValue(picURL);

        profileInfos.add(fbid); profileInfos.add(email);
        profileInfos.add(picURL);

        allInfo.setValue(profileInfos);
    }
    public void setId(String fbId){
        id.setValue(fbId);
    }

    public void setEmail(String email){
        emailAddress.setValue(email);
    }

    public void setProfilePicUrl(String profilePicURL){
        profilePicUrl.setValue(profilePicURL);
    }

    public LiveData<List<String>> loadFbUserProfile(AccessToken newAccessToken){
        profileInfos = new ArrayList<>();
        GraphRequest graphRequest = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.d("Request",response.getRawResponse());
                    email = object.getString("email");
                    facebookId = object.getString("id");
                    picUrl = "http://graph.facebook.com/" + facebookId + "/picture?type=normal";

                    id.setValue(facebookId); emailAddress.setValue(email);
                    profilePicUrl.setValue(picUrl);

                    profileInfos.add(facebookId); profileInfos.add(email);
                    profileInfos.add(picUrl);

                    allInfo.setValue(profileInfos);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields","email,id");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();

        return allInfo;
    }

    public LiveData<String> getFacebookId() {
        return id;
    }
    public LiveData<String> getEmail(){
        return emailAddress;
    }

    public LiveData<String> getProfilePicURL(){
        return profilePicUrl;
    }

    public LiveData<List<String>> getProfileInfo(){

        return allInfo;
    }
}
