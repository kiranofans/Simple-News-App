package com.android_projects.newsapipractice.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationListener extends RecyclerView.OnScrollListener {
    private final String TAG = PaginationListener.class.getSimpleName();

    //private final int PAGE_START = 1;
    private final int PAGE_SIZE = 100; //for one page
    private final int TOTAL_ITEM=100;//for total loaded items

    @NonNull
    private LinearLayoutManager layoutManager;

    public PaginationListener(@NonNull LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
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
    }

    protected abstract void loadMoreItems();
    public abstract boolean isLastPage();
    public abstract boolean isLoading();
}
