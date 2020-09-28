package com.example.cameraxtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.karumi.dexter.BuildConfig;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

public final class AppUtils {

    @NonNull
    private final String TAG = "AppUtils";

    @Nullable
    private static AppUtils _instance;

    @NonNull
    public static synchronized AppUtils getInstance() {
        if (null == _instance) _instance = new AppUtils();
        return _instance;
    }

    public final File getOutputDirectory(@NonNull final Activity activity, @NonNull final String fileType) {
        final File file = new File(Environment.getExternalStorageDirectory() + "/" + activity.getResources().getString(R.string.app_name) + "/" + fileType + "/");
        if (!file.exists()) file.mkdirs();
        return file;
    }

    public final void addFragment(@NonNull final Activity activity, @Nullable final Bundle bundle, final int parentLayout, @NonNull final Fragment fragment) {
        fragment.setArguments(bundle);
        ((AppCompatActivity) activity).getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(parentLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    public final void checkPermissionsThenDo(Activity activity, Callable<Void> permissionsGrantedFunction, Callable<Void> permissionsDeniedFunction, String... permissionsVarArgsArray) {
        Dexter.withActivity(activity)
                .withPermissions(permissionsVarArgsArray)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            try {
                                permissionsGrantedFunction.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                permissionsDeniedFunction.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog(activity);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public final void showSettingsDialog(Context context) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Give Permissions!");
        builder.setMessage("We need you to grant the permissions for the feature to work!");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            openSettings(context);
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Open device app settings to allow user to enable permissions
    public final void openSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
