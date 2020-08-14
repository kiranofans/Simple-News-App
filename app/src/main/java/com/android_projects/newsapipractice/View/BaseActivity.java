package com.android_projects.newsapipractice.View;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.Utils.Utility;
import com.android_projects.newsapipractice.View.Fragments.CategoriesFragment;
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
          /*  case R.id.nav_categories:
                fragment = new CategoriesFragment();
                break;*/
        }
        return setFragments(fragment);

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
           /* case R.id.top_setting_language:
                return true;
            case R.id.top_setting_category:
                return true;*/
            case R.id.top_setting_search:
                return true;
        }
        return super.onOptionsItemSelected(item);
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
