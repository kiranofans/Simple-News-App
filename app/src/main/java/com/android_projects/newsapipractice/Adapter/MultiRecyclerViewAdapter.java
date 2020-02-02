package com.android_projects.newsapipractice.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android_projects.newsapipractice.ArticleActivity;
import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.Source;
import com.android_projects.newsapipractice.databinding.ActivityArticleBinding;
import com.android_projects.newsapipractice.databinding.ListNewsBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static com.android_projects.newsapipractice.MainActivity.isLoading;
import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;
import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_IMG_URL;
import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_SOURCE_ARRAY;
import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_SOURCE_ID;
import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_SOURCE_NAME;
import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_TITLE;

public class MultiRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<? extends BaseModel> anyTypeItems;
    private Context context;

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListNewsBinding newsBinding = ListNewsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ArticleHolder(newsBinding);
    }

    public MultiRecyclerViewAdapter(Context context, List<? extends BaseModel> list) {
        this.context = context;
        anyTypeItems = list; //initialize anyTypeItems
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bind(anyTypeItems.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        //Remember to override getItemViewType if using MultiTypeRecyclerView
        return anyTypeItems.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return anyTypeItems.size();
    }

    public class ArticleHolder extends BaseViewHolder<Article>{
        ListNewsBinding binding;
        /**
         * Set the article data
         **/

        public ArticleHolder(ListNewsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            //Can perform recyclerView onclick here
        }

        @Override
        public void bind(Article object) {
            binding.articleDescription.setText(object.getDescription());
            binding.articleTitle.setText(object.getTitle());
            setReadMoreBtn(object);
            showFooter();
            Glide.with(context).load(object.getUrlToImage()).into(binding.articleImageView);
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

        private void showFooter(){
            if(isLoading){
                binding.recyclerViewFooter.loadMoreProgressBar
                        .setVisibility(View.VISIBLE);
            }else{
                binding.recyclerViewFooter.loadMoreProgressBar.setVisibility(View.GONE);
            }
        }
    }
}
