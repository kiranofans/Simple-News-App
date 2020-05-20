package com.android_projects.newsapipractice.data.GoogleApiModel;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleSignInCredentials implements Serializable{

    @SerializedName("web")
    @Expose
    private Web web;

    public Web getWeb() {
        return web;
    }

    public void setWeb(Web web) {
        this.web = web;
    }

}
