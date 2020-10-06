package com.android_projects.newsapipractice.View;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public abstract class PaginationListener extends RecyclerView.OnScrollListener {
    private final String TAG = PaginationListener.class.getSimpleName();

    //private final int PAGE_START = 1;
    private final int PAGE_SIZE = 100; //for one page
    private final int TOTAL_ITEM=100;//for total loaded items

    @NonNull
    private LinearLayoutManager layoutManager;
    private FloatingActionButton toTopButton;

    public PaginationListener(@NonNull LinearLayoutManager layoutManager, FloatingActionButton toTopButton) {
        this.layoutManager = layoutManager;
        this.toTopButton= toTopButton;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= PAGE_SIZE && totalItemCount < TOTAL_ITEM) {
                /**
                 * totalItemCount >= PAGE_SIZE && totalItemCount < TOTAL_ITEM
                 * used to stop loading when 100 articles have been loaded */
                loadMoreItems();

            }
        }
        if(layoutManager.findFirstCompletelyVisibleItemPosition()==0){
            toTopButton.setVisibility(View.GONE);
        }
        if(layoutManager.findLastCompletelyVisibleItemPosition()==PAGE_SIZE-89){
            toTopButton.setVisibility(View.VISIBLE);
            toTopBtnOnclick();
        }
    }

    protected abstract void loadMoreItems();
    public abstract boolean isLastPage();
    public abstract boolean isLoading();
    public abstract void toTopBtnOnclick();
}
