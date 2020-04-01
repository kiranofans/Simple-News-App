package com.android_projects.newsapipractice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.LocationListener;
import android.util.Log;

import com.android_projects.newsapipractice.Fragments.LocalFragment;

import static com.android_projects.newsapipractice.MyLocationService.LOCATION_BROADCAST_ACTION;

public class MyLocationBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = MyLocationBroadcastReceiver.class.getSimpleName();

    public double lat,lon;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equalsIgnoreCase(LOCATION_BROADCAST_ACTION)){
            lat = intent.getDoubleExtra("LATITUDE",0);
            lon = intent.getDoubleExtra("LONGITUDE",0);
        }
        Log.d(TAG,"Lat "+lat+"\nLon: "+lon);

    }
}
