package com.singularitycoder.httpurlconnection2.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.singularitycoder.httpurlconnection2.BuildConfig;

import java.util.List;
import java.util.concurrent.Callable;

public final class PermissionsBuilder {

    @NonNull
    private Activity activity;

    @Nullable
    private Callable<Void> permissionsGrantedFunction;

    @Nullable
    private Callable<Void> permissionsDeniedFunction;

    @Nullable
    private String[] permissionsVarArgsArray;

    public PermissionsBuilder(@NonNull Activity activity) {
        this.activity = activity;
    }

    public final PermissionsBuilder setPermissionsGrantedFunction(@Nullable Callable<Void> permissionsGrantedFunction) {
        this.permissionsGrantedFunction = permissionsGrantedFunction;
        return this;
    }

    public final PermissionsBuilder setPermissionsDeniedFunction(@Nullable Callable<Void> permissionsDeniedFunction) {
        this.permissionsDeniedFunction = permissionsDeniedFunction;
        return this;
    }

    public final PermissionsBuilder setPermissions(@Nullable String... permissionsVarArgsArray) {
        this.permissionsVarArgsArray = permissionsVarArgsArray;
        return this;
    }

    public final PermissionsBuilder build() {
        checkPermissionsAndDoWork();
        return this;
    }

    private void checkPermissionsAndDoWork() {
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

    private void showSettingsDialog(@NonNull final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Give Permissions!");
        builder.setMessage("We need you to grant the permissions for the features to work!");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            openSettings(context);
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Open device app settings to allow user to enable permissions
    private void openSettings(@NonNull final Context context) {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
