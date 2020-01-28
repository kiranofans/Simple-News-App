package com.android_projects.newsapipractice.Adapter;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.android_projects.newsapipractice.Adapter.Constants.ViewType.ARTICLE_TYPE;

public class Constants {

    @IntDef({ARTICLE_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewType{
       int ARTICLE_TYPE = 100;
    }
}
