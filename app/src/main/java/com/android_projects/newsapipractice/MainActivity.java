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
    private NotificationBadgeLayoutBinding badgeBinding;
    private BadgeDrawable badgeDrawable;

    private BottomNavigationMenuView bottomNavMenuView;
    private View notificationBadge;
    private boolean isBadgeVisible=false;

    private final static String default_notification_channel_id = "default" ;

    public static boolean isLoading=false;//To determine if load the data or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        //loading default fragment
        setFragments(new HomeFragment());
        badgeNotification(getNotification(),1);

        mainBinding.mainBottomNavigation.setOnNavigationItemSelectedListener(mNavItemSelectedListener);
    }

    public BottomNavigationView.OnNavigationItemSelectedListener mNavItemSelectedListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            fragment = new HomeFragment();
                            //Once clicked count=0
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
                    badgeNotification(getNotification(),0);
                    return setFragments(fragment);
                }
            };

    public void setBadge(int count,int resId,PendingIntent pendingIntent){
        badgeDrawable = mainBinding.mainBottomNavigation.getOrCreateBadge(resId);

        badgeDrawable.setNumber(count);
        if(badgeDrawable.getNumber()>0){
            badgeDrawable.setVisible(true);
        }else {
            badgeDrawable.setVisible(false);
            //badgeDrawable.setVisible(false);
        }
    }

    private void badgeNotification(Notification notification,int count){
        Intent notificationIntent = new Intent(this, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.BADGE_CHANNEL_IDS_NAME,1);
        notificationIntent.putExtra(MyNotificationPublisher.BADGE_NOTIFICATION_ID,notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,
                notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        setBadge(count,R.id.nav_home,pendingIntent);
        setBadge(count,R.id.nav_popular,pendingIntent);
        setBadge(count,R.id.nav_local,pendingIntent);

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
}
