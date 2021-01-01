package com.singularitycoder.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ActivityLifecycleMethods extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle_methods);
        // Activity and views are displayed
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Activity is in foreground
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Activity is in background and visible
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity is in foreground
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Activity is in background n not visible
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Activity is no longer visible
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}