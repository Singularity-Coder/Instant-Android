package com.singularitycoder.roomnews.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.singularitycoder.roomnews.R;
import com.singularitycoder.roomnews.databinding.ActivityMainBinding;
import com.singularitycoder.roomnews.helper.AppSharedPreference;
import com.singularitycoder.roomnews.helper.AppUtils;

public final class MainActivity extends AppCompatActivity {

    private int defaultTheme;

    @NonNull
    private final String TAG = "MainActivity";

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @Nullable
    private AppSharedPreference appSharedPreference;

    @Nullable
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appUtils.setStatusBarColor(this, R.color.purple_500);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialise();
        allowNightMode();  // > API 30
        if (isNightModeActive(this)) appSharedPreference.setDarkState(true);
        else appSharedPreference.setDarkState(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (defaultTheme != AppCompatDelegate.getDefaultNightMode()) recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void initialise() {
        appSharedPreference = AppSharedPreference.getInstance(this);
    }

    private void allowNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        getDelegate().applyDayNight();
    }

    // https://stackoverflow.com/questions/41391404/how-to-get-appcompatdelegate-current-mode-if-default-is-auto
    public boolean isNightModeActive(@NonNull final Context context) {
        defaultTheme = AppCompatDelegate.getDefaultNightMode();
        if (defaultTheme == AppCompatDelegate.MODE_NIGHT_YES) return true;
        if (defaultTheme == AppCompatDelegate.MODE_NIGHT_NO) return false;

        final int currentTheme = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentTheme == Configuration.UI_MODE_NIGHT_NO) return false;
        if (currentTheme == Configuration.UI_MODE_NIGHT_YES) return true;
        if (currentTheme == Configuration.UI_MODE_NIGHT_UNDEFINED) return false;

        return false;
    }
}