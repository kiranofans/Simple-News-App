package com.android_projects.newsapipractice.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

public class NetworkConnectivityReceiver extends BroadcastReceiver {
    private final String TAG = NetworkConnectivityReceiver.class.getSimpleName();

    public static ConnectivityReceiverListener connectivityReceiverListener;

    private ConnectivityManager connMgr;
    private boolean isNetworkOk;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isNetworkAvailable(context));
        }
    }

    private boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capability = connMgr.getNetworkCapabilities
                        (connMgr.getActiveNetwork());
                if (capability != null) {
                    if (capability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                withNetworkCallback();
            }else{
                try {
                    NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i(TAG, "Network availability : TRUE");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i(TAG, e.getMessage() + "\nCause: " + e.getCause());
                }
            }
        }
        Log.i(TAG, "Network availability: FALSE ");
        return false;
    }

    private void withNetworkCallback(){
        NetworkRequest netRequest = new NetworkRequest.Builder().addCapability
                (NetworkCapabilities.NET_CAPABILITY_INTERNET).build();

        connMgr.registerNetworkCallback(netRequest,new ConnectivityManager.NetworkCallback(){
            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities capabilities) {
                super.onCapabilitiesChanged(network, capabilities);
                isNetworkOk=capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                         capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                Log.d(TAG,"NetworkCallback: Network capability: "+isNetworkOk);
            }

            @Override
            public void onLosing(@NonNull Network network, int maxMsToLive) {
                super.onLosing(network, maxMsToLive);
                Log.d(TAG,"NetworkCallback -> Internet connection is losing...\n" +
                        "Max milliseconds to live: "+maxMsToLive);
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                Log.d(TAG,"NetworkCallback -> Internet connection lost");
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                Log.d(TAG,"NetworkCallback -> Internet connection is unavailable");
            }

            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Log.d(TAG,"Network connection available");
            }
        });
    }
}
