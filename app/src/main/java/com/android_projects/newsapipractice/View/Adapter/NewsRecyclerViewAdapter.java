package com.android_projects.newsapipractice.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.Utils.Utility;
import com.android_projects.newsapipractice.View.ArticleActivity;
import com.android_projects.newsapipractice.View.ImageActivity;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.ButtonReturnToTopBinding;
import com.android_projects.newsapipractice.databinding.ListNewsBinding;
import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.List;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private final String TAG = NewsRecyclerViewAdapter.class.getSimpleName();

    private List<Article> articleList;
    private Context context;

    //public boolean isLoading = false;//To determine if loading the data or not

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListNewsBinding newsBinding = ListNewsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ArticleHolder(newsBinding);
    }

    public NewsRecyclerViewAdapter(Context context, List<Article> list) {
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
        private Utility utility = new Utility();

        ShareDialog shareDialog = new ShareDialog((Activity) context);

        private int position;
        List<Object> payloads = new ArrayList<>();

        private Intent articleIntent, imageIntent;

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

        public ListNewsBinding getBinding() {
            return holderBinding;
        }

        @Override
        public void bind(Article object) {
            //Convert UTC+0 to local time zone and switched to "MMM d, yyyy KK:mm a" 12 hours format
            String newDateFormat = utility.getFinalTimeStamp
                    (context,"MMM d, yyyy KK:mm a", object.getPublishedAt());

            holderBinding.articleTvSource.setText(object.getSource().getName());
            holderBinding.articleTvPublishDate.setText(newDateFormat);
            holderBinding.articleTitle.setText(object.getTitle());
            itemOnClick(object);
            setImgOnClick(object);
            Glide.with(context).load(object.getUrlToImage()).into(holderBinding.articleImageView);
            setCardBtnOnClicks(object);

        }

        private void itemOnClick(Article object) {
            itemView.setOnClickListener((View v) -> {
                position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Log.d("TAG", position + " clicked");
                    articleIntent = new Intent(context, ArticleActivity.class);
                    articleIntent.putExtra(EXTRA_KEY_ARTICLE, object);

                    context.startActivity(articleIntent);
                }

            });
        }

        private void setImgOnClick(Article object) {
            holderBinding.articleImageView.setOnClickListener((View v) -> {
                position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    imageIntent = new Intent(context, ImageActivity.class);
                    imageIntent.putExtra(EXTRA_KEY_ARTICLE, object);

                    context.startActivity(imageIntent);
                }
            });
        }

        private void facebookLinkShare(Article obj) {
            String newsSource = obj.getSource().getName();
            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder().setShareHashtag(
                    new ShareHashtag.Builder().setHashtag("#" + newsSource).build())
                    .setQuote(obj.getDescription())
                    .setContentUrl(Uri.parse(obj.getUrl())).build();
            if (shareDialog.canShow(ShareLinkContent.class)) {
                shareDialog.show(shareLinkContent);
            }
        }

        private void setCardBtnOnClicks(Article obj) {
            position = getAdapterPosition();
            holderBinding.btnShare.setOnClickListener((View v) -> {
                utility.shareText(obj, itemView.getContext(), v.getContext().getString(R.string.share_via));
            });
            holderBinding.btnShareFacebook.setOnClickListener((View v) -> {
                facebookLinkShare(obj);
            });

            holderBinding.btnShareTwitter.setOnClickListener((View v) -> {
                utility.twitterShare(itemView.getContext(),obj);

            });
        }
    }
}
