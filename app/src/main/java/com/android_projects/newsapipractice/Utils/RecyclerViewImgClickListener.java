package com.android_projects.newsapipractice.Utils;

import com.android_projects.newsapipractice.Adapter.NewsRecyclerViewAdapter;
import com.android_projects.newsapipractice.databinding.ListNewsBinding;

public interface RecyclerViewImgClickListener {
    void onRecyclerViewImageClicked(NewsRecyclerViewAdapter.ArticleHolder articleHolder, int position, ListNewsBinding newsBinding);
}
