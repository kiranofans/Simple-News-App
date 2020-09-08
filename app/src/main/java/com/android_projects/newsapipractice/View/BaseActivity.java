package com.android_projects.newsapipractice.View;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

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
    private PermissionManager permMgr;
    public final int ALL_PERMISSIONS = 100;
    public boolean isLocationPermGranted, isWriteExternalPermGranted;

    //Others
    public Utility utility;

    //Fragments handling
    private FragmentManager fragMgr = getSupportFragmentManager();
    private FragmentTransaction fragTrans;
    public final Fragment homeFragment = new HomeFragment();
    public final Fragment popularFragment = new PopularFragment();
    public final Fragment localFragment = new LocalFragment();
    public Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utility = new Utility();
        permMgr = new PermissionManager(this);
        connReceiver = new NetworkConnectivityReceiver();
        registerReceiver(new NetworkConnectivityReceiver(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        requestPermissions();
    }

    public BottomNavigationView.OnNavigationItemSelectedListener mNavItemSelectedListener
            = (@NonNull MenuItem item) -> {
        fragTrans = fragMgr.beginTransaction();
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
        fragMgr.beginTransaction().add(R.id.main_fragment_container, popularFragment).hide(popularFragment).commit();
        fragMgr.beginTransaction().add(R.id.main_fragment_container, localFragment).hide(localFragment).commit();
        fragMgr.beginTransaction().add(R.id.main_fragment_container, homeFragment).commit();
    }

    private void requestPermissions() {
        //Request permissions
        String externalStoragePerm = permMgr.externalStoragePermission;
        String[] permissionTypes = {externalStoragePerm, permMgr.coarseLocationPerm, permMgr.fineLocationPerm};

        isLocationPermGranted = ContextCompat.checkSelfPermission(this, permMgr.coarseLocationPerm) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, permMgr.fineLocationPerm) == PackageManager.PERMISSION_GRANTED;
        isWriteExternalPermGranted = ContextCompat.checkSelfPermission(this, externalStoragePerm)
                == PackageManager.PERMISSION_GRANTED;

        if (isLocationPermGranted && isWriteExternalPermGranted) {
            utility.showDebugLog(TAG, "All permissions granted!");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, permissionTypes, ALL_PERMISSIONS);
            }
        }
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
