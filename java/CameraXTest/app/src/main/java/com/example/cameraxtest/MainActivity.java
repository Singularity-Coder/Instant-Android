package com.example.cameraxtest;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cameraxtest.databinding.ActivityMainBinding;

public final class MainActivity extends AppCompatActivity {

    @NonNull
    private final String TAG = "MainActivity";

    @NonNull
    private AppUtils appUtils = AppUtils.getInstance();

    @Nullable
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBarStuff();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpListeners();
    }

    private void hideTitleBarStuff() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // Hide Title
        getSupportActionBar().hide();   // Hide Title Bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // Hide Status Bar
    }

    private void setUpListeners() {
        binding.conLayMainRoot.setOnClickListener(v -> {
        });
        binding.tvImage.setOnClickListener(v -> appUtils.addFragment(this, null, R.id.con_lay_main_root, new PhotoCameraFragment()));
        binding.tvVideo.setOnClickListener(v -> appUtils.addFragment(this, null, R.id.con_lay_main_root, new VideoCameraFragment()));
    }
}