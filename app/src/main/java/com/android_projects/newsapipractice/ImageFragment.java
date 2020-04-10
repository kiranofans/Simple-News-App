package com.android_projects.newsapipractice;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.FragmentImageBinding;
import com.bumptech.glide.Glide;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_ARTICLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment {
    private final String TAG = ImageFragment.class.getSimpleName();

    private FragmentImageBinding imgBinding;
    private NewsArticleViewModel newsViewModel;
    private View v;

    private Article articleMod;

    public void ImageFragment(){}

    /*public static ImageFragment getInstance(){
        return new ImageFragment();
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        imgBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_image,
                container, false);

        return v=imgBinding.getRoot();
        //newsViewModel = ViewModelProviders.of(this).get(NewsArticleViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        articleMod = (Article) getActivity().getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);

        setTransition();

    }

    private void configureToolbar(Article articleObj){
        //Set the back arrow button
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        ((AppCompatActivity)getActivity()).setTitle("");
    }

    private void setTransition(){
        String transName = ViewCompat.getTransitionName(imgBinding.imageFragmentContainer);

        ViewCompat.setTransitionName(imgBinding.imageFragmentContainer,transName);
        Glide.with(this).load(articleMod.getUrlToImage()).into(imgBinding.fullImageView);

        configureToolbar(articleMod);
    }

}
