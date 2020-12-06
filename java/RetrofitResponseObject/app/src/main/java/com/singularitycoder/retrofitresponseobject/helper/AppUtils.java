package com.singularitycoder.retrofitresponseobject.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.singularitycoder.retrofitresponseobject.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public final class AppUtils extends AppCompatActivity {

    @NonNull
    private static final String TAG = "AppUtils";

    @Nullable
    private static AppUtils _instance;

    private AppUtils() {
    }

    @NonNull
    public static synchronized AppUtils getInstance() {
        if (null == _instance) _instance = new AppUtils();
        return _instance;
    }

    public final boolean hasInternet(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public final void setStatusBarColor(Activity activity, int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, statusBarColor));
            window.requestFeature(window.FEATURE_NO_TITLE);
            window.requestFeature(Window.FEATURE_PROGRESS);
            window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public final void glideImage(Context context, String imgUrl, ImageView imageView) {
        final RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.color.purple_100)
                .error(android.R.color.holo_red_light)
                .encodeQuality(40)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(context).load(imgUrl)
                .apply(requestOptions)
                .into(imageView);
    }

    public final void showSnack(
            @NonNull final View view,
            @NonNull final String message,
            @NonNull final String actionButtonText,
            @Nullable final Callable<Void> voidFunction) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(actionButtonText, v -> {
                    try {
                        voidFunction.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .show();
    }

    public final void networkStateListener(
            @NonNull final Context context,
            @Nullable final Callable<Void> onlineWifiFunction,
            @Nullable final Callable<Void> onlineMobileFunction,
            @Nullable final Callable<Void> offlineFunction) {

        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                if (!hasActiveInternetConnection(context)) {
                    runOnUiThread(() -> {
                        try {
                            offlineFunction.call();
                        } catch (Exception ignored) {
                        }
                    });
                    return;
                }

                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    runOnUiThread(() -> {
                        try {
                            onlineWifiFunction.call();
                        } catch (Exception ignored) {
                        }
                    });
                }

                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    runOnUiThread(() -> {
                        try {
                            onlineMobileFunction.call();
                        } catch (Exception ignored) {
                        }
                    });
                }
            }

            @Override
            public void onLost(Network network) {
                runOnUiThread(() -> {
                    try {
                        offlineFunction.call();
                    } catch (Exception ignored) {
                    }
                });
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            final NetworkRequest request = new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }
    }

    // Referred https://stackoverflow.com/
    private boolean hasActiveInternetConnection(@NonNull final Context context) {
        if (!hasInternet(context)) return false;

        try {
            final URL url = new URL("http://clients3.google.com/generate_204");
            final HttpURLConnection connection = (HttpURLConnection) (url).openConnection();
            connection.setRequestProperty("User-Agent", "Android");
            connection.setRequestProperty("Connection", "close");
            connection.setConnectTimeout(5000);
            connection.connect();
            return (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT && connection.getContentLength() == 0);
        } catch (IOException e) {
            Log.e(TAG, "Error checking internet connection", e);
        }

        return false;
    }
}