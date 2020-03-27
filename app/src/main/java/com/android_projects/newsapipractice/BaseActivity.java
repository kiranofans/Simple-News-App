package com.android_projects.newsapipractice;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android_projects.newsapipractice.Fragments.HomeFragment;
import com.android_projects.newsapipractice.Fragments.LocalFragment;
import com.android_projects.newsapipractice.Fragments.PopularFragment;
import com.android_projects.newsapipractice.Fragments.CategoriesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class BaseActivity extends AppCompatActivity implements LocationListener {
    private final String TAG = BaseActivity.class.getSimpleName();

    private LocationManager locationMgr;
    private final int RC_LOCATION_PERMISSION = 101;
    private Location location;

    private String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
    private String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxPermissions=new RxPermissions(this);
        checkPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.top_setting:
                return true;
            case R.id.top_setting_language:
                return true;
            case R.id.top_setting_category:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public BottomNavigationView.OnNavigationItemSelectedListener mNavItemSelectedListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            fragment = new HomeFragment();
                            break;
                        case R.id.nav_popular:
                            fragment = new PopularFragment();
                            break;
                        case R.id.nav_local:
                            fragment = new LocalFragment();
                            break;
                        case R.id.nav_categories:
                            fragment = new CategoriesFragment();
                            break;
                    }
                    return setFragments(fragment);
                }
            };

    public boolean setFragments(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, fragment, "Bottom Nav Fragments");
            fragTrans.commit();
            return true;
        }
        return false;
    }

    private void checkLocationPermission(){
        boolean isGranted = ContextCompat.checkSelfPermission(this, coarseLocationPermission) == PackageManager.PERMISSION_GRANTED |
                ContextCompat.checkSelfPermission(this, fineLocationPermission) == PackageManager.PERMISSION_GRANTED;
        if (!isGranted) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,coarseLocationPermission)){
                Toast.makeText(this,"You have to allow the app to get device location",Toast.LENGTH_LONG).show();
            }else{
                ActivityCompat.requestPermissions(this,new String[]{coarseLocationPermission,fineLocationPermission}, RC_LOCATION_PERMISSION);
            }
        }else{
            Log.d(TAG,"Permission Granted!");
            locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, BaseActivity.this);
            location = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            String lat = location.getLatitude()+"";
            String lon = location.getLongitude()+"";
            Log.d(TAG,"Latitude: "+lat+" Longitude: "+lon);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Latitude: "+location.getLatitude()+"\n"+
                "Longtitude: "+location.getLongitude());

    }

    public void checkPermission(){
        rxPermissions.request(coarseLocationPermission,fineLocationPermission).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if(isLocationPermissionGranted()){
                    // init view
                    //view.setVisibility(View.VISIBLE);
                    Toast.makeText(BaseActivity.this,"Permission granted!",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(BaseActivity.this,"Please allow the location permission",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public boolean isLocationPermissionGranted(){
        int coarseLocationPerm =this.checkCallingOrSelfPermission(coarseLocationPermission);
        int fineLocationPerm = this.checkCallingOrSelfPermission(fineLocationPermission);
        boolean isGranted = coarseLocationPerm == PackageManager.PERMISSION_GRANTED |
                fineLocationPerm == PackageManager.PERMISSION_GRANTED;
        return isGranted;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(TAG, "status: "+bundle.toString());
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "enabled: "+s);

    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "disabled: "+s);
    }
}
