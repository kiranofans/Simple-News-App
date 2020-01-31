package com.android_projects.newsapipractice.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.android_projects.newsapipractice.data.AppConstants;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ListNewsBinding;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_IMG_URL;
import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_TITLE;

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
        private Button readMoreBtn;

        private Intent articleIntent;

        private String imgURL;

        /**
         * Set the article data
         * **/

        public ArticleHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.article_title);
            descriptionTv = itemView.findViewById(R.id.article_description);
            articleImgView = itemView.findViewById(R.id.article_image_view);
            //articleContentImgView = itemView.findViewById(R.id.article_img_view_content);
            readMoreBtn = itemView.findViewById(R.id.btn_read_more);


            //Can perform recyclerView onclick here
        }

        @Override
        public void bind(Article object) {
            descriptionTv.setText(object.getDescription());
            titleTv.setText(object.getTitle());

            imgURL = object.getUrlToImage();

            setReadMoreBtn(imgURL);

            Glide.with(context).load(imgURL).into(articleImgView);
        }

        private void setReadMoreBtn(String imgURL){
            readMoreBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        articleIntent = new Intent(context, ArticleActivity.class);

                        //Send extra with bundle
                        Bundle bundle = new Bundle();
                        bundle.putString(EXTRA_KEY_IMG_URL,imgURL);
                        bundle.putString(EXTRA_KEY_TITLE,titleTv.getText().toString());

                        context.startActivity(articleIntent.putExtras(bundle));
                    }
                    return false;
                }
            });
        }
    }
}
