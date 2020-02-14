package com.android_projects.newsapipractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.android_projects.newsapipractice.Adapter.MultiRecyclerViewAdapter;
import com.android_projects.newsapipractice.Fragments.HomeFragment;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private ActivityMainBinding mainBinding;

    private NewsArticleViewModel viewModel;

    /*private MultiRecyclerViewAdapter recyclerViewAdapter;
    private int currentPageNum = 1;
    private List<Article> articleList = new ArrayList<>();*/

    private BottomNavigationView bottomNavigation;

    public static boolean isLoading=false;//To determine if load the data or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        //loading default fragment
        setFragments(new HomeFragment());

        //ViewModel
        viewModel = ViewModelProviders.of(this).get(NewsArticleViewModel.class);
        mainBinding.mainBottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch(item.getItemId()){
            case R.id.nav_home:
                fragment = new HomeFragment();
            break;
            case R.id.nav_2:
            break;
            case R.id.nav_3:
            break;

        }
        return setFragments(fragment);
    }

    private boolean setFragments(Fragment fragment){
        if(fragment!=null){
            FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container,fragment,"Bottom Nav Fragments");
            fragTrans.commit();
            return true;
        }
        return false;
    }
}
