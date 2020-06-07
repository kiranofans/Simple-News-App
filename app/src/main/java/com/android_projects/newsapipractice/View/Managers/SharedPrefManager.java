package com.android_projects.newsapipractice.View.Managers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private final String TAG = SharedPrefManager.class.getSimpleName();
    private Context _context;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPrefs;

    private final String KEY_PREF="key_preferences";
    private final String KEY_PERMISSION_PREF="key_permission_pref";
    public SharedPrefManager(Context context){
        sharedPrefs = context.getSharedPreferences(KEY_PREF,Context.MODE_PRIVATE);
        this._context=context;
    }

    public void firstTimePermRequest(String permission, boolean isFirstTime){
        sharedPrefs=_context.getSharedPreferences(KEY_PERMISSION_PREF,Context.MODE_PRIVATE);
        if(editor==null){
            editor=sharedPrefs.edit();
        }
        editor.putBoolean(permission, isFirstTime);
        editor.apply();// commit or apply
        editor=null;//clear editor
    }

    public boolean isFirstTimeRequestPerm(String permission){
        return sharedPrefs.getBoolean(permission,true);
    }

}
