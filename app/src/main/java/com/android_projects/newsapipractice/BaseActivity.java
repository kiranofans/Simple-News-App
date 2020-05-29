package com.android_projects.newsapipractice;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android_projects.newsapipractice.Fragments.CategoriesFragment;
import com.android_projects.newsapipractice.Fragments.HomeFragment;
import com.android_projects.newsapipractice.Fragments.LocalFragment;
import com.android_projects.newsapipractice.Fragments.PopularFragment;
import com.android_projects.newsapipractice.Utils.Utility;
import com.android_projects.newsapipractice.databinding.ActivityMainBinding;
import com.android_projects.newsapipractice.databinding.NotificationBadgeLayoutBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import static com.android_projects.newsapipractice.LoginActivity.googleIdToken;

public class BaseActivity extends AppCompatActivity {
    private final String TAG = BaseActivity.class.getSimpleName();
    private ActivityMainBinding mainBinding;

    public String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
    public String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    private NotificationBadgeLayoutBinding badgeBinding;
    private BadgeDrawable badgeDrawable;

    private BottomNavigationMenuView bottomNavMenuView;
    private View notificationBadge;
    private boolean isBadgeVisible=false;
    private final int LOCATION_PERM_RC = 101;

    public static String countryCode="";
    public boolean isGranted;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        requestLocationPermission();
        getSupportActionBar().setIcon(android.R.drawable.stat_sys_headset);
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
                    return setFragments(fragment);
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.top_setting:
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.top_setting_language:
                return true;
            case R.id.top_setting_category:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void badgeNotification(Notification notification, int count){
        Intent notificationIntent = new Intent(this, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.BADGE_CHANNEL_IDS_NAME,1);
        notificationIntent.putExtra(MyNotificationPublisher.BADGE_NOTIFICATION_ID,notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,
                notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        setBadge(count,R.id.nav_home,pendingIntent);
        setBadge(count,R.id.nav_popular,pendingIntent);
        setBadge(count,R.id.nav_local,pendingIntent);

    }
    public void setBadge(int count, int resId, PendingIntent pendingIntent){
        badgeDrawable = mainBinding.mainBottomNavigation.getOrCreateBadge(resId);

        badgeDrawable.setNumber(count);
        if(badgeDrawable.getNumber()>0){
            badgeDrawable.setVisible(true);
        }else {
            badgeDrawable.setVisible(false);
            //badgeDrawable.setVisible(false);
        }
    }

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
        isGranted = ContextCompat.checkSelfPermission(this, coarseLocationPermission) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, fineLocationPermission) == PackageManager.PERMISSION_GRANTED;
        if (isGranted) {
            Log.d(TAG, "Permission granted!");
        } else {
            Log.d(TAG, "Permission denied!");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, new String[]{
                        coarseLocationPermission, fineLocationPermission}, LOCATION_PERM_RC);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utility utility=new Utility();
        GoogleSignInAccount alreadyLoggedGoogleAccount = GoogleSignIn.getLastSignedInAccount(getApplication());
        utility.isLoggedIn(alreadyLoggedGoogleAccount);

    }

}
