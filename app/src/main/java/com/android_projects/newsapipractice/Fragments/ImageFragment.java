package com.android_projects.newsapipractice.Fragments;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android_projects.newsapipractice.R;
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
    private ContextThemeWrapper contextThemeWrapper;
    private int imgFragmentTheme;

    public void ImageFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       /* contextThemeWrapper = new ContextThemeWrapper(getContext(),R.style.ImageFragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);*/
        imgBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_image,
                container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        return v=imgBinding.getRoot();
        //newsViewModel = ViewModelProviders.of(this).get(NewsArticleViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        articleMod = (Article) getActivity().getIntent().getSerializableExtra(EXTRA_KEY_ARTICLE);
       /* if(contextThemeWrapper.getTheme()==null)
            Toast.makeText(getContext(), "No Theme", Toast.LENGTH_SHORT).show();;*/

        receiveTransition();

    }

    private void receiveTransition(){
        String transName = ViewCompat.getTransitionName(imgBinding.imageFragmentContainer);

        ViewCompat.setTransitionName(imgBinding.imageFragmentContainer,transName);
        Glide.with(this).load(articleMod.getUrlToImage()).into(imgBinding.fullImageView);

    }
    private void onImgFragmentTouchEvent(){

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ColorDrawable transparent = new ColorDrawable(Color.TRANSPARENT);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(transparent);
    }
}
