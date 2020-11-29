package com.singularitycoder.roomnews.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class AppSharedPreference {

    @NonNull
    private static final String KEY_IS_DARK = "KEY_IS_DARK";

    @Nullable
    private static AppSharedPreference _instance;

    @Nullable
    private SharedPreferences sharedPref;

    @Nullable
    private SharedPreferences.Editor sharedPrefEditor;

    private AppSharedPreference() {
    }

    @NonNull
    public static synchronized AppSharedPreference getInstance(Context context) {
        if (null == _instance) {
            _instance = new AppSharedPreference();
            _instance.configSessionUtils(context);
        }
        return _instance;
    }

    private void configSessionUtils(Context context) {
        sharedPref = context.getSharedPreferences("AppPreferences", Activity.MODE_PRIVATE);
        sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.apply();
    }

    public final void setDarkState(Boolean isDark) {
        sharedPrefEditor.putBoolean(KEY_IS_DARK, isDark);
        sharedPrefEditor.commit();
    }

    public final Boolean getDarkState() {
        return sharedPref.getBoolean(KEY_IS_DARK, false);
    }
}
