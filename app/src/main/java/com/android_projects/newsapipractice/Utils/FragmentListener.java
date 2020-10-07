package com.android_projects.newsapipractice.Utils;

import android.view.MenuItem;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public interface FragmentListener {
    void configSearchView(MenuItem menuItem);
    FloatingActionButton getToTopBtn();
    void setToTopBtnOnclick(RecyclerView recyclerView);
}
