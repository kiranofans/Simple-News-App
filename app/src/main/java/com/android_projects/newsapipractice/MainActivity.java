package com.android_projects.newsapipractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.MenuItem;

import com.android_projects.newsapipractice.Fragments.HomeFragment;
import com.android_projects.newsapipractice.Fragments.SecondFragment;
import com.android_projects.newsapipractice.Fragments.ThirdFragment;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private ActivityMainBinding mainBinding;

    public static boolean isLoading=false;//To determine if load the data or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        //loading default fragment
        setFragments(new HomeFragment());
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
                fragment = new SecondFragment();
            break;
            case R.id.nav_3:
                fragment = new ThirdFragment();
            break;

        }
        return setFragments(fragment);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                break;

        }
        return super.onOptionsItemSelected(item);
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
