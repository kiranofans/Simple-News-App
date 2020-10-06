package com.android_projects.newsapipractice.View;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuCompat;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.View.Adapter.SearchResultRecyclerView;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mainBinding;
    public FloatingActionButton toTopBtn;
    private SearchResultRecyclerView recyclerViewAdapter;
    private RecyclerView searchResultRecycler;
    private List<Article> articleList;

    private NewsArticleViewModel viewModel;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.mainBottomNavigation.setOnNavigationItemSelectedListener(mNavItemSelectedListener);
        searchResultRecycler = mainBinding.searchRecyclerList.searchRecyclerView;
        toTopBtn=mainBinding.goToTopButton;

        viewModel = new ViewModelProvider(this).get(NewsArticleViewModel.class);
        articleList = new ArrayList<>();
        recyclerViewAdapter = new SearchResultRecyclerView(this, getArticleListData());

        setRecyclerView();

        //Loading home (default) fragment
        setFragments();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.top_setting:
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.top_setting_search:
                configSearchView(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setToTopBtnOnclick(RecyclerView recyclerView){
        toTopBtn.setOnClickListener((View v)->{
            recyclerView.smoothScrollToPosition(0);
            toTopBtn.setVisibility(View.GONE);
        });
    }

    private void setRecyclerView() {
        searchResultRecycler.setVisibility(View.VISIBLE);
        searchResultRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchResultRecycler.setItemAnimator(new DefaultItemAnimator());

        searchResultRecycler.setAdapter(recyclerViewAdapter);
    }

    private void configSearchView(MenuItem menuItem) {
        SearchView searchView = (SearchView) menuItem.getActionView();
        SearchManager searchMgr = (SearchManager) getSystemService(SEARCH_SERVICE);

        searchView.setSearchableInfo(searchMgr.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                //If newText is empty, make recyclerView list visibility GONE
                searchResultRecycler.setVisibility(View.VISIBLE);
                recyclerViewAdapter.getFilter().filter(newText);
                hideShowRecyclerView(newText);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

        });
    }

    private void hideShowRecyclerView(String query) {
        if (query.isEmpty()) {
            searchResultRecycler.setVisibility(View.GONE);
        } else {
            searchResultRecycler.setVisibility(View.VISIBLE);
        }
    }

    private List<Article> getArticleListData() {
        viewModel.getArticleLiveData().observe(MainActivity.this, (List<Article> articles) -> {
            articleList.addAll(articles);
            recyclerViewAdapter.notifyDataSetChanged();
        });
        viewModel.getArticleListEverything(currentPage, "");
        return articleList;
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onNetworkConnectionChanged(Boolean isConnected) {
        utility.showNoNetworkUI(isConnected, mainBinding.activityMainContent,
                mainBinding.noNetworkLayout.noNetworkContent);
    }
}
