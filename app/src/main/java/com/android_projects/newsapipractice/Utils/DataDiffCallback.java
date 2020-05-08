package com.android_projects.newsapipractice.Utils;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;

import java.util.List;

public class DataDiffCallback extends DiffUtil.Callback
{
    private final String TAG = DataDiffCallback.class.getSimpleName();
    private final List<Article> mOldArticleList;
    private final List<Article> mNewArticleList;

    public static final String KEY_ARTICLE_NAMES="key_article_names";

    public DataDiffCallback(List<Article> oldList,List<Article> newList){
        this.mNewArticleList=newList;
        this.mOldArticleList=oldList;
    }
    @Override
    public int getOldListSize() {
        return mOldArticleList!=null ? mOldArticleList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewArticleList!=null ? mNewArticleList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        /* Called by the DiffUtil to decide whether 2 objects represent the same Item.
         * If your items have unique ids, this method should check their id equality */
        return mOldArticleList.get(oldItemPosition).getSource().getName() ==
                mNewArticleList.get(newItemPosition).getSource().getName();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        /* Checks whether 2 items have the same data.
           You can change its behavior depending on your UI.
         * This method is called by DiffUtil only if [areItemsTheSame] returns true */
        final Article oldArticle = mOldArticleList.get(oldItemPosition);
        final Article newArticle = mNewArticleList.get(newItemPosition);

        return oldArticle.getSource().getName().equals(newArticle.getSource().getName());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        /* If [areItemTheSame] return true and [areConstentsTheSame] returns false DiffUtil
        * calls this method to get a payload about the change.*/
        // Return particular field for changed item
        Article newArticle = mNewArticleList.get(newItemPosition);
        Article oldArticle = mOldArticleList.get(oldItemPosition);
        Bundle diffBundle = new Bundle();
        if(!newArticle.getSource().getName().equals(oldArticle.getSource().getName())){
            diffBundle.putString(KEY_ARTICLE_NAMES,newArticle.getSource().getName());
        }
        if(diffBundle.size() == 0){
            Log.d(TAG,"Diffbundle is empty");
            return null;
        }
        return diffBundle;
    }
}
