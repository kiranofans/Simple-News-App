package com.android_projects.newsapipractice.View.Fragments;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.Utils.Utility;
import com.android_projects.newsapipractice.View.Adapter.NewsRecyclerViewAdapter;
import com.android_projects.newsapipractice.View.MainActivity;
import com.android_projects.newsapipractice.View.Managers.PermissionManager;
import com.android_projects.newsapipractice.View.PaginationListener;
import com.android_projects.newsapipractice.ViewModels.NewsArticleViewModel;
import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.databinding.FragmentLocalBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class LocalFragment extends Fragment {
    private final String TAG = LocalFragment.class.getSimpleName();

    private View v;
    private FragmentLocalBinding localBinding;
    private NewsArticleViewModel localNewsViewModel;
    private NewsRecyclerViewAdapter recViewAdapter;
    private FloatingActionButton toTopBtn;
    private LinearLayoutManager layoutManager;

    private Utility utility;
    private LocationManager locationMgr;
    private PermissionManager permMgr;

    private final String SORT_BY_PUBLISHED_AT = "publishedAt";
    private int currentPageNum = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private List<Article> localNewsList;
    private String countryCode = "";
    private MainActivity main;
    public LocalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        localBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_local, container, false);
        locationMgr = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        utility = new Utility();
        permMgr = new PermissionManager(getContext());
        localNewsList = new ArrayList<>();
        main = (MainActivity)getActivity();

        return v = localBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        localNewsViewModel = new ViewModelProvider(this).get(NewsArticleViewModel.class);


        setRecyclerView(view);
        checkLocationPermissionResults(permMgr.locationPermissions);//Permission granted,display content
        onScrollListener();
    }

    private void swipeToRefreshListener() {
        localBinding.localSwipeRefreshLayout.setRefreshing(true);
        localBinding.localSwipeRefreshLayout.setEnabled(true);
        localBinding.localSwipeRefreshLayout.setOnRefreshListener(() -> {
            currentPageNum = 1;
            recViewAdapter.clear();
            loadPage(currentPageNum);
        });
    }

    private void setObserver() {
        localNewsViewModel.getArticleLiveData().observe(getViewLifecycleOwner(), (List<Article> articles) -> {
            isLoading = false;
            localNewsList.addAll(articles);
            utility.showDebugLog(TAG, "onChanged: " + localNewsList.size());
            localBinding.localSwipeRefreshLayout.setRefreshing(false);
            recViewAdapter.notifyDataSetChanged();
        });
    }

    private void loadPage(int page) {
        Log.d(TAG, "API called " + page);
        localNewsViewModel.getArticleListTopHeadlines(page, SORT_BY_PUBLISHED_AT, countryCode);
    }

    private void onScrollListener() {
        localBinding.mainLocalRecyclerView.addOnScrollListener(new PaginationListener(layoutManager,main.toTopBtn) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;//make the isLoading true again, so it is false
                currentPageNum++;
                loadPage(currentPageNum);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public void toTopBtnOnclick() {
                main.setToTopBtnOnclick(localBinding.mainLocalRecyclerView);
            }
        });
        recViewAdapter.notifyDataSetChanged();
    }

    private void setRecyclerView(View v) {
        recViewAdapter = new NewsRecyclerViewAdapter(v.getContext(), localNewsList);
        layoutManager = new LinearLayoutManager(v.getContext());

        localBinding.mainLocalRecyclerView.setLayoutManager(layoutManager);
        localBinding.mainLocalRecyclerView.setItemAnimator(new DefaultItemAnimator());
        localBinding.mainLocalRecyclerView.setAdapter(recViewAdapter);
    }

    private void requestPermissionAgain() {
        if (localBinding.noDataFoundLayout.noDataPermissionButton != null) {
            localBinding.noDataFoundLayout.noDataPermissionButton.setOnClickListener((View v) -> {
                utility.showDebugLog(TAG, "Clicked");
                checkLocationPermissionResults(permMgr.locationPermissions);
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkLocationPermissionResults(permissions);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            /* If granted after rationale window popped up */
            localBinding.noDataFoundLayout.noDataFoundContent.setVisibility(View.GONE);
            localBinding.localSwipeRefreshLayout.setRefreshing(true);
            localBinding.localSwipeRefreshLayout.setEnabled(true);

            setObserver();
            loadPage(currentPageNum);
            swipeToRefreshListener();
        } else {
            /* Permission was denied...if "Never ask again" is checked and confirmed,
             * it will automatically be PERMISSION_DENIED */
            localBinding.localSwipeRefreshLayout.setRefreshing(false);
            localBinding.localSwipeRefreshLayout.setEnabled(false);
            localBinding.noDataFoundLayout.noDataFoundContent.setVisibility(View.VISIBLE);
            requestPermissionAgain();
            utility.changePermBtnTextIfShown(localBinding.noDataFoundLayout.noDataPermissionButton,
                    getString(R.string.local_enable_access_from_settings));
            Log.d(TAG, "Location permission denied");
        }
    }

    private void checkLocationPermissionResults(String[] permissions) {
        //Deeply check permission results
        permMgr.checkPermission(v.getContext(), permissions[0],
                new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onNeedPermission() {
                        requestPermissions(permissions, 100);
                    }

                    @Override
                    public void onPermissionPreDenied() {
                        localBinding.noDataFoundLayout.noDataFoundContent.setVisibility(View.VISIBLE);
                        showLocationRational(permissions, getString(R.string.local_rationale_title),
                                getString(R.string.local_rationale_msg));
                        requestPermissionAgain();
                    }

                    @Override
                    public void onPermissionPreDeniedWithNeverAskAgain() {
                        Button btn = localBinding.noDataFoundLayout.noDataPermissionButton;
                        localBinding.noDataFoundLayout.noDataPermissionButton.setText
                                (getString(R.string.local_enable_access_from_settings));
                        utility.dialogToOpenSetting(v.getContext(), getString(R.string.local_permission_denied),
                                getString(R.string.perm_go_to_settings_msg),
                                AppOpsManager.OPSTR_COARSE_LOCATION, btn);
                    }

                    @Override
                    public void onPermissionGranted() {
                        setObserver();

                        //Got location data after location permission granted
                        countryCode = utility.getDeviceCountryCode(locationMgr, getActivity());
                        loadPage(currentPageNum);
                        swipeToRefreshListener();
                        localBinding.noDataFoundLayout.noDataFoundContent.setVisibility(View.GONE);
                    }
                });
    }

    private void showLocationRational(String[] permissions, String title, String message) {
        new AlertDialog.Builder(v.getContext()).setTitle(title).setMessage(message)
                .setCancelable(false).setNegativeButton(getString(R.string.perm_rationale_neg_btn),
                (DialogInterface dialogInterface, int i) -> {
                    dialogInterface.dismiss();

                }).setPositiveButton(getString(R.string.perm_rationale_pos_btn),
                (DialogInterface dialogInterface, int i) -> {
                    requestPermissions(permissions, 100);
                    dialogInterface.dismiss();

                }).show();
    }
}
