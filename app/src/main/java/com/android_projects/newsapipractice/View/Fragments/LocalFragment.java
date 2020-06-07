package com.android_projects.newsapipractice.View.Fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.Utils.Utility;
import com.android_projects.newsapipractice.View.Adapter.NewsRecyclerViewAdapter;
import com.android_projects.newsapipractice.View.Managers.PermissionManager;
import com.android_projects.newsapipractice.View.PaginationListener;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.FragmentLocalBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class LocalFragment extends Fragment {
    private final String TAG = LocalFragment.class.getSimpleName();

    private Utility utility;

    private View v;
    private FragmentLocalBinding localBinding;
    private NewsArticleViewModel localNewsViewModel;

    private LocationManager locationMgr;
    private PermissionManager permMgr;

    private final String SORT_BY_PUBLISHED_AT = "publishedAt";
    private int currentPageNum = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean isPermissionGranted = true;

    private NewsRecyclerViewAdapter recViewAdapter;
    private LinearLayoutManager layoutManager;

    private String[] locationPermissions;

    private List<Article> localNewsList = new ArrayList<>();

    public LocalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        localBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_local, container, false);
        utility = new Utility();
        permMgr = new PermissionManager(getContext());
        locationPermissions = new String[]{permMgr.coarseLocationPerm, permMgr.fineLocationPerm};

        return v = localBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        localNewsViewModel = ViewModelProviders.of(getActivity()).get(NewsArticleViewModel.class);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(view.getContext()
                .getString(R.string.title_local_news));

        setRecyclerView(view);
        checkLocationPermissionResults(locationPermissions);//Permission granted,display content
        onScrollListener();
    }

    private void swipeToRefreshListener() {
        localBinding.localSwipeRefreshLayout.setRefreshing(true);
        localBinding.localSwipeRefreshLayout.setEnabled(true);
        localBinding.localSwipeRefreshLayout.setOnRefreshListener(() -> {
            currentPageNum = 1;
            recViewAdapter.clear();
            loadPage(currentPageNum);
        });
    }

    private void setObserver() {
        localNewsViewModel.getArticleLiveData().observe(getActivity(), new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                isLoading = false;
                localNewsList.addAll(articles);
                Log.d(TAG, "onChanged: " + localNewsList.size());
                localBinding.localSwipeRefreshLayout.setRefreshing(false);
                recViewAdapter.notifyDataSetChanged();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void loadPage(int page) {
        Log.d(TAG, "API called " + page);
        localNewsViewModel.getArticleListTopHeadlines(page, SORT_BY_PUBLISHED_AT, getDeviceCountryCode());
    }

    private void onScrollListener() {
        localBinding.mainLocalRecyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;//make the isLoading true again, so it is false
                currentPageNum++;
                loadPage(currentPageNum);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        recViewAdapter.notifyDataSetChanged();
    }

    private void setRecyclerView(View v) {
        recViewAdapter = new NewsRecyclerViewAdapter(v.getContext(), localNewsList);
        layoutManager = new LinearLayoutManager(v.getContext());

        localBinding.mainLocalRecyclerView.setLayoutManager(layoutManager);
        localBinding.mainLocalRecyclerView.setItemAnimator(new DefaultItemAnimator());
        localBinding.mainLocalRecyclerView.setAdapter(recViewAdapter);
    }

    @SuppressLint("MissingPermission")
    private String getDeviceCountryCode() {
        String locationResult = "";
        //Get location data from Broadcast intent extra
        locationMgr = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Location location = locationMgr.getLastKnownLocation(locationMgr.getBestProvider
                (new Criteria(), false));

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        //String provider = locationIntent.getStringExtra(LOCATION_PROVIDER);

        Geocoder geocoder = new Geocoder(getContext());
        if (geocoder != null) {
            try {
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                Address address = addressList.get(0);
                locationResult = address.getCountryCode();//Testing purpose

                Log.d(TAG, "Lat:" + latitude + "lon:" + longitude);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            // Log.d(TAG,"Location is "+location);
            localBinding.noDataFoundLayout.noDataFoundContent.setVisibility(View.VISIBLE);
        }
        return locationResult;
    }

    private void requestPermissionAgain() {
        if (localBinding.noDataFoundLayout.noDataPermissionButton != null) {
            localBinding.noDataFoundLayout.noDataPermissionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Clicked");
                    checkLocationPermissionResults(locationPermissions);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //If granted after rationale window popped up
            //checkLocationPermissionResults(permissions);
            localBinding.noDataFoundLayout.noDataFoundContent.setVisibility(View.GONE);
            localBinding.localSwipeRefreshLayout.setRefreshing(true);
            localBinding.localSwipeRefreshLayout.setEnabled(true);
            setObserver();
            loadPage(currentPageNum);
            swipeToRefreshListener();
        }else {
            // Permission was denied...if "Never ask again" is checked and confirmed,
            // it will automatically be PERMISSION_DENIED
            localBinding.localSwipeRefreshLayout.setRefreshing(false);
            localBinding.localSwipeRefreshLayout.setEnabled(false);
            localBinding.noDataFoundLayout.noDataFoundContent.setVisibility(View.VISIBLE);
            requestPermissionAgain();
            Log.d(TAG,"Location permission denied");
        }
    }

    private void checkLocationPermissionResults(String[] permissions) {

        //Deeply check permission results
        permMgr.checkPermission(getContext(), permissions[0], new PermissionManager.PermissionRequestListener() {
            @Override
            public void onNeedPermission() {
                isPermissionGranted = false;
                requestPermissions(permissions, 100);
            }

            @Override
            public void onPermissionPreDenied() {
                //OK
                isPermissionGranted = false;
                localBinding.noDataFoundLayout.noDataFoundContent.setVisibility(View.VISIBLE);
                showLocationRational(permissions);
                requestPermissionAgain();
            }

            @Override
            public void onPermissionPreDeniedWithNeverAskAgain() {
                isPermissionGranted = false;
                //localBinding.noDataFoundLayout.noDataFoundContent.setVisibility(View.VISIBLE);
                localBinding.noDataFoundLayout.noDataPermissionButton.setText
                        ("Now TAP ME to enable location access from settings");
                Log.d(TAG,"change text");
                utility.dialogToOpenSetting(getContext(),"Location Permission Denied",
                        "Click GO TO SETTINGS to enable location permission, " +
                                "then refresh the page by tapping on the button in the bottom navigation bar.");

            }

            @Override
            public void onPermissionGranted() {
                isPermissionGranted=true;
                setObserver();
                loadPage(currentPageNum);
                swipeToRefreshListener();
                localBinding.noDataFoundLayout.noDataFoundContent.setVisibility(View.GONE);
            }
        });
       //return isPermissionGranted;
    }

    public void showLocationRational(String[] permissions) {
        new AlertDialog.Builder(getContext()).setTitle("Location permission denied")
                .setMessage("Without this permission the APP is unable to display contents on this page." +
                        "Are you sure you want to deny this permission?").setCancelable(false)
                .setNegativeButton("I'M SURE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                }).setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(permissions, 100);
                dialogInterface.dismiss();
            }
        }).show();
    }
}
