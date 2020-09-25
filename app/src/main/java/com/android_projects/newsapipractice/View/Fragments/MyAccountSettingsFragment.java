package com.android_projects.newsapipractice.View.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.View.LoginActivity;
import com.android_projects.newsapipractice.databinding.DialogThemeBinding;
import com.android_projects.newsapipractice.databinding.FragmentAccountSettingBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;
import java.util.List;

import static com.android_projects.newsapipractice.View.LoginActivity.googleSignInClient;

public class MyAccountSettingsFragment extends Fragment {
    private final String TAG = MyAccountSettingsFragment.class.getSimpleName();

    private FragmentAccountSettingBinding settingBinding;
    private DialogThemeBinding dialogBinding;
    private Dialog dialog;

    private SeekBar seekBar;
    private int maxValueX;
    private TextView progressTxt;

    private View v;
    private RadioGroup radioGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_setting, container, false);
        dialogBinding = DataBindingUtil.inflate(inflater.from(getContext()), R.layout.dialog_theme, null, false);

        return v = settingBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        radioGroup = dialogBinding.dialogRadioGroup;

        setSettingsUI();
        settingsTabOnClick();
        logoutBtn();
    }

    private void settingsTabOnClick() {
        settingBinding.settingsTextFont.prefSettingsLinearLayout.setOnClickListener((View v) -> {
            Log.d(TAG, "Font size tab clicked");
        });
        settingBinding.settingsSwitchTheme.prefSettingsLinearLayout.setOnClickListener((View v)->{
            Log.d(TAG,"Change App Theme");
            showThemeDialog();
        });
    }

    private void onRadioButtonChecked() {
        //for dialog not alert dialog
        radioGroup.setOnCheckedChangeListener((RadioGroup radioGroup, int checkedId) -> {
            int childCount = radioGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                RadioButton materialRb = (RadioButton) radioGroup.getChildAt(i);
                if (materialRb.getId() == checkedId) {
                    Log.d(TAG, "Selected radio button -> " + materialRb.getText().toString());
                    switch (materialRb.getId()){
                        case 0:
                            getActivity().setTheme(R.style.AppTheme);
                        case 1:
                            getActivity().setTheme(R.style.AppTheme);
                            dialog.dismiss();
                    }
                }
            }
        });
    }

    private void showThemeDialog() {
        dialog = new Dialog(v.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_theme);
        List<String> strList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                strList.add("Dark Mode");
            }else{
                strList.add("Light Mode");
            }
        }

        radioGroup = dialog.findViewById(R.id.dialog_radio_group);
        for(int i = 0;i<strList.size();i++){
            RadioButton radioBtn = new RadioButton(v.getContext());
            radioBtn.setText(strList.get(i));
            radioBtn.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium);
            radioGroup.addView(radioBtn);
        }
        dialog.show();
        onRadioButtonChecked();
        Log.d(TAG, "Clicked");
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

    private void setSettingsUI() {
        settingBinding.settingsWifi.prefSettingTitle.setText(getString(R.string.settings_title_wifi));
        settingBinding.settingsWifi.prefSettingDescription.setText(getString(R.string.settings_description_wifi));
        settingBinding.settingsWifi.prefSettingSwitch.setEnabled(true);
        settingBinding.settingsWifi.prefSettingListImgView.setImageResource(R.mipmap.ic_wifi);

        settingBinding.settingsMobileData.prefSettingTitle.setText(getString(R.string.settings_title_mobile_data));
        settingBinding.settingsMobileData.prefSettingListImgView.setImageResource(R.mipmap.ic_cellular_network);
        settingBinding.settingsMobileData.prefSettingDescription.setText(getString(R.string.settings_description_mobile_data));

        //Theme tab
        settingBinding.settingsSwitchTheme.prefSettingTitle.setText("Change App Theme");
        settingBinding.settingsSwitchTheme.prefSettingDescription.setText("Allow user to change app theme");
        settingBinding.settingsSwitchTheme.prefSettingListImgView.setImageResource(R.mipmap.ic_change_theme);

        settingBinding.settingsTextFont.prefSettingTitle.setText(getString(R.string.settings_title_font));
        settingBinding.settingsTextFont.prefSettingDescription.setText(getString(R.string.settings_description_font));
        settingBinding.settingsTextFont.prefSettingListImgView.setImageResource(R.mipmap.ic_font);

    }
}
