package com.android_projects.newsapipractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import com.android_projects.newsapipractice.Adapter.MultiRecyclerViewAdapter;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;

    private NewsArticleViewModel viewModel;

    private MultiRecyclerViewAdapter recyclerViewAdapter;
    private int currentPageNum = 1;
    private List<Article> articleList = new ArrayList<>();

    public static boolean isLoading=false;//To determine if load the data or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        //ViewModel
        viewModel = ViewModelProviders.of(this).get(NewsArticleViewModel.class);

        getNewArticles(currentPageNum);//first api call
        swipeToRefreshListener(); //Pull to refresh the page
    }

    private void swipeToRefreshListener(){
        mainBinding.swipeRefreshLayout.setOnRefreshListener(() ->{
            currentPageNum=1;
            getNewArticles(currentPageNum);
        });
    }

    private void getNewArticles(int page){
        mainBinding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getArticles(page).observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                isLoading = false;
                articleList.addAll(articles);
                mainBinding.swipeRefreshLayout.setRefreshing(false);
                setRecyclerView();
            }
        });
    }

    private void setRecyclerView(){
        //recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerViewAdapter = new MultiRecyclerViewAdapter(this,articleList);

        mainBinding.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mainBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mainBinding.recyclerView.setAdapter(recyclerViewAdapter);

        mainBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (!isLoading) {//true
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= 20) {
                        currentPageNum++;
                        getNewArticles(currentPageNum);
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
