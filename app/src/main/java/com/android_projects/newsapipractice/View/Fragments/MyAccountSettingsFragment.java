package com.android_projects.newsapipractice.View.Fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android_projects.newsapipractice.R;
import com.android_projects.newsapipractice.View.LoginActivity;
import com.android_projects.newsapipractice.databinding.DialogFontSizeBinding;
import com.android_projects.newsapipractice.databinding.FragmentAccountSettingBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;
import java.util.List;

import static com.android_projects.newsapipractice.View.LoginActivity.googleSignInClient;

public class MyAccountSettingsFragment extends Fragment {
    private final String TAG = MyAccountSettingsFragment.class.getSimpleName();

    private FragmentAccountSettingBinding settingBinding;
    private DialogFontSizeBinding dialogBinding;
    private AlertDialog.Builder dialog;

    private View v;
    private RadioGroup fontSizeRadioGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_setting, container, false);
        dialogBinding = DataBindingUtil.inflate(inflater.from(getContext()), R.layout.dialog_font_size, null, false);
        fontSizeRadioGroup = dialogBinding.dialogRadioGroup;

        return v = settingBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSettingsUI();
        settingsTabOnClick();
        logoutBtn();
    }

    private void settingsTabOnClick() {
        settingBinding.settingFontSize.prefSettingsLinearLayout.setOnClickListener((View v) -> {
           // showFontSizePopup();
            Log.d(TAG,"Font size tab clicked");
        });
    }

    private void onRadioButtonChecked() {
        //for dialog not alert dialog
        fontSizeRadioGroup.setOnCheckedChangeListener((RadioGroup radioGroup, int checkedId) -> {
            int childCount = radioGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                MaterialRadioButton materialRb = (MaterialRadioButton) radioGroup.getChildAt(i);
                if (materialRb.getId() == checkedId) {
                    Log.d(TAG, "Selected radio button -> " + materialRb.getText().toString());
                }
            }
        });
    }

    @SuppressLint("ResourceType")
    private void showFontSizePopup() {
        String[] items = {"Small", "Medium", "Large", "Extra Large"};
        fontSizeRadioGroup.setVisibility(View.VISIBLE);

        dialog = new AlertDialog.Builder(v.getContext()).setTitle("Select A Size")
                .setCancelable(false);
        dialog.setSingleChoiceItems(items, -1, (DialogInterface dialogInterface, int item) -> {
            for (int i = 0; i < items.length; i++) {
                if (i == item) {
                    Log.d(TAG, "Selected radio button -> " + item);
                }
            }
            dialogInterface.dismiss();
        });
        dialog.show();

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

        //May change font size tab UI
        settingBinding.settingFontSize.prefSettingTitle.setText(getString(R.string.settings_title_font_size));
        settingBinding.settingFontSize.prefSettingDescription.setText(getString(R.string.settings_description_font_size));
        settingBinding.settingFontSize.prefSettingListImgView.setImageResource(R.mipmap.ic_font_size);
       // settingBinding.settingFontSize.prefSettingSeekbar.setPadding(0,0,0,0);

        settingBinding.settingsTextFont.prefSettingTitle.setText(getString(R.string.settings_title_font));
        settingBinding.settingsTextFont.prefSettingDescription.setText(getString(R.string.settings_description_font));
        settingBinding.settingsTextFont.prefSettingListImgView.setImageResource(R.mipmap.ic_font);

    }
}
