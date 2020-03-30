package com.android_projects.newsapipractice.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android_projects.newsapipractice.Adapter.NewsArticleRecyclerViewAdapter;
import com.android_projects.newsapipractice.BaseActivity;
import com.android_projects.newsapipractice.MyLocationListener;
import com.android_projects.newsapipractice.PaginationListener;
import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.FragmentLocalBinding;

import java.util.ArrayList;
import java.util.List;

import static com.android_projects.newsapipractice.data.AppConstants.COUNTRY_CODE;

public class LocalFragment extends Fragment implements LocationListener {
    private final String TAG = LocalFragment.class.getSimpleName();

    private View v;
    private FragmentLocalBinding localBinding;
    private NewsArticleViewModel localNewsViewModel;

    private LocationManager locationMgr;

    private final String SORT_BY_PUBLISHED_AT = "publishedAt";
    private int currentPageNum = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private NewsArticleRecyclerViewAdapter recViewAdapter;
    private LinearLayoutManager layoutManager;

    private List<Article> localNewsList = new ArrayList<>();

    private double lat, lon;
    private Location location;

    private String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
    private String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    private String countryCode;
    private Criteria locCriteria = new Criteria();
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
        localNewsViewModel = ViewModelProviders.of(this).get(NewsArticleViewModel.class);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(view.getContext().getString(R.string.title_local_news));
        locationMgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        setRecyclerView(view);
        setObserver();
        loadPage(currentPageNum);
        swipeToRefreshListener();
        onScrollListener();
    }

    private void swipeToRefreshListener() {
        localBinding.localSwipeRefreshLayout.setOnRefreshListener(() -> {
            currentPageNum = 1;
            recViewAdapter.clear();
            loadPage(currentPageNum);
        });
    }

    private void setObserver() {
        localNewsViewModel.getArticleLiveData().observe(this, new Observer<List<Article>>() {
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
        localBinding.localSwipeRefreshLayout.setRefreshing(true);
        countryCode=getDeviceLocationData(locationMgr);
        localNewsViewModel.getArticleListTopHeadlines(page, SORT_BY_PUBLISHED_AT, countryCode);
        Log.d(TAG, "Country Code " +countryCode);
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
        recViewAdapter = new NewsArticleRecyclerViewAdapter(v.getContext(), localNewsList);
        layoutManager = new LinearLayoutManager(v.getContext());

        localBinding.mainLocalRecyclerView.setLayoutManager(layoutManager);
        localBinding.mainLocalRecyclerView.setItemAnimator(new DefaultItemAnimator());
        localBinding.mainLocalRecyclerView.setAdapter(recViewAdapter);
    }

    private String getDeviceLocationData(LocationManager locationMgr) {
        String locationResult = "";
        boolean isGranted = ContextCompat.checkSelfPermission(getContext(),coarseLocationPermission) == PackageManager.PERMISSION_GRANTED |
                ContextCompat.checkSelfPermission(getContext(),fineLocationPermission) == PackageManager.PERMISSION_GRANTED;
        Geocoder geocoder = new Geocoder(getContext());
        if(isGranted){
            String bestProvider = locationMgr.getBestProvider(locCriteria,false);

            locationMgr.requestLocationUpdates(bestProvider, 0, 0, (LocationListener)getContext());
            location = locationMgr.getLastKnownLocation(bestProvider);
            if(location != null){
                lat = location.getLatitude();
                lon = location.getLongitude();
                try {
                    if (geocoder != null) {
                        List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
                        Address address = addressList.get(0);
                        StringBuilder strBuilder = new StringBuilder();
                        strBuilder.append(address.getCountryCode());
                        address.getCountryName();
                        locationResult = strBuilder.toString();
                    }
                    Log.d(TAG,"Permission granted");
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage() + "Cause: " + e.getCause());
                }
            }else{
                localBinding.noContentLayout.noContentLayout.setVisibility(View.VISIBLE);
            }

        }
        Log.d(TAG, "Result: "+locationResult);
        return locationResult;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Latitude: " + location.getLatitude() + "\n" +
                "Longtitude: " + location.getLongitude() + "Country code: " + COUNTRY_CODE);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(TAG, "status: " + bundle.toString());
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "enabled: " + s);

    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "disabled: " + s);
    }
}
