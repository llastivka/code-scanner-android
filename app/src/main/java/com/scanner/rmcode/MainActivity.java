package com.scanner.rmcode;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.scanner.rmcode.fragments.BaseFragment;
import com.scanner.rmcode.fragments.CameraFragment;
import com.scanner.rmcode.fragments.HistoryFragment;
import com.scanner.rmcode.fragments.ResultFragment;
import com.scanner.rmcode.fragments.SettingsFragment;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private static final Logger logger = Logger.getLogger(MainActivity.class.getName());
    private byte[] imageBytes;
    private String result = null;
    private boolean darkTheme;
    Drawable background;
    Drawable buttonDrawable;
    ColorStateList accentColor;

    Toolbar toolbar;

    SharedPreferences preferences;

//    static {
//        System.loadLibrary("decoder");
//    }

    static {
        System.loadLibrary("opencv_java3");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String themePrefName = getResources().getString(R.string.theme_pref_name);
        String darkThemePref = getResources().getString(R.string.dark_pref);

        String themePreference = preferences.getString(themePrefName, darkThemePref);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        setDarkTheme(themePreference != null && themePreference.equals(darkThemePref));

        changeFragments(new CameraFragment(), getString(R.string.code_scanner));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:
                changeFragments(new CameraFragment(), getString(R.string.code_scanner));
                return true;
            case R.id.history:
                changeFragments(new HistoryFragment(), getString(R.string.history));
                return true;
            case R.id.settings:
                changeFragments(new SettingsFragment(), getString(R.string.settings));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void changeFragments(Fragment fragment, String toolbarTitle) {
        logger.info("Switching to " + fragment.getClass().getName());
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();

        TextView title = findViewById(R.id.toolbar_title);
        title.setText(toolbarTitle);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Kalam-Regular.ttf");
        title.setTypeface(type);

        setCurrentThemeDetailsForFragment(fragment);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void changeToResultFragments(String result) {
        setResult(result);
        changeFragments(new ResultFragment(), getString(R.string.code_scanner));
    }

    public void navigateToBrowser(String result) {
        String url= result;
        if (isLink(result)) {
            url = (!result.startsWith("http://") && !result.startsWith("https://")) ? "http://" + url : url;
        } else {
            url = "https://www.google.com/search?q=" + url;
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public boolean isLink(String text) {
        return text.contains("http://") || text.contains("https://") || text.contains("u.nu/");
    }

    public int getDpMeasure(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
        if (darkTheme){
            background = getDrawable(R.drawable.back_grey);
            buttonDrawable = getDrawable(R.drawable.basic_button);
            accentColor = getColorStateList(R.color.darkThemeColorAccent4);
            toolbar.setBackgroundColor(getColor(R.color.darkThemeColorToolbar));
        } else {
            background = getDrawable(R.drawable.back_light);
            buttonDrawable = getDrawable(R.drawable.basic_button_light);
            accentColor = getColorStateList(R.color.darkThemeColorAccent3);
            toolbar.setBackgroundColor(getColor(R.color.darkThemeColorToolbarLight));
        }
    }

    private void setCurrentThemeDetailsForFragment(Fragment fragment) {
        if (fragment instanceof BaseFragment && background != null && buttonDrawable != null
            && accentColor != null) {
            ((BaseFragment) fragment).setThemeDetails(background, buttonDrawable, accentColor);
        }
    }
}
