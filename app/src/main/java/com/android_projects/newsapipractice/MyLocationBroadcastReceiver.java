package com.android_projects.newsapipractice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.util.Log;

import com.android_projects.newsapipractice.Fragments.LocalFragment;

import static com.android_projects.newsapipractice.MyLocationService.LOCATION_BROADCAST_ACTION;
import static com.android_projects.newsapipractice.MyLocationService.countryCode;

public class MyLocationBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = MyLocationBroadcastReceiver.class.getSimpleName();

    private MyLocationService locationService;
    private Location mLocation;

    public double lat,lon;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            context.startForegroundService(intent);

        }else{
            context.startService(intent);

        }
        String action = intent.getAction();
        if(action.equalsIgnoreCase(LOCATION_BROADCAST_ACTION)&& !action.isEmpty()){
            Log.d(TAG,"Is Action null: "+action.isEmpty());
            lat = intent.getExtras().getDouble("LATITUDE",0);
            lon = intent.getExtras().getDouble("LONGITUDE",0);
            countryCode=lat+"";
        }
        Log.d(TAG,"Lat "+lat+"\nLon: "+lon);

    }
}
