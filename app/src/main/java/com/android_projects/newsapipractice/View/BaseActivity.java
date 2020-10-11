package com.android_projects.newsapipractice.View;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.Utils.Utility;
import com.android_projects.newsapipractice.View.Fragments.HomeFragment;
import com.android_projects.newsapipractice.View.Fragments.LocalFragment;
import com.android_projects.newsapipractice.View.Fragments.PopularFragment;
import com.android_projects.newsapipractice.network.ConnectivityReceiverListener;
import com.android_projects.newsapipractice.network.NetworkConnectivityReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

public class BaseActivity extends AppCompatActivity implements ConnectivityReceiverListener {
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

    //App update
    private final int APP_UPDATE_RC=120;
    public AppUpdateManager appUpdateMgr;
    public Task<AppUpdateInfo> updateInfoTask;
    public InstallStateUpdatedListener updateListener;
    private final int DAYS_FOR_FLEXIBLE_UPDATE =3;
    private final int MEDIUM_PRIORITY_UPDATE = 3;//update priority scaled 0 to 5

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContent();
        registerReceiver();
        handleUpdate();
    }

    private void initContent(){
        utility = new Utility();
        connReceiver = new NetworkConnectivityReceiver();
        appUpdateMgr = AppUpdateManagerFactory.create(BaseActivity.this);
        updateInfoTask = appUpdateMgr.getAppUpdateInfo();

        //Before starting an update, register a listener for updates
        appUpdateMgr.registerListener(updateListener);
    }

    private void handleUpdate(){
        utility.showDebugLog(TAG,"Checking for updates");
        checkUpdate(updateInfoTask);
    }

    private void requestFlexibleUpdate(AppUpdateManager appUpdateManager){
        try {
            AppUpdateInfo appUpdateInfo=appUpdateManager.getAppUpdateInfo().getResult();
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                    AppUpdateType.FLEXIBLE,this, APP_UPDATE_RC);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private void checkUpdate(Task<AppUpdateInfo> appUpdateInfoTask){
        if(appUpdateInfoTask!=null){
            appUpdateInfoTask.addOnSuccessListener((AppUpdateInfo appUpdateInfo)->{
                if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.clientVersionStalenessDays() != null
                        && appUpdateInfo.updatePriority()<= MEDIUM_PRIORITY_UPDATE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    requestFlexibleUpdate(appUpdateMgr);
                    utility.showToastMsg(BaseActivity.this, "Update available",
                            Toast.LENGTH_LONG);
                }else{
                    Log.d(TAG,"No update available");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==APP_UPDATE_RC){
            if(requestCode!= RESULT_OK){
                utility.showDebugLog(TAG,"Update flow failed! Result code: "+resultCode);
                requestFlexibleUpdate(appUpdateMgr);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(new NetworkConnectivityReceiver(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();//set the back arrow onClick event
        return true;
    }
}
