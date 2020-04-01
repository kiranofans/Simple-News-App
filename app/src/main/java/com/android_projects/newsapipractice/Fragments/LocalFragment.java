package com.android_projects.newsapipractice.Fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android_projects.newsapipractice.Adapter.NewsArticleRecyclerViewAdapter;
import com.android_projects.newsapipractice.MyLocationBroadcastReceiver;
import com.android_projects.newsapipractice.MyLocationService;
import com.android_projects.newsapipractice.PaginationListener;
import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.FragmentLocalBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.android_projects.newsapipractice.MyLocationService.BROADCAST_CONN_CHANGE;
import static com.android_projects.newsapipractice.MyLocationService.LOCATION_BROADCAST_ACTION;
import static com.android_projects.newsapipractice.MyLocationService.countryCode;
import static com.android_projects.newsapipractice.MyLocationService.countryName;
import static com.android_projects.newsapipractice.MyLocationService.latitude;

public class LocalFragment extends Fragment{
    private final String TAG = LocalFragment.class.getSimpleName();

    private View v;
    private FragmentLocalBinding localBinding;
    private NewsArticleViewModel localNewsViewModel;

    private LocationManager locationMgr;
    private final String LATITUDE="LATITUDE";
    private final String LONGITUDE = "LONGITUDE";
    private final String LOCATION_PROVIDER = "PROVIDER";

    private final String SORT_BY_PUBLISHED_AT = "publishedAt";
    private int currentPageNum = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private NewsArticleRecyclerViewAdapter recViewAdapter;
    private LinearLayoutManager layoutManager;

    private List<Article> localNewsList = new ArrayList<>();

    //private String countryCode,countryName;
    //private Criteria locCriteria = new Criteria();
    private Intent locationIntent;

    private MyLocationService mLocationReceiver;
    private MyLocationService myLocationListener;
    private IntentFilter intentFilter;

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

        intentFilter = new IntentFilter();
        intentFilter.addAction(LOCATION_BROADCAST_ACTION);
        mLocationReceiver=new MyLocationService();
        getActivity().registerReceiver(mLocationReceiver,intentFilter);

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
        localNewsViewModel.getArticleListTopHeadlines(page, SORT_BY_PUBLISHED_AT, countryCode);

        Log.d(TAG, "Local lat " +latitude);
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

    private String getDeviceCountryCode(){
        String locationResult = "";
        //Get location data from Broadcast intent extra

        double lat = locationIntent.getDoubleExtra(LATITUDE,0);
        double lon= locationIntent.getDoubleExtra(LONGITUDE,0);
        String provider = locationIntent.getStringExtra(LOCATION_PROVIDER);

        Geocoder geocoder=new Geocoder(getContext());
        if(geocoder!=null){
            try {
                List<Address> addressList = geocoder.getFromLocation(lat,lon,1);
                Address address=addressList.get(0);
                locationResult= address.getCountryCode();//Testing purpose
               /* countryName = address.getCountryName();
                countryCode=address.getCountryCode();*/
                Log.d(TAG,"Lat:"+lat+"lon:"+lon+"\nProvider: "+provider);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG,"Country Code: "+countryCode+"\nCountry Name: "+countryName);

        }else{
           // Log.d(TAG,"Location is "+location);
            localBinding.noContentLayout.noDataFoundLayout.setVisibility(View.VISIBLE);

        }
        return locationResult;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mLocationReceiver,new IntentFilter(LOCATION_BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mLocationReceiver);
    }
}
