package com.singularitycoder.navigationcomponents;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public final class MainActivity extends AppCompatActivity {

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appUtils.setStatusBarColor(this, android.R.color.white);
        setContentView(R.layout.activity_main);
        ignoreNightMode();  // > API 30
    }

    private void ignoreNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getDelegate().applyDayNight();
    }
}