package com.android_projects.newsapipractice.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android_projects.newsapipractice.Adapter.NewsArticleRecyclerViewAdapter;
import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.FragmentPopularBinding;

import java.util.ArrayList;
import java.util.List;

public class PopularFragment extends Fragment {
    private  final String TAG = PopularFragment.class.getSimpleName();

    private View v;
    private LinearLayoutManager layoutManager;

    private FragmentPopularBinding popBinding;
    private NewsArticleViewModel newsViewModel;

    private NewsArticleRecyclerViewAdapter recyclerViewAdapter;
    private int currentPageNum=1;
    private List<Article> popularArticleList = new ArrayList<>();

    public boolean isLoading = false;
    private final String SORT_BY_POPULARITY="popularity";

    public static PopularFragment newInstance() {
        return new PopularFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        popBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_popular,container,false);
        return v = popBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newsViewModel= ViewModelProviders.of(this).get(NewsArticleViewModel.class);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(v.getContext().getString(R.string.title_popular_news));

        setPopularRecyclerView();
        setPopObserver();
        loadPage(currentPageNum);
        swipeToRefreshListener();
        onScrollListener();
    }

    private void setPopularRecyclerView(){
        recyclerViewAdapter = new NewsArticleRecyclerViewAdapter(v.getContext(),popularArticleList);
        layoutManager=new LinearLayoutManager(v.getContext());

        popBinding.mainPopularRecyclerView.setLayoutManager(layoutManager);
        popBinding.mainPopularRecyclerView.setItemAnimator(new DefaultItemAnimator());
        popBinding.mainPopularRecyclerView.setAdapter(recyclerViewAdapter);
    }

    private void swipeToRefreshListener(){
        popBinding.swipeRefreshLayout.setOnRefreshListener(()->{
            currentPageNum=1;
            recyclerViewAdapter.clear();
            loadPage(currentPageNum);
        });
    }

    private void loadPage(int pageNum){
        Log.d(TAG, "API called " + pageNum);
        popBinding.swipeRefreshLayout.setRefreshing(true);
        newsViewModel.getArticlesList(pageNum,SORT_BY_POPULARITY);
    }

    private void setPopObserver(){
        newsViewModel.getArticleLiveData().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                isLoading=false;
                popularArticleList.addAll(articles);
                popBinding.swipeRefreshLayout.setRefreshing(false);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = ViewModelProviders.of(this).get(SecondViewModel.class);
        // TODO: Use the ViewModel
    }

    private void onScrollListener() {
        popBinding.mainPopularRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

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
