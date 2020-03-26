package com.android_projects.newsapipractice.Fragments;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.databinding.FragmentLocalBinding;

public class LocalFragment extends Fragment implements LocationListener {
    private final String TAG = LocalFragment.class.getSimpleName();

    private View v;
    private FragmentLocalBinding localBinding;

    private LocationManager locationMgr;
    private final int RC_LOCATION_PERMISSION =101;

    public LocalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        localBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_local, container, false);
        return v = localBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view = v, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle
                (view.getContext().getString(R.string.title_local_news));

    }

    private void checkLocationSelfPermission(View v){
        String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
        String fineLocationPermission=Manifest.permission.ACCESS_FINE_LOCATION;
        boolean isGranted = ContextCompat.checkSelfPermission(v.getContext(),coarseLocationPermission) == PackageManager.PERMISSION_GRANTED |
                ContextCompat.checkSelfPermission(v.getContext(),fineLocationPermission)==PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && !isGranted) {
            locationMgr = (LocationManager) v.getContext().getSystemService(Context.LOCATION_SERVICE);
            locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, LocalFragment.this);
        }else{
            //call getLocation()
            requestPermissions(new String[]{coarseLocationPermission,fineLocationPermission},RC_LOCATION_PERMISSION);
        }
    }
    private String getDeviceLocation(View v) {
        String locationResult = "";
        checkLocationSelfPermission(v);

        return "";
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Latitude: "+location.getLatitude()+"\n"+
                "Longtitude: "+location.getLongitude());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkLocationSelfPermission(v);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(TAG, "status: "+bundle.toString());
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "enabled: "+s);

    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "disabled: "+s);
    }
}
