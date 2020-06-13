package com.android_projects.newsapipractice.View;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.View.Fragments.HomeFragment;
import com.android_projects.newsapipractice.databinding.ActivityMainBinding;
import com.android_projects.newsapipractice.network.NetworkConnectivityReceiver;

public class MainActivity extends BaseActivity implements NetworkConnectivityReceiver.ConnectivityReceiverListener {
    private final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mainBinding;
    private final static String default_notification_channel_id = "default";

    //Network
    private NetworkConnectivityReceiver connReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        connReceiver = new NetworkConnectivityReceiver();
        registerReceiver(new NetworkConnectivityReceiver(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //loading default fragment
        setFragments(new HomeFragment());

        mainBinding.mainBottomNavigation.setOnNavigationItemSelectedListener(mNavItemSelectedListener);
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onNetworkConnectionChanged(Boolean isConnected) {
        showNoNetworkUI(isConnected);
    }

    private void showNoNetworkUI(Boolean isConnected) {
        if (!isConnected) {
            mainBinding.activityMainContent.setVisibility(View.GONE);
            mainBinding.noNetworkContent.noNetworkContent.setVisibility(View.VISIBLE);

        } else {
            mainBinding.activityMainContent.setVisibility(View.VISIBLE);
            mainBinding.noNetworkContent.noNetworkContent.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        connReceiver.connectivityReceiverListener = this;

    }
}
