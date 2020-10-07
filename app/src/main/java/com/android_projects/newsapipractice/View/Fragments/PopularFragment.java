package com.android_projects.newsapipractice.View.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.android_projects.newsapipractice.Utils.FragmentListener;
import com.android_projects.newsapipractice.Utils.Utility;
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
    private FragmentPopularBinding popBinding;

    private LinearLayoutManager layoutManager;
    private NewsArticleViewModel newsViewModel;
    private NewsRecyclerViewAdapter recyclerViewAdapter;

    //private MainActivity mainActivity;
    private FragmentListener fragListener;

    private int currentPageNum = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    private List<Article> popularArticleList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        popBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_popular, container, false);
        return v = popBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newsViewModel = new ViewModelProvider(this).get(NewsArticleViewModel.class);
        setHasOptionsMenu(true);

        setPopularRecyclerView();
        setPopObserver();
        loadPage(currentPageNum);
        swipeToRefreshListener();
        onScrollListener();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main_setting_menu, menu);
        fragListener.configSearchView(menu.findItem(R.id.top_setting_search));
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
        popBinding.mainPopularRecyclerView.addOnScrollListener(new PaginationListener
                (layoutManager, fragListener.getToTopBtn()) {
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
                fragListener.setToTopBtnOnclick(popBinding.mainPopularRecyclerView);
            }
        });
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        fragListener = (FragmentListener) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragListener=null;
    }
}
