package com.android_projects.newsapipractice.View;

import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Notification;
import android.os.Bundle;

import com.android_projects.newsapipractice.View.Fragments.HomeFragment;
import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.databinding.ActivityMainBinding;

import static com.android_projects.newsapipractice.data.AppConstants.BADGE_CHANNEL_ID;

public class MainActivity extends BaseActivity{
    private final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mainBinding;
    private final static String default_notification_channel_id = "default" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

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
