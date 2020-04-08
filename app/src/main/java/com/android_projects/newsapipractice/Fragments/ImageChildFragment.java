package com.android_projects.newsapipractice.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.databinding.FragmentImageBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageChildFragment extends Fragment {
    private final String TAG = ImageChildFragment.class.getSimpleName();

    private FragmentImageBinding imgBinding;
    private NewsArticleViewModel newsViewModel;
    private View v;

    public ImageChildFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        imgBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_image, container,false);
        return v = imgBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newsViewModel = ViewModelProviders.of(this).get(NewsArticleViewModel.class);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
    }
}
