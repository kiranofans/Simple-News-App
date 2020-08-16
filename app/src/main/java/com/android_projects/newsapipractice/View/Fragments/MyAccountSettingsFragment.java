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
import com.android_projects.newsapipractice.View.MyAccountActivity;
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
        return settingBinding.getRoot();
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
                getActivity().finish();
            });
        });
    }

    private void setSettingsUI(){
        settingBinding.settingsWifi.prefSettingTitle.setText(getString(R.string.settings_title_wifi));
        settingBinding.settingsWifi.prefSettingDescription.setText(getString(R.string.settings_description_wifi));
        settingBinding.settingsWifi.prefSettingSwitch.setEnabled(true);
        settingBinding.settingsWifi.prefSettingListImgView.setImageResource(R.mipmap.ic_wifi);

        settingBinding.settingsMobileData.prefSettingTitle.setText(getString(R.string.settings_title_mobile_data));
        settingBinding.settingsMobileData.prefSettingListImgView.setImageResource(R.mipmap.ic_cellular_network);
        settingBinding.settingsMobileData.prefSettingDescription.setText(getString(R.string.settings_description_mobile_data));

        //May change font size tab UI
        settingBinding.settingFontSize.prefSettingTitle.setText(getString(R.string.settings_title_font_size));
        settingBinding.settingFontSize.prefSettingDescription.setText(getString(R.string.settings_description_font_size));
        settingBinding.settingFontSize.prefSettingListImgView.setImageResource(R.mipmap.ic_font_size);

        settingBinding.settingsTextFont.prefSettingTitle.setText(getString(R.string.settings_title_font));
        settingBinding.settingsTextFont.prefSettingDescription.setText(getString(R.string.settings_description_font));
        settingBinding.settingsTextFont.prefSettingListImgView.setImageResource(R.mipmap.ic_font);

    }
}
