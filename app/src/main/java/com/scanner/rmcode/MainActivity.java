package com.scanner.rmcode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.scanner.rmcode.fragments.CameraFragment;
import com.scanner.rmcode.fragments.HistoryFragment;
import com.scanner.rmcode.fragments.ResultFragment;
import com.scanner.rmcode.fragments.SettingsFragment;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private static final Logger logger = Logger.getLogger(MainActivity.class.getName());
    private Toolbar toolbar;
    private byte[] imageBytes;
    private String result = null;

    SharedPreferences preferences;

//    static {
//        System.loadLibrary("decoder");
//    }

    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String themePrefName = getResources().getString(R.string.theme_pref_name);
        String darkThemePref = getResources().getString(R.string.dark_pref);

        String themePreference = preferences.getString(themePrefName, darkThemePref);
        if (themePreference != null && themePreference.equals(darkThemePref)) {
            setTheme(R.style.RMDarkTheme);
        } else {
            setTheme(R.style.RMLightTheme);
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        changeFragments(new CameraFragment(), getString(R.string.code_scanner));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

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

    public void changeFragments(Fragment fragment, String toolbarTitle) {
        logger.info("Switching to " + fragment.getClass().getName());
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
        toolbar.setTitle(toolbarTitle);
    }

    public void changeToResultFragments(String result) {
        setResult(result);
        changeFragments(new ResultFragment(), getString(R.string.code_scanner));
    }

    public void navigateToBrowser(String result) {
        String url = result;
        url = (!result.startsWith("http://") && !result.startsWith("https://")) ? "http://" + url : url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
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
}
