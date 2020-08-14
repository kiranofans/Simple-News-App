package com.android_projects.newsapipractice.View.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.View.LoginActivity;
import com.android_projects.newsapipractice.databinding.FragmentAccountSettingBinding;
import com.google.android.gms.tasks.Task;

import static com.android_projects.newsapipractice.View.LoginActivity.googleSignInClient;

public class MyAccountSettingsFragment extends Fragment {
    private final String TAG = MyAccountSettingsFragment.class.getSimpleName();

    private FragmentAccountSettingBinding settingBinding;
    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_setting, container, false);
        return v = settingBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSettingsUI();
        logoutBtn();
    }

    private void logoutBtn() {
        settingBinding.preferenceLogOutBtn.setOnClickListener((View v) -> {
            Log.d(TAG, "Clicked");
            googleSignInClient.signOut().addOnCompleteListener((Task<Void> task) -> {
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        });
    }

    private void setSettingsUI(){
        settingBinding.settingsWifi.prefSettingTitle.setText("Use Wi-Fi (Default)");
        settingBinding.settingsWifi.prefSettingDescription.setText("Default setting that allows user to enable Wi-Fi");
        settingBinding.settingsWifi.prefSettingSwitch.setEnabled(true);
        settingBinding.settingsWifi.prefSettingListImgView.setImageResource(R.mipmap.ic_wifi);

        settingBinding.settingsMobileData.prefSettingTitle.setText("Use Cellular Data");
        settingBinding.settingsMobileData.prefSettingListImgView.setImageResource(R.mipmap.ic_cellular_network);
        settingBinding.settingsMobileData.prefSettingDescription.setText("Alow user to enable mobile network");

        //May change font size tab ui later
        settingBinding.settingFontSize.prefSettingTitle.setText("Font Size");
        settingBinding.settingFontSize.prefSettingDescription.setText("Allow user to change text size");
        settingBinding.settingFontSize.prefSettingListImgView.setImageResource(R.mipmap.ic_font_size);

        settingBinding.settingsTextFont.prefSettingTitle.setText("Font");
        settingBinding.settingsTextFont.prefSettingDescription.setText("Allow user to change text font");
        settingBinding.settingsTextFont.prefSettingListImgView.setImageResource(R.mipmap.ic_font);

    }
}
