package com.singularitycoder.retrofitresponseobject.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Build;
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
import com.singularitycoder.retrofitresponseobject.R;

public final class AppUtils extends AppCompatActivity {

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
                .placeholder(R.color.teal_200)
                .error(android.R.color.holo_red_light)
                .encodeQuality(40)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(context).load(imgUrl)
                .apply(requestOptions)
                .into(imageView);
    }
}