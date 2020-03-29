package com.android_projects.newsapipractice;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.List;

public abstract class MyLocationListener implements LocationListener {
    private final String TAG = MyLocationListener.class.getSimpleName();

    private Location location;
    private LocationManager locationManager;

    public MyLocationListener(LocationManager locationManager,Location location){
        this.locationManager = locationManager;
        this.location = location;
    }

   /* public String getDeviceLocation(){
        String locationResult = "";
        boolean isGranted = ContextCompat.checkSelfPermission(getContext(),coarseLocationPermission) == PackageManager.PERMISSION_GRANTED |
                ContextCompat.checkSelfPermission(getContext(),fineLocationPermission) == PackageManager.PERMISSION_GRANTED;
        Geocoder geocoder = new Geocoder(getContext());
        if(isGranted){
            String bestProvider = locationMgr.getBestProvider(locCriteria,false);
            *//*locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, BaseActivity.this);*//*
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener)getContext());
            location = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                lat = location.getLatitude();
                lon = location.getLongitude();
                try {
                    if (geocoder != null) {
                        List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
                        Address address = addressList.get(0);
                       *//* for(int i =0;i<providerList.size();i++){
                            location = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        }*//*
                        StringBuilder strBuilder = new StringBuilder();
                        strBuilder.append(address.getCountryCode());
                        address.getCountryName();
                        locationResult = strBuilder.toString();
                    }
                    //Toast.makeText(getContext(), "Permission granted!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Permission granted");
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage() + "Cause: " + e.getCause());
                }
            }else{
                localBinding.noContentLayout.noContentLayout.setVisibility(View.VISIBLE);
            }

        }
        Log.d(TAG, "Result: "+locationResult);
        return locationResult;
    }*/
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Latitude: " + location.getLatitude() + "\n" +
                "Longtitude: " + location.getLongitude());

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(TAG, "status: " + bundle.toString());
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "enabled: " + s);

    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "disabled: " + s);
    }
}
