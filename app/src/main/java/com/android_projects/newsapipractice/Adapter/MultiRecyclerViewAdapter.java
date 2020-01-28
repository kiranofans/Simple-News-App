package com.android_projects.newsapipractice.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ListNewsBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class MultiRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<?extends BaseModel> anyTypeItems;
    private ListNewsBinding newsBinding;

    private Context context;

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType){
            case Constants.ViewType.ARTICLE_TYPE:
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_news,
                        parent, false);
                return new ArticleHolder(v,viewType);
        }
        return null;
    }

    public MultiRecyclerViewAdapter(Context context){
        this.context=context;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bind(anyTypeItems.get(position));
    }

    @Override
    public int getItemCount() {
        return anyTypeItems.size();
    }

    public class ArticleHolder extends BaseViewHolder<Article>{
        private TextView titleTv, descriptionTv;
        private ImageView articleImgView;

        public ArticleHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            //Can perform recyclerView onclick here
        }

        @Override
        public void bind(Article object) {
            newsBinding.articleDescription.setText(object.getDescription());
            newsBinding.articleTitle.setText(object.getTitle());
            Glide.with(context).load(object.getUrlToImage()).into(newsBinding.articleImageView);
        }
    }
}
