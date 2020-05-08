package com.android_projects.newsapipractice;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android_projects.newsapipractice.Fragments.CategoriesFragment;
import com.android_projects.newsapipractice.Fragments.HomeFragment;
import com.android_projects.newsapipractice.Fragments.LocalFragment;
import com.android_projects.newsapipractice.Fragments.PopularFragment;
import com.android_projects.newsapipractice.databinding.ActivityMainBinding;
import com.android_projects.newsapipractice.databinding.NotificationBadgeLayoutBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import static com.android_projects.newsapipractice.data.AppConstants.BADGE_CHANNEL_ID;

public class MainActivity extends BaseActivity{
    private final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mainBinding;
    private final static String default_notification_channel_id = "default" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        //loading default fragment
        setFragments(new HomeFragment());
        //badgeNotification(getNotification(),1);

        mainBinding.mainBottomNavigation.setOnNavigationItemSelectedListener(mNavItemSelectedListener);
    }

    private Notification getNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                default_notification_channel_id);
        builder.setChannelId(BADGE_CHANNEL_ID);
        return builder.build();
    }


  /*  private String getDeviceLocationData(LocationManager locationMgr) {
        String locationResult = "";
        boolean isGranted = ContextCompat.checkSelfPermission(getContext(),coarseLocationPermission) == PackageManager.PERMISSION_GRANTED |
                ContextCompat.checkSelfPermission(getContext(),fineLocationPermission) == PackageManager.PERMISSION_GRANTED;
        Geocoder geocoder = new Geocoder(getContext());
        if(isGranted){
            String bestProvider = locationMgr.getBestProvider(locCriteria,false);

            locationMgr.requestLocationUpdates(bestProvider, 0, 0, (LocationListener)getContext());
            location = locationMgr.getLastKnownLocation(bestProvider);
            if(location != null){
                lat = location.getLatitude();
                lon = location.getLongitude();
                try {
                    if (geocoder != null) {
                        List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
                        Address address = addressList.get(0);
                        StringBuilder strBuilder = new StringBuilder();
                        strBuilder.append(address.getCountryCode());
                        address.getCountryName();
                        locationResult = strBuilder.toString();
                    }
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
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if(count == 0){
            super.onBackPressed();
        }else{
            getSupportFragmentManager().popBackStack();
        }
    }
}
