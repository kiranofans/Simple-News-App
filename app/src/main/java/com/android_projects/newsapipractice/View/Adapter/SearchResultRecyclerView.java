package com.android_projects.newsapipractice.View.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android_projects.newsapipractice.Utils.Utility;
import com.android_projects.newsapipractice.View.ArticleActivity;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ListNewsSearchResultBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;

public class SearchResultRecyclerView extends RecyclerView.Adapter<SearchResultRecyclerView.SearchArticleViewHolder> implements Filterable {
    private final String TAG = SearchResultRecyclerView.class.getSimpleName();

    private Utility utility=new Utility();

    private List<Article> articleList;
    private List<Article> articleFilteredList;
    private Context _context;

    public SearchResultRecyclerView(Context context, List<Article> articleList){
        _context=context;
        this.articleList=articleList;
        articleFilteredList = new ArrayList<>(articleList);
    }

    @NonNull
    @Override
    public SearchArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListNewsSearchResultBinding searchBiding = ListNewsSearchResultBinding
                .inflate(LayoutInflater.from(_context), parent, false);
        return new SearchArticleViewHolder(searchBiding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchArticleViewHolder holder, int position) {
        Article articleMod = articleFilteredList.get(position);
        String formattedDate = utility.getFinalTimeStamp
                (_context, "MMM d, yyyy KK:mm a", articleMod.getPublishedAt());

        holder.titleTxt.setText(articleMod.getTitle());
        holder.timeStamp.setText(formattedDate);
        Glide.with(_context).load(articleMod.getUrlToImage()).into(holder.resultImgView);
        holder.onItemClicked(articleMod);
    }

    @Override
    public int getItemViewType(int position) {
        return articleFilteredList.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        Log.d(TAG,"list size: "+articleFilteredList.size());
        return articleFilteredList==null || articleFilteredList.isEmpty() ? 0 : articleFilteredList.size();
    }

    public class SearchArticleViewHolder extends RecyclerView.ViewHolder {
        ListNewsSearchResultBinding searchHolderBinding;
        private int position;
        private Intent articleIntent;

        TextView titleTxt,timeStamp;
        ImageView resultImgView;
        public SearchArticleViewHolder(@NonNull ListNewsSearchResultBinding binding) {
            super(binding.getRoot());
            searchHolderBinding=binding;

            titleTxt=searchHolderBinding.searchResultTitle;
            timeStamp=searchHolderBinding.searchResultTimeStamp;
            resultImgView=searchHolderBinding.searchResultImg;
        }

        public void onItemClicked(Article obj){
            itemView.setOnClickListener((View v)->{
                position=getAdapterPosition();
                if(position!= RecyclerView.NO_POSITION){
                    articleIntent=new Intent(_context, ArticleActivity.class);
                    articleIntent.putExtra(EXTRA_KEY_ARTICLE,obj);

                    _context.startActivity(articleIntent);
                }
            });

        }
    }
    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter=new Filter(){
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Article> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();
            if (charSequence.toString().isEmpty()) {
                articleFilteredList=articleList;
                Log.d(TAG,"ArticleFilteredList size: "+articleFilteredList.size());
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Article item : articleList) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                Log.d(TAG,"Main filteredList size: "+filteredList.size());
                articleFilteredList=filteredList;
            }
            results.values = articleFilteredList;
            results.count = articleFilteredList.size();
            Log.d(TAG,"filtered results: "+results.count);
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            articleFilteredList=(List<Article>) results.values;
            if(articleFilteredList==null){
                Log.d(TAG,"Null");
            }
            Log.d(TAG,"final result: "+articleFilteredList.size());
            notifyDataSetChanged();
        }
    };
}
