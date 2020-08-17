package com.android_projects.newsapipractice.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.Utils.Utility;
import com.android_projects.newsapipractice.View.ArticleActivity;
import com.android_projects.newsapipractice.View.ImageActivity;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;
import com.android_projects.newsapipractice.databinding.FooterNoMoreDataBinding;
import com.android_projects.newsapipractice.databinding.ListNewsBinding;
import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.internal.util.AppendOnlyLinkedArrayList;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private final String TAG = NewsRecyclerViewAdapter.class.getSimpleName();

    private List<Article> articleList;
    private Context context;

    public boolean isFooterVisible = false;//To determine if loading the data or not

    //Views
    private final int FOOTER_VIEW=2;
    private final int NORMAL_VIEW=0;

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //ListNewsBinding newsBinding = ListNewsBinding.inflate(LayoutInflater.from(context), parent, false);
        switch (viewType){
            case FOOTER_VIEW:
                FooterNoMoreDataBinding footerViewBinding = FooterNoMoreDataBinding.inflate(LayoutInflater.from(context),parent,false);
                return new FooterViewHolder(footerViewBinding);
            case NORMAL_VIEW:
                ListNewsBinding newsBinding = ListNewsBinding.inflate(LayoutInflater.from(context), parent, false);
                return new ArticleHolder(newsBinding);
            default:
                return null;
        }
       // return null;
    }

    public NewsRecyclerViewAdapter(Context context, List<Article> list) {
        this.context = context;
        articleList = list; //initialize articleList
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        try{
            if(holder instanceof ArticleHolder){
                ArticleHolder articleHolder = (ArticleHolder) holder;
                articleHolder.bind(articleList.get(position));
            }else if(holder instanceof FooterViewHolder){
                FooterViewHolder footerViewHolder = (FooterViewHolder)holder;
                //footerViewHolder.bind(articleList.get(position+1));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        //Remember to override getItemViewType if using MultiTypeRecyclerView
        if (position == articleList.size()) {
            // This is where we'll add footer.
            return FOOTER_VIEW;
        }
        //return articleList.get(position).getViewType();
        return NORMAL_VIEW;
    }

    @Override
    public int getItemCount() {
        if(articleList==null){
            return 0;
        }
        if(articleList.size()==0){
            return 1;
        }
        return articleList.size()+1;
    }

    public void clear() {
        articleList.clear();
        notifyDataSetChanged();
    }

    public class FooterViewHolder extends BaseViewHolder<Article>{
        private FooterNoMoreDataBinding footerBinding;

        public FooterViewHolder(@NonNull FooterNoMoreDataBinding footerBinding) {
            super(footerBinding.getRoot());
            this.footerBinding=footerBinding;
            footerBinding.footerText.setText("- End of News Articles -");

        }

        @Override
        protected void clearListData() {
        }

        @Override
        public void bind(Article object) {
        }
    }

    public class ArticleHolder extends BaseViewHolder<Article> {
        private ListNewsBinding holderBinding;
        private Utility utility = new Utility();

        ShareDialog shareDialog = new ShareDialog((Activity) context);

        private int position;

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
            holderBinding.getRoot().setOnClickListener((View v) -> {
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
