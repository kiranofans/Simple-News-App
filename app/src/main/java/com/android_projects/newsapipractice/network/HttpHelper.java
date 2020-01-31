package com.android_projects.newsapipractice.network;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class HttpHelper {

    public static <T> T fromJson(String jsonStr, Class<T> tClass){
        if(!TextUtils.isEmpty(jsonStr)){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            T obj = null;
            try{
                obj = gson.fromJson(jsonStr,tClass);
            }catch (Exception e){
                Log.d("Deserialize",e.getMessage());
            }
            return obj;
        }
        return null;
    }
    /*public <T> List<T> safeGetArrayList(List<T> list){
        return list.
    }*/
}
