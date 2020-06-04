package com.android_projects.newsapipractice.View.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android_projects.newsapipractice.Adapter.CategoriesRecyclerViewAdapter;
import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.FragmentCategoryBinding;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {
    private final String TAG = CategoriesFragment.class.getSimpleName();

    private FragmentCategoryBinding catBinding;
    private View v;
    private FragmentCategoryBinding categoryBinding;

    private CategoriesRecyclerViewAdapter recyclerViewAdapter;
    private List<Article> categoryList=new ArrayList<>();

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        catBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_category,container,false);
        return v= catBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(view.getContext().getString(R.string.title_category));

       /* catBinding.buttonOpenFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getContext(), GoogleLoginActivity.class));
            }
        });*/
    }

   /* @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = ViewModelProviders.of(this).get(SecondViewModel.class);
        // TODO: Use the ViewModel
    }*/
}
