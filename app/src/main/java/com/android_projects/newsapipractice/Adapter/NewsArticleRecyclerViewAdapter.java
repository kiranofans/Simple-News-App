package com.android_projects.newsapipractice.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android_projects.newsapipractice.ArticleActivity;
import com.android_projects.newsapipractice.Utils.DataDiffCallback;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ButtonReturnToTopBinding;
import com.android_projects.newsapipractice.databinding.ListNewsBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static com.android_projects.newsapipractice.Utils.DataDiffCallback.KEY_ARTICLE_NAMES;
import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;

public class NewsArticleRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private final String TAG = NewsArticleRecyclerViewAdapter.class.getSimpleName();

    private List<Article> articleList;
    private Context context;
    public boolean isLoading=false;//To determine if load the data or not

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListNewsBinding newsBinding = ListNewsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ArticleHolder(newsBinding);
    }

    public NewsArticleRecyclerViewAdapter(Context context, List<Article> list) {
        this.context = context;
        articleList = list; //initialize articleList
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        holder.bind(articleList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        //Remember to override getItemViewType if using MultiTypeRecyclerView
        return articleList.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public void clear(){
        articleList.clear();
        notifyDataSetChanged();
    }

    public void add(Article articleResponse){
        articleList.add(articleResponse);
        notifyItemInserted(articleList.size() - 1);
    }
    public void addAll(List<Article> articles){
        for(Article response : articles){
            add(response);
        }
    }

    public void updateList(List<Article> newList){
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DataDiffCallback(this.articleList,newList));
        diffResult.dispatchUpdatesTo(this);
        String result = diffResult.toString();
        Log.d(TAG,"Diff result: "+result);
    }

    public class ArticleHolder extends BaseViewHolder<Article> {
        ListNewsBinding binding;
        private ButtonReturnToTopBinding goToTopBinding;

        List<Object> payloads = new ArrayList<>();
        /**
         * Set the article data
         **/

        public ArticleHolder(ListNewsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            //Can perform recyclerView onclick here
        }

        @Override
        protected void clearListData() {

        }


        @Override
        public void bind(Article object) {
            binding.articleDescription.setText(object.getDescription());
            binding.articleTitle.setText(object.getTitle());
            setReadMoreBtn(object);
            showFooter();
            Glide.with(context).load(object.getUrlToImage()).into(binding.articleImageView);
            if(payloads.isEmpty()){
                return;
            }else{
                Bundle bundle=(Bundle) payloads.get(0);
                for(String key: bundle.keySet()){
                    if(key.equals(KEY_ARTICLE_NAMES)){
                        binding.goToTopButton.buttonReturnToTop.setVisibility(View.VISIBLE);
                        Log.d(TAG,"RecyclerView: New Articles");
                    }
                }
            }
        }

        private void setReadMoreBtn(Article object) {
            binding.btnReadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Log.d("TAG", position + " clicked");
                        Intent articleIntent = new Intent(context, ArticleActivity.class);
                        articleIntent.putExtra(EXTRA_KEY_ARTICLE, object);

                        context.startActivity(articleIntent);
                    }
                }
            });
        }

        private void showFooter() {
            if (isLoading) {
                binding.recyclerViewFooter.loadMoreProgressBar.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerViewFooter.loadMoreProgressBar.setVisibility(View.GONE);
            }
        }
    }
}
