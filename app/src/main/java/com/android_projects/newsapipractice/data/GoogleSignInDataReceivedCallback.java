package com.android_projects.newsapipractice.data;

import com.android_projects.newsapipractice.data.GoogleApiModel.Web;

public interface GoogleSignInDataReceivedCallback {
    void onGoogleCredentialReceived(Web credentials);

}
