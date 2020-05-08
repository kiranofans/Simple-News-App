package com.android_projects.newsapipractice.Fragments;

import com.android_projects.newsapipractice.Adapter.NewsRecyclerViewAdapter;

import java.io.Serializable;

public interface FragmentCommunication {
    void respond(NewsRecyclerViewAdapter.ArticleHolder holder, int position, Serializable object);
}
