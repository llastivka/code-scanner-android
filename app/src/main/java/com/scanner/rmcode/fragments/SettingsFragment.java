package com.scanner.rmcode.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.scanner.rmcode.MainActivity;
import com.scanner.rmcode.R;

public class SettingsFragment extends Fragment implements BaseFragment{

    private Context mContext;
    private Activity mFragmentActivity;

    View settingsFragmentView;

    Drawable background;
    Drawable buttonDrawable;
    ColorStateList accentColor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentActivity = getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.settings_fragment, container, false);
        settingsFragmentView = view;
        setTheme();
        setFonts(view);

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

        final RadioGroup themeRadioGroup = view.findViewById(R.id.theme_radio_group);
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

        final Button okBtn = view.findViewById(R.id.setting_ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                ((MainActivity) mFragmentActivity).changeFragments(new CameraFragment(), getString(R.string.code_scanner));
            }
        });

        themeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked)
                {
                    SharedPreferences.Editor editor = preferences.edit();
                    if (checkedId == R.id.dark_theme_radio_btn) {
                        editor.putString(themePrefName, darkThemePref);
                        background = mContext.getDrawable(R.drawable.back_grey);
                        buttonDrawable = mContext.getDrawable(R.drawable.basic_button);
                        accentColor = mContext.getColorStateList(R.color.darkThemeColorAccent4);
                        ((MainActivity) mFragmentActivity).setDarkTheme(true);
                        setTheme();
//                        view.setBackground(mContext.getDrawable(R.drawable.back_grey));
//                        okBtn.setBackground(mContext.getDrawable(R.drawable.basic_button));
                    } else {
                        editor.putString(themePrefName, lightThemePref);
                        background = mContext.getDrawable(R.drawable.back_light);
                        buttonDrawable = mContext.getDrawable(R.drawable.basic_button_light);
                        accentColor = mContext.getColorStateList(R.color.darkThemeColorAccent3);
                        ((MainActivity) mFragmentActivity).setDarkTheme(false);
                        setTheme();
//                        view.setBackground(mContext.getDrawable(R.drawable.back_light));
//                        okBtn.setBackground(mContext.getDrawable(R.drawable.basic_button_light));
                    }
                    editor.apply();
                }
            }
        });

        return view;
    }

    private void setFonts(View view) {
        Typeface type = Typeface.createFromAsset(mContext.getAssets(),"fonts/Kalam-Regular.ttf");

        CheckBox autoNav = view.findViewById(R.id.settings_auto_nav_check_box);
        autoNav.setTypeface(type);

        TextView themesTitle = view.findViewById(R.id.settings_themes_title);
        themesTitle.setTypeface(type);

        RadioButton darkTheme = view.findViewById(R.id.dark_theme_radio_btn);
        darkTheme.setTypeface(type);

        RadioButton lightTheme = view.findViewById(R.id.light_theme_radio_btn);
        lightTheme.setTypeface(type);

        Button okButton = view.findViewById(R.id.setting_ok_button);
        okButton.setTypeface(type);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setThemeDetails(Drawable back, Drawable buttonBack, ColorStateList accent) {
        background = back;
        buttonDrawable = buttonBack;
        accentColor = accent;

        if (settingsFragmentView != null) {
            setTheme();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setTheme() {
        settingsFragmentView.setBackground(background);

        Button okButton = settingsFragmentView.findViewById(R.id.setting_ok_button);
        okButton.setBackground(buttonDrawable);

        CheckBox autoNav = settingsFragmentView.findViewById(R.id.settings_auto_nav_check_box);
        autoNav.setButtonTintList(accentColor);

        RadioButton darkTheme = settingsFragmentView.findViewById(R.id.dark_theme_radio_btn);
        darkTheme.setButtonTintList(accentColor);

        RadioButton lightTheme = settingsFragmentView.findViewById(R.id.light_theme_radio_btn);
        lightTheme.setButtonTintList(accentColor);
    }
}
