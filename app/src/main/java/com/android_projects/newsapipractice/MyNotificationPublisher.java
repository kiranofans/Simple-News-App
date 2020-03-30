package com.android_projects.newsapipractice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.android_projects.newsapipractice.data.AppConstants;

public class MyNotificationPublisher extends BroadcastReceiver {
    private final String TAG = MyNotificationPublisher.class.getSimpleName();

    public static final String BADGE_NOTIFICATION_ID="badge_notification";
    public static final String BADGE_CHANNEL_IDS_NAME="badge_notification_channel_name";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationMgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification=intent.getParcelableExtra(BADGE_NOTIFICATION_ID);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(AppConstants.BADGE_CHANNEL_ID,
                    BADGE_CHANNEL_IDS_NAME,importance);
            assert notificationMgr!=null;
            notificationMgr.createNotificationChannel(notificationChannel);
        }
        int id = intent.getIntExtra(BADGE_NOTIFICATION_ID,0);
        notificationMgr.notify(id,notification);
    }
}
