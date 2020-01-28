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

    public MultiRecyclerViewAdapter(Context context, List<?extends BaseModel> list){
        this.context=context;
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
        private TextView titleTv, descriptionTv;
        private ImageView articleImgView;

        /**
         * Set the article data
         * **/

        public ArticleHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.article_title);
            descriptionTv = itemView.findViewById(R.id.article_description);
            articleImgView = itemView.findViewById(R.id.article_image_view);
            //Can perform recyclerView onclick here
        }

        @Override
        public void bind(Article object) {
            descriptionTv.setText(object.getDescription());
            titleTv.setText(object.getTitle());
            Glide.with(context).load(object.getUrlToImage()).into(articleImgView);
        }
    }
}
