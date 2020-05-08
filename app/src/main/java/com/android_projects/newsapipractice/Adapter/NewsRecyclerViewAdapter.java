package com.android_projects.newsapipractice.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android_projects.newsapipractice.ArticleActivity;
import com.android_projects.newsapipractice.Fragments.FragmentCommunication;
import com.android_projects.newsapipractice.ImageActivity;
import com.android_projects.newsapipractice.Utils.DataDiffCallback;
import com.android_projects.newsapipractice.Utils.RecyclerViewImgClickListener;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ButtonReturnToTopBinding;
import com.android_projects.newsapipractice.databinding.ListNewsBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private final String TAG = NewsRecyclerViewAdapter.class.getSimpleName();

    private List<Article> articleList;
    private Context context;
    public boolean isLoading = false;//To determine if load the data or not

    private RecyclerViewImgClickListener mListener;

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListNewsBinding newsBinding = ListNewsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ArticleHolder(newsBinding);
    }

    public NewsRecyclerViewAdapter(Context context, List<Article> list, RecyclerViewImgClickListener listener) {
        this.context = context;
        articleList = list; //initialize articleList
        mListener=listener;
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

    public void clear() {
        articleList.clear();
        notifyDataSetChanged();
    }

    public void add(Article articleResponse) {
        articleList.add(articleResponse);
        notifyItemInserted(articleList.size() - 1);
    }

    public void addAll(List<Article> articles) {
        for (Article response : articles) {
            add(response);
        }
    }

    public void updateList(List<Article> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DataDiffCallback(this.articleList, newList));
        diffResult.dispatchUpdatesTo(this);
        String result = diffResult.toString();
        Log.d(TAG, "Diff result: " + result);
    }

    public class ArticleHolder extends BaseViewHolder<Article> {
        private ListNewsBinding holderBinding;
        private ButtonReturnToTopBinding goToTopBinding;

        private FragmentCommunication fragCommunicator;
        private int position;
        List<Object> payloads = new ArrayList<>();

        private Intent articleIntent,imageIntent;
        /**
         * Set the article data
         **/

        public ArticleHolder(ListNewsBinding binding) {
            super(binding.getRoot());
            this.holderBinding = binding;
            //Can perform recyclerView onclick here
        }

        @Override
        protected void clearListData() {

        }

        public ListNewsBinding getBinding(){
            return holderBinding;
        }
        @Override
        public void bind(Article object) {
            holderBinding.articleTvSource.setText(object.getSource().getName());
            holderBinding.articleTvPublishDate.setText(object.getPublishedAt());//can be converted to local timezone
            holderBinding.articleTitle.setText(object.getTitle());
            itemOnClick(object);
            setImageOnClick(object);
            //showFooter();
            Glide.with(context).load(object.getUrlToImage()).into(holderBinding.articleImageView);
            setCardButtonOnClicks();
            /*if (payloads.isEmpty()) {
                Log.d(TAG,"Payloads is empty");
                return;
            } else {
                Bundle bundle = (Bundle) payloads.get(0);
                for (String key : bundle.keySet()) {
                    if (key.equals(KEY_ARTICLE_NAMES)) {
                        //binding.goToTopButton.setVisibility(View.VISIBLE);
                        Log.d(TAG, "RecyclerView: New Articles");
                    }
                }
            }*/
        }

        private void showFooter() {
            if (isLoading) {
                Toast.makeText(context,"isLoading", Toast.LENGTH_SHORT).show();
                //binding.recyclerViewFooter.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(context,"isNotLoading", Toast.LENGTH_SHORT).show();
                //binding.recyclerViewFooter.setVisibility(View.GONE);
            }
        }

        private void itemOnClick(Article object) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Log.d("TAG", position + " clicked");
                        articleIntent = new Intent(context, ArticleActivity.class);
                        articleIntent.putExtra(EXTRA_KEY_ARTICLE, object);

                        context.startActivity(articleIntent);
                    }
                }
            });

        }

        private void setImageOnClick(Article object){
            //ImageActivity imgFrag = ImageActivity.newInstance(position);
            holderBinding.articleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = getAdapterPosition();
                    Log.d(TAG, "Image clicked "+position);
                    if(position!=RecyclerView.NO_POSITION){
                        imageIntent = new Intent(context, ImageActivity.class);
                        imageIntent.putExtra(EXTRA_KEY_ARTICLE,object);

                        String transName = "Image_"+position;
                        ViewCompat.setTransitionName(holderBinding.articleImageView,transName);
                        //mListener.onRecyclerViewImageClicked(holder,position, holderBinding);

                        context.startActivity(imageIntent);
                    }
                }
            });
        }
        private void setCardButtonOnClicks() {
            position = getAdapterPosition();

            holderBinding.btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Share button clicked "+position);

                    Toast.makeText(view.getContext(), "Ok", Toast.LENGTH_SHORT).show();
                }
            });
            holderBinding.btnShareFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Facebook clicked "+position);
                    Toast.makeText(view.getContext(), "Facebook", Toast.LENGTH_SHORT).show();
                }
            });

            holderBinding.btnShareTwitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Twitter clicked "+position);
                    Toast.makeText(view.getContext(), "Twitter", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}
