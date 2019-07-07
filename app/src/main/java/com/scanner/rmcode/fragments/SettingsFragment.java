package com.scanner.rmcode.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.scanner.rmcode.MainActivity;
import com.scanner.rmcode.R;

public class SettingsFragment extends Fragment {

    private Context mContext;
    private Activity mFragmentActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.settings_fragment, container, false);

        final SharedPreferences preferences = ((MainActivity) mFragmentActivity).getPreferences();

        CheckBox autoNavigationCheckBox = view.findViewById(R.id.settings_auto_nav_check_box);
        String autoNavPrefName = mContext.getResources().getString(R.string.auto_nav_pref_name);
        autoNavigationCheckBox.setChecked(preferences.getBoolean(autoNavPrefName, false));

        autoNavigationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("autoNav", isChecked);
                editor.apply();
            }
        });

        RadioGroup themeRadioGroup = view.findViewById(R.id.theme_radio_group);
        RadioButton darkThemeRadioBtn = view.findViewById(R.id.dark_theme_radio_btn);
        RadioButton lightThemeRadioBtn = view.findViewById(R.id.light_theme_radio_btn);

        final String themePrefName = mContext.getResources().getString(R.string.theme_pref_name);
        final String darkThemePref = mContext.getResources().getString(R.string.dark_pref);
        final String lightThemePref = mContext.getResources().getString(R.string.light_pref);

        String checkedTheme = preferences.getString(themePrefName, darkThemePref);
        if (checkedTheme != null && checkedTheme.equals(darkThemePref)) {
            darkThemeRadioBtn.setChecked(true);
        } else {
            lightThemeRadioBtn.setChecked(true);
        }

        themeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked)
                {
                    SharedPreferences.Editor editor = preferences.edit();
                    if (checkedId == R.id.dark_theme_radio_btn) {
                        editor.putString(themePrefName, darkThemePref);
                        mFragmentActivity.setTheme(R.style.RMDarkTheme);
                        mContext.setTheme(R.style.RMDarkTheme);
                    } else {
                        editor.putString(themePrefName, lightThemePref);
                        mFragmentActivity.setTheme(R.style.RMLightTheme);
                        mContext.setTheme(R.style.RMLightTheme);
                    }
                    editor.apply();
                }
            }
        });

        Button okBtn = view.findViewById(R.id.setting_ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mFragmentActivity).changeFragments(new CameraFragment(), getString(R.string.code_scanner));
            }
        });

        return view;
    }
}
