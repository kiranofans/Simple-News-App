package com.android_projects.newsapipractice.View.Managers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Used to handle 3 cases of permissions
 **/
public class PermissionManager {
    private final String TAG = PermissionManager.class.getSimpleName();

    public String externalStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public String coarseLocationPerm = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    public String fineLocationPerm = android.Manifest.permission.ACCESS_FINE_LOCATION;
    public String backgroundLocationPerm = Manifest.permission.ACCESS_BACKGROUND_LOCATION;
    public String[] locationPermissions={coarseLocationPerm,fineLocationPerm,backgroundLocationPerm};

    private Context _context;
    private SharedPrefManager sharedPrefMgr;

    public PermissionManager(Context context) {
        this._context = context;
        sharedPrefMgr = new SharedPrefManager(context);
    }

    public boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    private boolean shouldAskPermission(Context context, String permission) {
        if (shouldAskPermission()) {
            int permissionResult = ContextCompat.checkSelfPermission(context, permission);

            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    public void checkPermission(Context context, String permission, PermissionRequestListener listener) {
        if (shouldAskPermission(context, permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) context, permission)) {
                listener.onPermissionPreDenied();
            } else {
                if (sharedPrefMgr.isFirstTimeRequestPerm(permission)) {
                    sharedPrefMgr.firstTimePermRequest(permission, false);
                    listener.onNeedPermission();
                    Log.d(TAG, "onNeedPermission");
                } else {
                    listener.onPermissionPreDeniedWithNeverAskAgain();
                    Log.d(TAG, "onPermissionPreDeniedWithNeverAskAgain");
                }
            }
        } else {
            listener.onPermissionGranted();
            Log.d(TAG, "onPermissionGranted");
        }
    }

    public interface PermissionRequestListener {
        void onNeedPermission();

        void onPermissionPreDenied();

        void onPermissionPreDeniedWithNeverAskAgain();

        void onPermissionGranted();
    }
}
