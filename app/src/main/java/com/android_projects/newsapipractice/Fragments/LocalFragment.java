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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android_projects.newsapipractice.Adapter.NewsArticleRecyclerViewAdapter;
import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;
import com.android_projects.newsapipractice.databinding.FragmentLocalBinding;

import java.util.ArrayList;
import java.util.List;

public class LocalFragment extends Fragment implements LocationListener {
    private final String TAG = LocalFragment.class.getSimpleName();

    private View v;
    private FragmentLocalBinding localBinding;
    private NewsArticleViewModel localNewsViewModel;

    private LocationManager locationMgr;
    private final int RC_LOCATION_PERMISSION =101;

    private final String SORT_BY_PUBLISHED_AT="publishedAt";
    private int currentPageNum=1;
    private boolean isLoading=false;

    private NewsArticleRecyclerViewAdapter recViewAdapter;
    private LinearLayoutManager layoutManager;

    private List<Article> localNewsList;
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

        localNewsList=new ArrayList<>();
        setRecyclerView(view);
        setObserver();
        loadPage(currentPageNum);
        swipeToRefreshListener();
        onScrollListener();
    }

    private void swipeToRefreshListener(){
        localBinding.localSwipeRefreshLayout.setOnRefreshListener(()->{
            currentPageNum=1;
            recViewAdapter.clear();
            loadPage(currentPageNum);
        });
    }

    private void setObserver(){
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

    private void loadPage(int page){
        Log.d(TAG, "API called " + page);
        localBinding.localSwipeRefreshLayout.setRefreshing(true);
        localNewsViewModel.getArticleListTopHeadlines(page,SORT_BY_PUBLISHED_AT,"ca");
    }

    private void onScrollListener(){
        localBinding.mainLocalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if(!isLoading){
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0 && totalItemCount >= 2) {
                        currentPageNum++;
                        loadPage(currentPageNum);
                        isLoading = true;//make the isLoading true again, so it is false
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        recViewAdapter.notifyDataSetChanged();
    }

    private void setRecyclerView(View v){
        recViewAdapter = new NewsArticleRecyclerViewAdapter(v.getContext(), localNewsList);
        layoutManager=new LinearLayoutManager(v.getContext());

        localBinding.mainLocalRecyclerView.setLayoutManager(layoutManager);
        localBinding.mainLocalRecyclerView.setItemAnimator(new DefaultItemAnimator());
        localBinding.mainLocalRecyclerView.setAdapter(recViewAdapter);
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
