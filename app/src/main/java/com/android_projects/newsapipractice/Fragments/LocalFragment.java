package com.android_projects.newsapipractice.Fragments;

import android.location.Location;
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
import com.android_projects.newsapipractice.BaseActivity;
import com.android_projects.newsapipractice.PaginationListener;
import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.FragmentLocalBinding;

import java.util.ArrayList;
import java.util.List;

import static com.android_projects.newsapipractice.data.AppConstants.COUNTRY_CODE;

public class LocalFragment extends Fragment {
    private final String TAG = LocalFragment.class.getSimpleName();

    private View v;
    private FragmentLocalBinding localBinding;
    private NewsArticleViewModel localNewsViewModel;

    private LocationManager locationMgr;
    private final int RC_LOCATION_PERMISSION = 101;

    private final String SORT_BY_PUBLISHED_AT = "publishedAt";
    private int currentPageNum = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private NewsArticleRecyclerViewAdapter recViewAdapter;
    private LinearLayoutManager layoutManager;

    private List<Article> localNewsList = new ArrayList<>();

    private double lat, lon;
    private Location location;
    private BaseActivity baseActivityInstance = new BaseActivity();

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

        setRecyclerView(view);
        setObserver();
        loadPage(currentPageNum);
        swipeToRefreshListener();
        onScrollListener();
        // getDeviceLocation(view,lat, lon);
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

    private void loadPage(int page) {
        Log.d(TAG, "API called " + page);
        localBinding.localSwipeRefreshLayout.setRefreshing(true);
        localNewsViewModel.getArticleListTopHeadlines(page, SORT_BY_PUBLISHED_AT, COUNTRY_CODE);
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
                return false;
            }

            @Override
            public boolean isLoading() {
                return false;
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
}
