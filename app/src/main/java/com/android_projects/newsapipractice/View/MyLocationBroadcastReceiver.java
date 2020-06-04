package com.android_projects.newsapipractice.View;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import static com.android_projects.newsapipractice.View.MyLocationService.LOCATION_BROADCAST_ACTION;

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
           // countryCode=lat+"";
        }
        Log.d(TAG,"Lat "+lat+"\nLon: "+lon);

    }
}
