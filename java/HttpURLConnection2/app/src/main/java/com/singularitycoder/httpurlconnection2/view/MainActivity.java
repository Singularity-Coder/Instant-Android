package com.singularitycoder.httpurlconnection2.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.singularitycoder.httpurlconnection2.R;
import com.singularitycoder.httpurlconnection2.databinding.ActivityMainBinding;
import com.singularitycoder.httpurlconnection2.helper.AppUtils;

public final class MainActivity extends AppCompatActivity {

    @NonNull
    private final String TAG = "MainActivity";

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @Nullable
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appUtils.setStatusBarColor(this, R.color.purple_700);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ignoreNightMode();  // > API 30
    }

    private void ignoreNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getDelegate().applyDayNight();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}