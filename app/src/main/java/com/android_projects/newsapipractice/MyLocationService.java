package com.android_projects.newsapipractice;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.api.GoogleApiClient;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class MyLocationService extends BroadcastReceiver implements LocationListener{
    private final String TAG = MyLocationService.class.getSimpleName();

    //private static MyLocationListener mLocationListener;

    public static String LOCATION_BROADCAST_ACTION = "com.android_projects.newsapipractice.location_broadcast";
    public static final String BROADCAST_CONN_CHANGE="android.net.conn.CONNECTIVITY_CHANGE";

    private Location location;
    private LocationManager locationMgr;
    private Criteria locationCriteria = new Criteria();
    public static String countryCode,countryName,latitude;
    private double lat,lon;

    private String coarseLocationPerm = Manifest.permission.ACCESS_COARSE_LOCATION;
    private String fineLocationPerm = Manifest.permission.ACCESS_FINE_LOCATION;

    Intent locationIntent = new Intent(LOCATION_BROADCAST_ACTION);

    LocalBroadcastManager localBroadcastMgr;

    public MyLocationService(){}

    public MyLocationService(LocationManager locationMgr, Location location){
        this.locationMgr=locationMgr;
        this.location = location;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"Broadcast received; Lat: "+lat+"\nLon: "+lon);

        boolean isGranted = ContextCompat.checkSelfPermission(context, coarseLocationPerm) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, fineLocationPerm) == PackageManager.PERMISSION_GRANTED;
        localBroadcastMgr=LocalBroadcastManager.getInstance(context);
        locationMgr=(LocationManager)context.getSystemService(LOCATION_SERVICE);

        //mLocationListener=new MyLocationListener();

        String action = intent.getAction();
        if(action.equals(LOCATION_BROADCAST_ACTION) && action!=null){
            Toast.makeText(context,"Action is ok", Toast.LENGTH_SHORT).show();
            String bestProvider = locationMgr.getBestProvider(locationCriteria,false);
            Geocoder geocoder = new Geocoder(context);
            locationMgr.requestLocationUpdates(bestProvider,0,0,this);
            location = locationMgr.getLastKnownLocation(bestProvider);
            if(location!=null){
                //geocode(location,geocoder);
                //mLocationListener.onLocationChanged(location);
                lat = location.getLatitude(); lon =location.getLongitude();
              /*  lat = intent.getDoubleExtra("LATITUDE",0);
                lon = intent.getDoubleExtra("LONGITUDE",0);*/
                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocation(lat,lon,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addressList.get(0);
                countryCode = address.getCountryCode();
                countryName=address.getCountryName();
               //latitude = locationMgr+"";
               //setResult(102, address.getCountryName());
            }else{
                Log.d(TAG,"Location is "+location+"location manager is "+locationMgr);
            }

        }else{
            latitude=action;
            Log.d(TAG,"Action Error: "+action);
        }
    }

    private String geocode(Location location, Geocoder geocoder){
        String result = "";
        if(location!=null){
            if(geocoder!=null){
                try {
                    List<Address> addressList = geocoder.getFromLocation(lat,lon,1);
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

    private boolean isSameProvider(String networkProvider,String gpsProvider){
        if(networkProvider == null){
            return gpsProvider == null;
        }
        return networkProvider.equals(gpsProvider);
    }

   /* public class MyLocationListener implements android.location.LocationListener{
        MyLocationListener(){

        }

    }*/
 /*   public void geoCodeLocationData(Geocoder geocoder) throws IOException {
        //Geocoding location data using latitude and longitude
        if(geocoder!=null){
            List<Address> addressList = geocoder.getFromLocation(lat,lon,1);
            Address address=addressList.get(0);
            String result= address.getCountryCode();//Testing purpose
            Log.d(TAG,"Country Code: "+result+"\nCountry Name: "+countryName);
        }
    }*/


/*
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("STOP_SERVICE","DONE");
        locationMgr.removeUpdates(mLocationListener);
    }*/

    @Override
    public void onLocationChanged(Location location) {
        //location =locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if(location != null && locationIntent!=null){
            locationIntent.setAction(LOCATION_BROADCAST_ACTION);
            lat=location.getLatitude();
            lon = location.getLongitude();
            //latitude = location.getLatitude()+"";

            Log.d(TAG, "Latitude: " + lat + "\n" + "Longitude: " + lon);
            locationIntent.putExtra("LATITUDE",lat);
            locationIntent.putExtra("LONGITUDE",lon);
            locationIntent.putExtra("PROVIDER",location.getProvider());
            localBroadcastMgr.sendBroadcast(locationIntent);
            //latitude=location+"";
            //sendBroadcast(locationIntent);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(TAG, "status: " + bundle.toString());
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, provider+" enabled");

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, provider+" disabled");
    }
    public interface OnLocationDataGeoCodeListener{
        void onLocationDataGeocodeListener();
    }
}
