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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android_projects.newsapipractice.View.Adapter.NewsRecyclerViewAdapter;
import com.android_projects.newsapipractice.View.PaginationListener;
import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private final String TAG = HomeFragment.class.getSimpleName();

    private FragmentHomeBinding homeBinding;
    private NewsArticleViewModel viewModel;
    private View v;

    private NewsRecyclerViewAdapter recyclerViewAdapter;
    private LinearLayoutManager layoutManager;

    private int currentPageNum = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;//To determine if load the data or not

    private List<Article> articleList = new ArrayList<>();

    private final String SORT_BY_PUBLISHED_AT="publishedAt";
    private final String SORT_BY_RELEVANCY="relevancy";

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,
                container, false);
        return v=homeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NewsArticleViewModel.class);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(view.getContext().getString(R.string.title_latest_news));
        layoutManager=new LinearLayoutManager(view.getContext());

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
        //List<Article> newList = new ArrayList<>();
        viewModel.getArticleLiveData().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                isLoading = false;
                articleList.addAll(articles);
                Log.d(TAG, "onChanged: " + articleList.size());

                homeBinding.swipeRefreshLayout.setRefreshing(false);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadPage(int page) {
        Log.d(TAG, "API called " + page);
        homeBinding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getArticleListEverything(page,SORT_BY_PUBLISHED_AT);
    }

    private void setRecyclerView(View v) {
        recyclerViewAdapter = new NewsRecyclerViewAdapter(v.getContext(), articleList);

        //homeBinding.mainHomeRecyclerView.getAdapter().
        homeBinding.mainHomeRecyclerView.setLayoutManager(layoutManager);
        homeBinding.mainHomeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        homeBinding.mainHomeRecyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.updateList(articleList);
    }

    private void onScrollListener(){
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

 /*   private void setFragmentTransition(NewsRecyclerViewAdapter.ArticleHolder articleHolder, int pos) {

        //Article articleMod = (Article) getActivity().getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);
        String transName = getString(R.string.fragment_transition_name);


        //Exit activity transition
        Explode exitExplode = new Explode();
        exitExplode.setDuration(FADE_DEFAULT_TIME);
        this.setExitTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        this.setAllowEnterTransitionOverlap(true);
    }*/

   /* @Override
    public void onRecyclerViewImageClicked(NewsRecyclerViewAdapter.ArticleHolder articleHolder, int position, ListNewsBinding newsBinding) {
       // Article articleMod = (Article) getActivity().getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);
        String transName = getString(R.string.fragment_transition_name);
        int pos = articleHolder.getAdapterPosition();
        articleMod = (Article)getActivity().getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);

        //Exit activity transition
        Fade exitFade = new Fade();
        exitFade.setDuration(FADE_DEFAULT_TIME);
        setExitTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        this.setAllowEnterTransitionOverlap(true);

       *//* getWindow().setSharedElementEnterTransition(new DetailsTransition());
        imgFragment.setEnterTransition(new Fade());
        imgFragment.setSharedElementReturnTransition(new Fade());*//*

       *//* getActivity().getSupportFragmentManager().beginTransaction().addSharedElement
                (articleHolder.getBinding().articleImageView,transName+position)
                .replace(R.id.main_fragment_container,imgFragment).addToBackStack(null).commit();*//*
       //Intent imgIntent = new Intent(getContext(),ImageActivity.class);
      // getActivity().startActivityFromFragment(this,imgIntent,101);

    }*/

}
