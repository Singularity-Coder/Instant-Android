package com.singularitycoder.retrofitresponseobject.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.singularitycoder.retrofitresponseobject.R;
import com.singularitycoder.retrofitresponseobject.databinding.ActivityMainBinding;
import com.singularitycoder.retrofitresponseobject.helper.AppUtils;

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
        appUtils.setStatusBarColor(this, R.color.purple_500);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allowNightMode();  // > API 30
    }

    private void allowNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        getDelegate().applyDayNight();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}