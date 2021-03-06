package com.android_projects.newsapipractice.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.android_projects.newsapipractice.BuildConfig;
import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.View.MainActivity;
import com.android_projects.newsapipractice.data.Models.Article;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Utility {
    private final String TAG = Utility.class.getSimpleName();

    //Sign in check
    public boolean isLoggedInWithGoogle(Context context) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            Log.d(TAG, "Already logged in with Google");
            return true;
        } else {
            Log.d(TAG, "Not logged in yet");
            return false;
        }
    }
    public MainActivity initMainActivityObj(Activity activity, MainActivity mainActivity){
        if(activity instanceof MainActivity){
            mainActivity=(MainActivity) activity;
        }
        return mainActivity;
    }
    public void handleGoogleSignInResult(Task<GoogleSignInAccount> completeTask) {
        try {
            GoogleSignInAccount googleAccount = completeTask.getResult(ApiException.class);
            if (googleAccount.getIdToken() != null) {
                Log.d(TAG, "Sign in result ok");
                //Update UI
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //Update UI

        }
    }

    //Share content methods
    public void shareText(Article obj, Context context, String shareStr) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        //Pass your sharing content to the "putExtra" method of the Intent class
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, obj.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, obj.getTitle() + " " + obj.getUrl());
        context.startActivity(Intent.createChooser(shareIntent, shareStr));
    }

    public void twitterShare(Context context, Article obj) {
        //May apply webView later
        String twitterUrl = "https://twitter.com/intent/tweet?text=" +
                obj.getTitle() + "&url=" + obj.getUrl();
        Uri twitterUri = Uri.parse(twitterUrl);
        context.startActivity(new Intent(Intent.ACTION_VIEW, twitterUri));
    }

    /**
     * @param dateTimePattern
     * @return a new instance of SimpleDateFormat with a new Date()
     */
    public String getNewDateTimeFormat(String dateTimePattern) {
        //Convert source date time format to other format,
        return new SimpleDateFormat(dateTimePattern, Locale.ENGLISH).format(new Date());
    }

    /**
     * @param dateFormat
     * @param timeStamp
     * @return output date time string
     * Convert Zulu time zone to local time zone, switching to other date time format,
     * and parsing to today or yesterday strings using Joda-Time
     **/
    public String getFinalTimeStamp(Context context, String dateFormat, String timeStamp) {
        DateTime zuluDateTime;
        String outputDateStr;
        DateTime today = new DateTime();
        DateTime yesterday = today.minusDays(1);

        DateTimeFormatter formattedDate = DateTimeFormat.forPattern(dateFormat);
        zuluDateTime = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(timeStamp)
                .toDateTime(DateTimeZone.getDefault());//OK

        //New format to convert to today or yesterday
        DateTimeFormatter newDateTimeStr = DateTimeFormat.forPattern("hh:mm a");
        LocalDate zuluLocalDate = zuluDateTime.toLocalDate();
        if (zuluLocalDate.equals(today.toLocalDate())) {//Use zuluDateTime to compare
            outputDateStr = context.getString(R.string.date_time_today)
                    + " " + newDateTimeStr.print(zuluDateTime);
        } else if (zuluLocalDate.equals(yesterday.toLocalDate())) {
            outputDateStr = context.getString(R.string.date_time_yesterday)
                    + " " + newDateTimeStr.print(zuluDateTime);
        } else {
            //Otherwise, show converted customized date time format
            outputDateStr = formattedDate.print(zuluDateTime);
        }
        showDebugLog(TAG, outputDateStr);
        return outputDateStr;
    }

    /**
     * @param dateFormat
     * @return Date time string in local time zone
     * To convert new Date() to local time
     */
    public String getLocalDateTime(String dateFormat) {
        Date parsedDate = new Date();

        //Source date time string
        String dateTimeStr = getNewDateTimeFormat(dateFormat);
        SimpleDateFormat currentFormat = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        currentFormat.setTimeZone(TimeZone.getDefault());
        try {
            parsedDate = currentFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            showDebugLog(TAG, e.getMessage() + "\nCause: " + e.getCause());
        }
        return currentFormat.format(parsedDate);
    }

    //Permission check
    public void openAppSettings(Context context) {
        Intent settingIntent = new Intent();
        settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        settingIntent.setData(uri);
        settingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(settingIntent);
    }

    public void openSystemSettings(Context context) {
        Intent settingIntent = new Intent(Settings.ACTION_SETTINGS);
        settingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(settingIntent);
    }

    public void dialogToOpenSetting(Context context, String title, String message,
                                    String settingPerms, Button btn) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setCancelable(false)
                .setNegativeButton("NOT NOW", (DialogInterface dialogInterface, int i) -> {
                    dialogInterface.dismiss();
                }).setPositiveButton("GO TO SETTINGS", (DialogInterface dialogInterface, int i) ->
        {
            openAppSettings(context);
            dialogInterface.dismiss();
            if (isSettingAccessEnabled(context, settingPerms)) {
                changePermBtnTextIfShown(btn, context.getString(R.string.local_refresh));
            }
        }).show();
    }

    public void changePermBtnTextIfShown(Button btn, String text) {
        if (btn == null) {
            return;
        }
        btn.setText(text);
    }

    public boolean isSettingAccessEnabled(Context context, String settingPermission) {
        try {
            PackageManager packageMgr = context.getPackageManager();
            ApplicationInfo appInfo = packageMgr.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            //OPSTR_COARSE or FINE Location will detect location access from settings
            int mode = appOpsManager.checkOpNoThrow(settingPermission,
                    appInfo.uid, appInfo.packageName);

            Log.d(TAG, "is enabled: " + mode);

            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Permission Ignored");
            return false;
        }
    }

    /*public void showActivityPermissionRationale(Activity activityContext, String title, String msg, String[] permissionArray) {
      *//*  final View v = LayoutInflater.from(activityContext).inflate(R.layout.alert_dialog_layout, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(activityContext).create();*//*
        new AlertDialog.Builder(activityContext).setTitle(title)
                .setMessage(msg).setCancelable(false)
                .setNegativeButton("STILL DENY", (DialogInterface dialogInterface, int i) ->
                {
                    dialogInterface.dismiss();
                }).setPositiveButton("RETRY", (DialogInterface dialogInterface, int i) ->
        {
            ActivityCompat.requestPermissions(activityContext, permissionArray, 100);
            dialogInterface.dismiss();
        }).show();
    }*/

    public void showAlertDialog(Activity activityContext, String title, String msg) {
        new AlertDialog.Builder(activityContext).setTitle(title).setMessage(msg).setCancelable(false)
                .setPositiveButton("GO TO SETTINGS", (DialogInterface dialogInterface, int i) -> {
                    openSystemSettings(activityContext);
                }).setNegativeButton("CANCEL", (DialogInterface dialogInterface, int i) -> {
            dialogInterface.dismiss();
        }).show();
    }

    @SuppressLint("MissingPermission")
    public String getDeviceCountryCode(LocationManager locationMgr, Activity context) {
        String locationResult = "";
        Location bestLocation = null;
        double latitude = 0;
        double longitude = 0;

        //getProviders() returns list of only "enabled" location providers
        List<String> providers = (locationMgr != null) ? locationMgr.getProviders(true) : null;
        if (locationMgr != null) {
            for (String provider : providers) {
                Location location = locationMgr.getLastKnownLocation(provider);
                if (location == null) {
                    continue;
                }
                if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = location;
                }
            }
            if (bestLocation != null) {
                latitude = bestLocation.getLatitude();
                longitude = bestLocation.getLongitude();
                Geocoder geocoder = new Geocoder(context);
                try {
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    Address address = addressList.get(0);
                    locationResult = address.getCountryCode();//Testing purpose

                    Log.d(TAG, "Lat:" + latitude + "lon:" + longitude);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showAlertDialog(context, "Cannot Get Location Data",
                        "Please try to change your settings");

            }
        }

        return locationResult;
    }

    //Network handling
    public void showNoNetworkUI(Boolean isConnected, View activityMain, View noNetworkLayout) {
        if (!isConnected) {
            activityMain.setVisibility(View.GONE);
            noNetworkLayout.setVisibility(View.VISIBLE);

        } else {
            activityMain.setVisibility(View.VISIBLE);
            noNetworkLayout.setVisibility(View.GONE);
        }
    }

    //Others
    public void showToastMsg(Context context, String message, int length) {
        Toast.makeText(context, message, length).show();
    }

    public void showDebugLog(String LOG_TAG, String message) {
        Log.d(LOG_TAG, message);
    }

    /*public void showLongerToastMsg(Context context, String msg) {
        int durationInMilliSecs = 10000;
        Toast mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);

        new CountDownTimer(Math.max(durationInMilliSecs, 1000), 100*//* Tick duration *//*) {
            @Override
            public void onTick(long l) {
                mToast.show();
                mToast.getView().setOnClickListener((View v) -> {
                    mToast.cancel();
                });
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }*/

}
