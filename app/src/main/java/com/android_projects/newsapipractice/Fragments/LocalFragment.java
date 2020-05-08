package com.android_projects.newsapipractice.Fragments;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android_projects.newsapipractice.Adapter.NewsRecyclerViewAdapter;
import com.android_projects.newsapipractice.BuildConfig;
import com.android_projects.newsapipractice.MyLocationBroadcastReceiver;
import com.android_projects.newsapipractice.MyLocationService;
import com.android_projects.newsapipractice.PaginationListener;
import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.Utils.RecyclerViewImgClickListener;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.FragmentLocalBinding;
import com.android_projects.newsapipractice.databinding.ListNewsBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static com.android_projects.newsapipractice.MyLocationService.LOCATION_BROADCAST_ACTION;
import static com.android_projects.newsapipractice.MyLocationService.countryCode;
import static com.android_projects.newsapipractice.MyLocationService.countryName;

public class LocalFragment extends Fragment implements RecyclerViewImgClickListener{
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

    private NewsRecyclerViewAdapter recViewAdapter;
    private LinearLayoutManager layoutManager;

    private List<Article> localNewsList = new ArrayList<>();

    public MyLocationService locationService;
    public boolean isTracking = false;
    //private String countryCode,countryName;
    //private Criteria locCriteria = new Criteria();

    private MyLocationBroadcastReceiver locReceiver=null;
   /* private MyLocationService mLocationReceiver;
    private MyLocationService myLocationListener;*/
    private IntentFilter intentFilter = new IntentFilter();
    private Intent locationIntent;

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

        locReceiver=new MyLocationBroadcastReceiver();

        intentFilter.addAction(LOCATION_BROADCAST_ACTION);
        locationIntent = new Intent(getActivity().getApplication(), MyLocationService.class);
        getActivity().getApplication().startService(locationIntent);
        getActivity().getApplication().bindService(locationIntent,serviceConnection, Context.BIND_AUTO_CREATE);

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
        localNewsViewModel.getArticleListTopHeadlines(page, SORT_BY_PUBLISHED_AT, "ca");

        Toast.makeText(getContext(),"is Local location null:"+countryCode,Toast.LENGTH_LONG).show();
        Log.d(TAG,"Is location null: "+countryName+"\nLocation: "+countryCode
                /*locationIntent.getExtras().getDouble(LATITUDE,0)*/ );
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
        recViewAdapter = new NewsRecyclerViewAdapter(v.getContext(), localNewsList,this);
        layoutManager = new LinearLayoutManager(v.getContext());

        localBinding.mainLocalRecyclerView.setLayoutManager(layoutManager);
        localBinding.mainLocalRecyclerView.setItemAnimator(new DefaultItemAnimator());
        localBinding.mainLocalRecyclerView.setAdapter(recViewAdapter);
    }

    private void openSetting(){
        Intent settingIntent = new Intent();
        settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID,null);
        settingIntent.setData(uri);
        settingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(settingIntent);
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            String name = componentName.getClassName();
            if(name.endsWith("MyLocationService")){
                locationService=((MyLocationService.LocationServiceBinder)iBinder).getService();
                locationService.startTracking();
                locReceiver=new MyLocationBroadcastReceiver();
                getActivity().registerReceiver(locReceiver,intentFilter);
                Log.d(TAG,"Location Ready "+locationService);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if(componentName.getClassName().equals("MyLocationService")){
                locationService = null;
            }
            if(locReceiver==null){
                Log.d(TAG,"Do not unregister receiver as it was never registered");
            }else{
                Log.d(TAG,"Unregister receiver");
                getActivity().unregisterReceiver(locReceiver);
                locReceiver=null;
            }
        }
    };

    @SuppressLint("MissingPermission")
    private String getDeviceCountryCode(){
        String locationResult = "";
        //Get location data from Broadcast intent extra
        locationMgr=(LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Location location = locationMgr.getLastKnownLocation(locationMgr.getBestProvider
                (new Criteria(),false));
       /* locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,
                0,new MyLocationService.MyLocationListener());*/


        double latitude=location.getLatitude();
        double longitude = location.getLongitude();
         double lat = locationIntent.getDoubleExtra(LATITUDE,0);
        double lon= locationIntent.getDoubleExtra(LONGITUDE,0);
        //String provider = locationIntent.getStringExtra(LOCATION_PROVIDER);

        Geocoder geocoder=new Geocoder(getContext());
        if(geocoder!=null){
            try {
                List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
                Address address=addressList.get(0);
                locationResult= address.getCountryCode();//Testing purpose

                Log.d(TAG,"Lat:"+latitude+"lon:"+longitude);
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
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(locReceiver,new IntentFilter(LOCATION_BROADCAST_ACTION));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(locReceiver,new IntentFilter(LOCATION_BROADCAST_ACTION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(locReceiver);
        super.onStop();
    }

    @Override
    public void onRecyclerViewImageClicked(NewsRecyclerViewAdapter.ArticleHolder articleHolder, int position, ListNewsBinding newsBinding) {

    }

   /* @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(locReceiver);
    }*/
}
