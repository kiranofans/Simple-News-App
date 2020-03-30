package com.android_projects.newsapipractice;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android_projects.newsapipractice.Fragments.HomeFragment;
import com.android_projects.newsapipractice.databinding.ActivityMainBinding;
import com.android_projects.newsapipractice.databinding.NotificationBadgeLayoutBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity{
    private final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mainBinding;
    private NotificationBadgeLayoutBinding badgeBinding;

    private ViewGroup viewGroup;

    private BottomNavigationMenuView bottomNavMenuView;
    private View notificationBadge;
    private boolean isBadgeVisible=false;

    public static boolean isLoading=false;//To determine if load the data or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
       /* badgeBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.notification_badge_layout,viewGroup,false);*/

        //loading default fragment
        setFragments(new HomeFragment());
       addBadge(1);
        mainBinding.mainBottomNavigation.setOnNavigationItemSelectedListener(mNavItemSelectedListener);
    }

    private View getNotificationBadge(){
        if(notificationBadge!=null){
            return notificationBadge;
        }
        bottomNavMenuView = (BottomNavigationMenuView) mainBinding.mainBottomNavigation.getChildAt(0);
        notificationBadge=LayoutInflater.from(this).inflate(R.layout.notification_badge_layout,bottomNavMenuView,false);
        return notificationBadge;
    }
    private void addBadge(int count){

        BadgeDrawable badgeDrawable = mainBinding.mainBottomNavigation.getOrCreateBadge(R.id.nav_home);
        badgeDrawable.setNumber(count);
        badgeDrawable.setVisible(true);
    }

    private void removeBadge(){
        mainBinding.mainBottomNavigation.removeBadge(R.id.nav_home);
    }

    private void refreshBadgeView(){
        //If bage is not visible
        boolean isBadgeVisible = notificationBadge.getVisibility()!=View.VISIBLE;
        notificationBadge.setVisibility(View.GONE);
    }
}
