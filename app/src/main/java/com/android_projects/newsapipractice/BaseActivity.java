package com.android_projects.newsapipractice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class BaseActivity extends AppCompatActivity {
    private final String TAG = BaseActivity.class.getSimpleName();

    private String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
    private String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //rxPermissions = new RxPermissions(this);
        requestLocationPermission();
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

  /*  public BottomNavigationView.OnNavigationItemSelectedListener mNavItemSelectedListener = new
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
                    //setBadge(1,item.getItemId());
                    return setFragments(fragment);
                }
            };*/


    public boolean setFragments(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, fragment, "Bottom Nav Fragments");
            fragTrans.commit();
            return true;
        }
        return false;
    }

    private void requestLocationPermission() {
        boolean isGranted = ContextCompat.checkSelfPermission(this, coarseLocationPermission) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, fineLocationPermission) == PackageManager.PERMISSION_GRANTED;
        if (isGranted) {
            Log.d(TAG, "Permission granted!");
            //Get location data
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, new String[]{
                        coarseLocationPermission, fineLocationPermission}, 101);
            }
        }
    }

    /*@Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Latitude: " + location.getLatitude() + "\n" +
                "Longtitude: " + location.getLongitude() + "Country code: " + COUNTRY_CODE);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }*/
}
