package com.scanner.rmcode.fragments;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

public interface BaseFragment {

    void setThemeDetails(Drawable background, Drawable button, ColorStateList accent);

    void setTheme();
}
