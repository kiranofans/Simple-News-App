package com.android_projects.newsapipractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.android_projects.newsapipractice.Adapter.MultiRecyclerViewAdapter;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;

    private NewsArticleViewModel viewModel;

    private RecyclerView recyclerView;
    private MultiRecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        //ViewModel
        viewModel = ViewModelProviders.of(this).get(NewsArticleViewModel.class);

        getNewArticles();
    }


    private void getNewArticles(){
        viewModel.getArticles().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                setRecyclerView(articles);
            }
        });
    }

    private void setRecyclerView(List<Article> articlesList){
        //recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerViewAdapter = new MultiRecyclerViewAdapter(this,articlesList);

        mainBinding.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mainBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mainBinding.recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }
}
