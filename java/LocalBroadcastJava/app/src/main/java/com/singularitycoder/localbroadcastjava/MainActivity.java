package com.singularitycoder.localbroadcastjava;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.singularitycoder.localbroadcastjava.databinding.ActivityMainBinding;

import static com.singularitycoder.localbroadcastjava.AppConstants.INTENT_ACTION_DATE;
import static com.singularitycoder.localbroadcastjava.AppConstants.INTENT_DATA_DATE;

public final class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            binding.tvDate.setText(intent.getStringExtra(INTENT_DATA_DATE));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.btnStartService.setOnClickListener(v -> MyJobIntentService.enqueueMyWork(this, 1, new Intent(this, MyJobIntentService.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register local broadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(INTENT_ACTION_DATE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister local broadcast
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}