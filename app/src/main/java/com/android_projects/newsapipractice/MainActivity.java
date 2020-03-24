package com.android_projects.newsapipractice;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.MenuItem;

import com.android_projects.newsapipractice.Fragments.HomeFragment;
import com.android_projects.newsapipractice.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity{
    private ActivityMainBinding mainBinding;

    public static boolean isLoading=false;//To determine if load the data or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        //loading default fragment
        setFragments(new HomeFragment());
        mainBinding.mainBottomNavigation.setOnNavigationItemSelectedListener(mNavItemSelectedListener);
    }

/*    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.top_setting:
                break;
            case R.id.top_setting_language:
                break;
            case R.id.top_setting_category:
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
