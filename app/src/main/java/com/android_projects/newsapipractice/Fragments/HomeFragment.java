package com.android_projects.newsapipractice.Fragments;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android_projects.newsapipractice.Adapter.MultiRecyclerViewAdapter;
import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.ViewModels.HomeViewModel;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private final String TAG = HomeFragment.class.getSimpleName();

    private FragmentHomeBinding homeBinding;
    private NewsArticleViewModel viewModel;

    private MultiRecyclerViewAdapter recyclerViewAdapter;
    private int currentPageNum = 1;
    private List<Article> articleList = new ArrayList<>();

    public static boolean isLoading=false;//To determine if load the data or not
    private HomeViewModel mViewModel;
    private View v;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        homeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,
                container,false);
        v=homeBinding.getRoot();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(NewsArticleViewModel.class);

        setRecyclerView();
        setObserver();
        loadPage(currentPageNum);//load news data the very first time
        swipeToRefreshListener();
        onScrollListener();
    }

    private void swipeToRefreshListener(){
        homeBinding.swipeRefreshLayout.setOnRefreshListener(() ->{
            currentPageNum=1;
            recyclerViewAdapter.clear();
            loadPage(currentPageNum);
        });
    }

    private void setObserver(){
        viewModel.getArticleLiveData().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                isLoading = false;
                articleList.addAll(articles);
                Log.d(TAG,"onChanged: "+articleList.size());
                homeBinding.swipeRefreshLayout.setRefreshing(false);
                recyclerViewAdapter.notifyDataSetChanged();            }
        });
    }
    private void loadPage(int page){
        Log.d(TAG,"API called "+page);
       homeBinding.swipeRefreshLayout.setRefreshing(true);
       viewModel.getArticlesList(page);
    }

    private void setRecyclerView(){
        //recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerViewAdapter = new MultiRecyclerViewAdapter(v.getContext(),articleList);

        homeBinding.recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        homeBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        homeBinding.recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void onScrollListener(){
        homeBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (!isLoading) {//true
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
        recyclerViewAdapter.notifyDataSetChanged();
    }
}
