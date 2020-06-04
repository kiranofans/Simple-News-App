package com.android_projects.newsapipractice.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.List;

public class MyLocationService extends Service {
    private final String TAG = MyLocationService.class.getSimpleName();

    private final LocationServiceBinder locationServiceBinder = new LocationServiceBinder();

    public static String PACKAGE_NAME = "com.android_projects.newsapipractice";
    public static String LOCATION_BROADCAST_ACTION = PACKAGE_NAME+"location_broadcast";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION= PACKAGE_NAME+".started_from_notification";
    private final int LOCATION_NOTIFICATION_ID = 12345678;
    public static final String BROADCAST_CONN_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    private Location mLocation;
    private LocationManager locationMgr = null;
    private MyLocationListener mLocationListener;

    public static String countryName;
    public static double latitude;
    private double lat, lon;

    private final int LOCATION_FOREGROUND_ID = 123456;
    private String coarseLocationPerm = Manifest.permission.ACCESS_COARSE_LOCATION;
    private String fineLocationPerm = Manifest.permission.ACCESS_FINE_LOCATION;

    public Intent locationIntent;

    LocalBroadcastManager localBroadcastMgr;

    private final String LOCATION_CHANNEL_ID = "location_channel_01";
    private final String LOCATION_CHANNEL_NAME = "location_channel";

    private Activity _activity;
    private boolean isConfigurationChanged=false;
    private final IBinder locationServiceBinder1 = new LocationServiceBinder();

    public MyLocationService() {
    }

    public MyLocationService(Activity activity){
        this._activity=activity;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"in onBind()");
        stopForeground(true);
        isConfigurationChanged=false;
        return locationServiceBinder1;

    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG,"in onRebind()");
        stopForeground(true);
        isConfigurationChanged=false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");
        if (!isConfigurationChanged && LocationUtil.requestLocationUpdates(this)) {
            Log.i(TAG, "Starting foreground service");
            startForeground(LOCATION_NOTIFICATION_ID, getNotification());
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        initLocationManager(getApplicationContext());
        startTracking();
        startForeground(LOCATION_FOREGROUND_ID, getNotification());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isConfigurationChanged=true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e(TAG, "onStartCommand");
        startForeground(LOCATION_FOREGROUND_ID, getNotification());

        // Tells the system to not try to recreate the service after it has been killed
        return START_NOT_STICKY;
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new MyLocationListener(LocationManager.PASSIVE_PROVIDER)
    };

    private Notification getNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(),MyLocationService.class);

        String text = LocationUtil.getLocationText(mLocation);
        notificationIntent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION,true);

       /* PendingIntent servicePendingIntent = PendingIntent.getService(getApplicationContext(),
                0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);*/

        Notification.Builder builder;
        NotificationManager notificationMgr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Notification channel is not needed before Android 8.0 (Oreo)
            NotificationChannel channel = new NotificationChannel(
                    LOCATION_CHANNEL_ID, LOCATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationMgr = getSystemService(NotificationManager.class);
            notificationMgr.createNotificationChannel(channel);
            builder = new Notification.Builder(getApplicationContext(), LOCATION_CHANNEL_ID);
        }
        builder = new Notification.Builder(getApplicationContext())
                    .setPriority(Notification.PRIORITY_HIGH).setWhen(System.currentTimeMillis());

        //builder.setContentText(getLastKnownLocation().toString());
        return builder.build();
    }

    @SuppressLint("missingPermission")
    public String startTracking() {
        String result = "";
        initLocationManager(getApplicationContext());

        //mLocationListener = new MyLocationListener(LocationManager.PASSIVE_PROVIDER);
        try {
            locationMgr.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,0,0,mLocationListeners[0]);
            mLocation = getLastKnownLocation(getApplicationContext());

            //geocode(mLocation,new Geocoder(this));
            countryName = mLocation + " MyLocation";
        } catch (SecurityException e) {
            Log.i(TAG, "Failed to request location update,ignore", e);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "GPS or Network provider does not exist " + e.getMessage()
                    + "\nCaused by " + e.getCause());
        }
        return result=geocode(mLocation,new Geocoder(this));
    }

    @SuppressLint("MissingPermission")
    public Location getLastKnownLocation(Context context) {
        initLocationManager(context);
        List<String> providerList = locationMgr.getProviders(false);
        Location bestLocations = null;
        for (String provider : providerList) {
            Location location = locationMgr.getLastKnownLocation(provider);
            Log.d(TAG, provider);
            if (location == null) {
                continue;
            }
            if (bestLocations == null || location.getAccuracy() < bestLocations.getAccuracy()) {
                Log.d(TAG, "Location: " + location);
                bestLocations = location;
            }
        }
        if (bestLocations == null) {
            return null;
        }
        return bestLocations;
    }

    public void stopTracking() {
        this.onDestroy();
    }

    private void initLocationManager(Context context) {
        if (locationMgr == null) {
            locationMgr = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        }
    }

    public String geocode(Location location, Geocoder geocoder) {
        String result = "";
        if (location != null) {
            if (geocoder != null) {
                try {
                    List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
                    Address address = addressList.get(0);
                    StringBuilder strBuilder = new StringBuilder();
                    result = strBuilder.append(address.getCountryCode()).toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private class MyLocationListener implements LocationListener {
        private final String LOG_TAG = MyLocationListener.class.getSimpleName();

        private Location currentLocation = null;
        private Location mCurrentLocation;

        public MyLocationListener() {
        }

        public MyLocationListener(String provider) {
            Log.e(LOG_TAG, "LocationListener provider: " + provider);
            mCurrentLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            locationIntent = new Intent(getApplicationContext(),MyLocationBroadcastReceiver.class);
            locationIntent.setAction(LOCATION_BROADCAST_ACTION);
            //Bundle bundle = new Bundle();
            if (location != null) {
                this.mCurrentLocation = location;
                Log.d(TAG, "LocationChanged: " + location);
                Log.d(TAG, "Latitude:" + location.getLatitude() + "\nLongitude: " + location.getLongitude());
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                //countryCode = lat + "";
                locationIntent.putExtra("LATITUDE",lat);
                locationIntent.putExtra("LONGITUDE",lng);
                /*bundle.putDouble("LATITUDE",lat);
                bundle.putDouble("LONGITUDE",lng);*/
               sendBroadcast(locationIntent);


            } else {
                Log.d(TAG, "Location is " + location);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d(LOG_TAG, "status: " + bundle.toString());
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(LOG_TAG, provider + " enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(LOG_TAG, provider + " disabled");
        }
    }

    public boolean isServiceRunningInForeground(Context context){
        ActivityManager activityMgr = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : activityMgr.getRunningServices(Integer.MAX_VALUE)){
            if(getClass().getName().equals(service.service.getClassName())){
                if(service.foreground){
                    return true;
                }
            }
        }
        return false;
    }

    public class LocationServiceBinder extends Binder {
        public MyLocationService getService() {
            return MyLocationService.this;
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (locationMgr != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                            (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationMgr.removeUpdates(mLocationListeners[i]);
                } catch (Exception e) {
                    Log.i(TAG, "Failed to remove location listeners,ignore", e);
                }
            }
        }
    }
}
