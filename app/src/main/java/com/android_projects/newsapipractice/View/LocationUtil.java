package com.android_projects.newsapipractice.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import com.android_projects.newsapipractice.R;

import java.text.DateFormat;
import java.util.Date;

public class LocationUtil {
    public static final String KEY_REQUEST_LOCATION_UPDATES="request_location_updates";

    static boolean requestLocationUpdates(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_REQUEST_LOCATION_UPDATES,false);
    }

    static void setRequestLocationUpdates(Context context,boolean requestLocationUpdates){
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(KEY_REQUEST_LOCATION_UPDATES,requestLocationUpdates).apply();
    }

    static String getLocationText(Location location){
        return location == null ? "Unknown location":"("+
                location.getLatitude()+", "+location.getLongitude()+")";
    }
    @SuppressLint("StringFormatInvalid")
    static String getLocationTitle(Context context){
        return context.getString(R.string.location_update, DateFormat.getDateTimeInstance().format(new Date()));
    }
}
