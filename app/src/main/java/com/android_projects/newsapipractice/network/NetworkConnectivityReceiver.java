package com.android_projects.newsapipractice.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

public class NetworkConnectivityReceiver extends BroadcastReceiver {
    private final String TAG=NetworkConnectivityReceiver.class.getSimpleName();

    public static ConnectivityReceiverListener connectivityReceiverListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(connectivityReceiverListener!=null){
            connectivityReceiverListener.onNetworkConnectionChanged(isNetworkAvailable(context));
        }
    }

    private boolean isNetworkAvailable(Context context){
        if(context==null) return false;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connMgr!=null){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                NetworkCapabilities capability = connMgr.getNetworkCapabilities
                        (connMgr.getActiveNetwork());
                if (capability != null) {
                    if (capability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }  else if (capability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                        return true;
                    }
                }
            }else{
                try {
                    NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i(TAG, "Network availability : TRUE");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i(TAG, e.getMessage()+"\nCause: "+e.getCause());
                }
            }
        }
        Log.i("update_statut","Network availability: FALSE ");
        return false;
    }
    public interface ConnectivityReceiverListener{
        void onNetworkConnectionChanged(Boolean isConnected);
    }
}
