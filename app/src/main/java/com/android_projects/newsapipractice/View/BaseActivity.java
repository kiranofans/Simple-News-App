package com.android_projects.newsapipractice.View;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.Utils.Utility;
import com.android_projects.newsapipractice.View.Fragments.HomeFragment;
import com.android_projects.newsapipractice.View.Fragments.LocalFragment;
import com.android_projects.newsapipractice.View.Fragments.PopularFragment;
import com.android_projects.newsapipractice.View.Managers.PermissionManager;
import com.android_projects.newsapipractice.network.NetworkConnectivityReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity implements NetworkConnectivityReceiver.ConnectivityReceiverListener {
    private final String TAG = BaseActivity.class.getSimpleName();

    //Network
    private NetworkConnectivityReceiver connReceiver;

    //Permission
    public final int ALL_PERMISSIONS = 100;

    //Others
    public Utility utility;

    //Fragments handling
    private FragmentManager fragMgr = getSupportFragmentManager();
    public final Fragment homeFragment = new HomeFragment();
    public final Fragment popularFragment = new PopularFragment();
    public final Fragment localFragment = new LocalFragment();
    public Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utility = new Utility();
        connReceiver = new NetworkConnectivityReceiver();
        registerReceiver(new NetworkConnectivityReceiver(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public BottomNavigationView.OnNavigationItemSelectedListener mNavItemSelectedListener
            = (@NonNull MenuItem item) -> {
        FragmentTransaction fragTrans = fragMgr.beginTransaction();
        /* To switch fragments without loosing instance state, hide activeFragment and commit it,
        then set the current fragment as the active fragment */
        switch (item.getItemId()) {
            case R.id.nav_home:
                fragTrans.hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
                return true;
            case R.id.nav_popular:
                fragTrans.hide(activeFragment).show(popularFragment).commit();
                activeFragment = popularFragment;
                return true;
            case R.id.nav_local:
                fragTrans.hide(activeFragment).show(localFragment).commit();
                activeFragment = localFragment;
                return true;
        }
        return false;
    };

    public void setFragments() {
        //Hide all fragments EXCEPT FOR the fragment that will serve as a home fragment, then commit
        //This should be put below onCreate() in MainActivity in my case
        fragMgr.beginTransaction().add(R.id.main_fragment_container, localFragment).hide(localFragment).commit();//fragment 3
        fragMgr.beginTransaction().add(R.id.main_fragment_container, popularFragment).hide(popularFragment).commit();//fragment 2
        fragMgr.beginTransaction().add(R.id.main_fragment_container, homeFragment).commit();//fragment 1
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(hasAllPermissionsGranted(grantResults)){
            utility.showDebugLog(TAG, "All permissions granted!");
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                utility.showToastMsg(getApplicationContext(),permissions+" denied", Toast.LENGTH_LONG);
            }
        }
    }

    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults){
        for(int grantResult: grantResults){
            if(grantResult == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        utility.isLoggedInWithGoogle(getApplicationContext());
    }

    @Override
    public void onNetworkConnectionChanged(Boolean isConnected) {
        utility.showDebugLog(TAG, "Is network available:" + isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connReceiver.connectivityReceiverListener = this;
    }
}
