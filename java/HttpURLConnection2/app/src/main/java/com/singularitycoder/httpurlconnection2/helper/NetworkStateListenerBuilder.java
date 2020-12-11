package com.singularitycoder.httpurlconnection2.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public final class NetworkStateListenerBuilder extends AppCompatActivity {

    private static final String TAG = "NetworkStateListenerBui";
    private final AppUtils appUtils = AppUtils.getInstance();
    private final Context context;
    private Callable<Void> onlineWifiFunction;
    private Callable<Void> onlineMobileFunction;
    private Callable<Void> offlineFunction;

    public NetworkStateListenerBuilder(@NonNull Context context) {
        this.context = context;
    }

    public final NetworkStateListenerBuilder setOnlineWifiFunction(@Nullable Callable<Void> onlineWifiFunction) {
        this.onlineWifiFunction = onlineWifiFunction;
        return this;
    }

    public final NetworkStateListenerBuilder setOnlineMobileFunction(@Nullable Callable<Void> onlineMobileFunction) {
        this.onlineMobileFunction = onlineMobileFunction;
        return this;
    }

    public final NetworkStateListenerBuilder setOfflineFunction(@Nullable Callable<Void> offlineFunction) {
        this.offlineFunction = offlineFunction;
        return this;
    }

    public final NetworkStateListenerBuilder build() {
        networkStateListener();
        return this;
    }

    private final void networkStateListener() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (null == networkInfo) {
            runOnUiThread(() -> {
                try {
                    offlineFunction.call();
                } catch (Exception ignored) {
                }
            });
            return;
        }

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

    // Referred https://stackoverflow.com/ for this method
    private boolean hasActiveInternetConnection(@NonNull final Context context) {
        if (!appUtils.hasInternet(context)) return false;

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
