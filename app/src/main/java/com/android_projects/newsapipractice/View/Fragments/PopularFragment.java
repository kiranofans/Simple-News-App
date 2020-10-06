package com.android_projects.newsapipractice.View.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.View.Adapter.NewsRecyclerViewAdapter;
import com.android_projects.newsapipractice.View.MainActivity;
import com.android_projects.newsapipractice.View.PaginationListener;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.FragmentPopularBinding;

import java.util.ArrayList;
import java.util.List;

public class PopularFragment extends Fragment {
    private final String TAG = PopularFragment.class.getSimpleName();

    private View v;
    private LinearLayoutManager layoutManager;
    private MainActivity main;

    private FragmentPopularBinding popBinding;
    private NewsArticleViewModel newsViewModel;

    private NewsRecyclerViewAdapter recyclerViewAdapter;
    private int currentPageNum = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    private List<Article> popularArticleList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        popBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_popular,container,false);
        main = (MainActivity)getActivity();
        return v = popBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newsViewModel = new ViewModelProvider(this).get(NewsArticleViewModel.class);

        setPopularRecyclerView();
        setPopObserver();
        loadPage(currentPageNum);
        swipeToRefreshListener();
        onScrollListener();
    }

    private void setPopularRecyclerView() {
        recyclerViewAdapter = new NewsRecyclerViewAdapter(v.getContext(), popularArticleList);
        layoutManager = new LinearLayoutManager(v.getContext());

        popBinding.mainPopularRecyclerView.setLayoutManager(layoutManager);
        popBinding.mainPopularRecyclerView.setItemAnimator(new DefaultItemAnimator());
        popBinding.mainPopularRecyclerView.setAdapter(recyclerViewAdapter);
    }

    private void swipeToRefreshListener() {
        popBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPageNum = 1;
            recyclerViewAdapter.clear();
            loadPage(currentPageNum);
        });
    }

    private void loadPage(int pageNum) {
        Log.d(TAG, "API called " + pageNum);
        popBinding.swipeRefreshLayout.setRefreshing(true);
        newsViewModel.getArticleListPopular(pageNum);
    }

    private void setPopObserver() {
        newsViewModel.getArticleLiveData().observe(getViewLifecycleOwner(),
                (List<Article> articles) -> {
                    isLoading = false;
                    recyclerViewAdapter.addAllDataToList(articles);
                    popBinding.swipeRefreshLayout.setRefreshing(false);
                });
    }

    private void onScrollListener() {
        popBinding.mainPopularRecyclerView.addOnScrollListener(new PaginationListener(layoutManager,main.toTopBtn) {
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

            @Override
            public void toTopBtnOnclick() {
                main.setToTopBtnOnclick(popBinding.mainPopularRecyclerView);
            }
        });
        recyclerViewAdapter.notifyDataSetChanged();
    }
}
