package com.android_projects.newsapipractice.View.Fragments;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.View.Adapter.NewsRecyclerViewAdapter;
import com.android_projects.newsapipractice.View.PaginationListener;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.FragmentHomeBinding;
import com.android_projects.newsapipractice.network.NetworkConnectivityReceiver;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements NetworkConnectivityReceiver.ConnectivityReceiverListener {
    private final String TAG = HomeFragment.class.getSimpleName();

    //Network
    private NetworkConnectivityReceiver connReceiver;

    //UI
    private FragmentHomeBinding homeBinding;
    private NewsArticleViewModel viewModel;
    private View v;
    private NewsRecyclerViewAdapter recyclerViewAdapter;
    private LinearLayoutManager layoutManager;

    private int currentPageNum = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;//To determine if load the data or not

    private List<Article> articleList = new ArrayList<>();

    private final String SORT_BY_PUBLISHED_AT = "publishedAt";
    private final String SORT_BY_RELEVANCY = "relevancy";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,
                container, false);
        return v = homeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NewsArticleViewModel.class);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(view.getContext().getString(R.string.title_latest_news));
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerViewAdapter = new NewsRecyclerViewAdapter(v.getContext(), articleList);

        setRecyclerView(view);
        setObserver();//Observer has to be separated from loadPage()
        loadPage(currentPageNum);//load news data the very first time
        swipeToRefreshListener();
        onScrollListener();
    }

    private void swipeToRefreshListener() {

        homeBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPageNum = 1;
            recyclerViewAdapter.clear();
            loadPage(currentPageNum);
        });
    }

    //Observer only refresh
    @SuppressLint("FragmentLiveDataObserve")
    private void setObserver() {
        viewModel.getArticleLiveData().observe(this, (List<Article> articles) -> {
            isLoading = false;
            articleList.addAll(articles);
            Log.d(TAG, "onChanged: " + articleList.size());

            homeBinding.swipeRefreshLayout.setRefreshing(false);
            recyclerViewAdapter.notifyDataSetChanged();
        });
    }

    private void loadPage(int page) {
        Log.d(TAG, "API called " + page);
        isLastPage = false;
        homeBinding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getArticleListEverything(page, SORT_BY_PUBLISHED_AT);
    }

    private void setRecyclerView(View v) {
        homeBinding.mainHomeRecyclerView.setLayoutManager(layoutManager);
        homeBinding.mainHomeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        homeBinding.mainHomeRecyclerView.setAdapter(recyclerViewAdapter);
    }

    private void onScrollListener() {
        homeBinding.mainHomeRecyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
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
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNetworkConnectionChanged(Boolean isConnected) {
        if (isConnected) {
            setObserver();
        } else {
            homeBinding.swipeRefreshLayout.setRefreshing(false);
            homeBinding.swipeRefreshLayout.setEnabled(false);
        }
    }
}
