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
import android.view.MenuItem;

import com.android_projects.newsapipractice.Fragments.HomeFragment;
import com.android_projects.newsapipractice.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity{
    private final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mainBinding;

    public static boolean isLoading=false;//To determine if load the data or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        //loading default fragment
        setFragments(new HomeFragment());
        mainBinding.mainBottomNavigation.setOnNavigationItemSelectedListener(mNavItemSelectedListener);
    }
    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 101){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && grantResults[0]!= PackageManager.PERMISSION_GRANTED){
                Log.d(TAG,"Permission denied");
                ActivityCompat.requestPermissions(this,new String[]{coarseLocationPermission,
                        fineLocationPermission},101);
            }
        }else{
            Log.d(TAG,"Pemission granted");
        }
    }*/


}
