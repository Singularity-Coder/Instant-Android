package com.singularitycoder.foregroundservice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.jakewharton.rxbinding3.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.btn_play)
    ImageButton btnPlay;
    @Nullable
    @BindView(R.id.btn_pause)
    ImageButton btnPause;
    @Nullable
    @BindView(R.id.btn_stop)
    ImageButton btnStop;

    private static final String TAG = "MainActivity";
    public static final String BROADCAST_PAUSE = "com.singularitycoder.foregroundservice.Pause";
    public final String URL_AUDIO = "https://www.kozco.com/tech/c304-2.wav";

    @NonNull
    private Unbinder unbinder;

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        unbinder = ButterKnife.bind(this);
        setClickListeners();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void setStatusBarColor(Activity activity, int statusBarColor) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, statusBarColor));
        window.requestFeature(window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setClickListeners() {
        compositeDisposable.add(
                RxView.clicks(btnPlay)
                        .map(o -> btnPlay)
                        .subscribe(
                                button -> {
                                    startForegroundService();
                                    Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();
                                },
                                throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show())
        );

        compositeDisposable.add(
                RxView.clicks(btnPause)
                        .map(o -> btnPause)
                        .subscribe(
                                button -> {
                                    Intent broadcastIntent = new Intent(BROADCAST_PAUSE);
                                    sendBroadcast(broadcastIntent);
                                    Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show();
                                },
                                throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show())
        );

        compositeDisposable.add(
                RxView.clicks(btnStop)
                        .map(o -> btnStop)
                        .subscribe(
                                button -> {
                                    stopForegroundService();
                                    Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show();
                                },
                                throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show())
        );
    }

    public void startForegroundService() {
        Intent intent = new Intent(this, MusicForegroundService.class);
        intent.putExtra("AUDIO_URL", URL_AUDIO);
        ContextCompat.startForegroundService(this, intent);
    }

    public void stopForegroundService() {
        Intent serviceIntent = new Intent(this, MusicForegroundService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        compositeDisposable.dispose();
    }
}
