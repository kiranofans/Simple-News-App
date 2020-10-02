package com.android_projects.newsapipractice.View.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.util.Log;

public class SharedPrefManager {
    private final String TAG = SharedPrefManager.class.getSimpleName();

    private Context _context;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPrefs;

    private final String KEY_PREF = "key_preferences";
    private final String KEY_PERMISSION_PREF = "key_permission_pref";
    private final String KEY_SWITCH_PREF = "key_switch_pref";

    public SharedPrefManager(Context context) {
        sharedPrefs = context.getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
        this._context = context;
    }

    public void firstTimePermRequest(String permission, boolean isFirstTime) {
        sharedPrefs = _context.getSharedPreferences(KEY_PERMISSION_PREF, Context.MODE_PRIVATE);
        if (editor == null) {
            editor = sharedPrefs.edit();
        }
        editor.putBoolean(permission, isFirstTime);
        editor.apply();//commit or apply
        editor = null;//clear editor
    }

    public boolean isFirstTimeRequestPerm(String permission) {
        return sharedPrefs.getBoolean(permission, true);
    }

    public void saveSwitchState(String switchKey, boolean isSwitchedOn) {
        sharedPrefs = _context.getSharedPreferences(KEY_SWITCH_PREF, Context.MODE_PRIVATE);
        if (editor == null) {
            editor = sharedPrefs.edit();
        }
        editor.putBoolean(switchKey, isSwitchedOn);
        editor.apply();
        Log.d(TAG, "Saved value: " + isSwitchedOn);
        editor = null;
    }

    public boolean getWifiSwitchState(String switchKey, WifiManager wifiManager) {
        sharedPrefs = _context.getSharedPreferences(KEY_SWITCH_PREF, Context.MODE_PRIVATE);
        boolean result = sharedPrefs.getBoolean(switchKey, true);

        if (result) {
            //In case if Wi-Fi is disabled and the switch is on, enable the Wi-Fi
            wifiManager.setWifiEnabled(true);
        }
        Log.d(TAG, "Load value: " + sharedPrefs.getBoolean(switchKey, true));
        return result;
    }

    public boolean getMobileDataSwitchState(String switchKey) {
        sharedPrefs = _context.getSharedPreferences(KEY_SWITCH_PREF, Context.MODE_PRIVATE);

        boolean result = sharedPrefs.getBoolean(switchKey, false);
        Log.d(TAG, "Load value: " + sharedPrefs.getBoolean(switchKey, false));
        return result;
    }
}
