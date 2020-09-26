package com.example.cameraxtest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.cameraxtest.databinding.ActivityMainBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.karumi.dexter.BuildConfig;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MainActivity extends AppCompatActivity {

    @NonNull
    private final String TAG = "MainActivity";

    @NonNull
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";

    @NonNull
    private String cameraFacing = "FRONT";

    @NonNull
    private final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    @Nullable
    private ImageCapture imageCapture;

    @Nullable
    private File outputDirectory;

    @Nullable
    private ExecutorService cameraExecutor;

    @Nullable
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBarStuff();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialise();
        checkPermissions(this, () -> startCamera("FRONT"), null, REQUIRED_PERMISSIONS);
        setUpListeners();
    }

    private void initialise() {
        outputDirectory = getOutputDirectory();
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void setUpListeners() {
        binding.ivSnap.setOnClickListener(view -> takePhoto());
        binding.ivFlipCamera.setOnClickListener(view -> {
            if (("FRONT").equals(cameraFacing)) {
                checkPermissions(this, () -> startCamera("FRONT"), null, REQUIRED_PERMISSIONS);
            }

            if (("BACK").equals(cameraFacing)) {
                checkPermissions(this, () -> startCamera("BACK"), null, REQUIRED_PERMISSIONS);
            }
        });
        binding.ivCancelCamera.setOnClickListener(view -> {

        });
        binding.ivConfirmImage.setOnClickListener(view -> {

        });
        binding.ivRetakeImage.setOnClickListener(view -> {
            binding.conLayViewFinder.setVisibility(View.VISIBLE);
            binding.conLayPreview.setVisibility(View.GONE);
        });
    }

    private void hideTitleBarStuff() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // Hide Title
        getSupportActionBar().hide();   // Hide Title Bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // Hide Status Bar
    }

    private void takePhoto() {
        // Get a stable reference of the modifiable image capture use case
//        imageCapture
        File photoFile = new File(outputDirectory, new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg");   // Create time-stamped output file to hold the image
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();   // Create output options object which contains file + metadata
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {  // Set up image capture listener, which is triggered after photo has been taken
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = Uri.fromFile(photoFile);
                String msg = "Photo capture succeeded: " + savedUri;
                Bitmap bitmap = BitmapFactory.decodeFile(savedUri.getPath());
                binding.ivSnappedImagePreview.setImageBitmap(bitmap);
                binding.conLayViewFinder.setVisibility(View.GONE);
                binding.conLayPreview.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                Log.d(TAG, msg);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
            }
        });
    }

    private Void startCamera(String key) {
        final ListenableFuture cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                final ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(this).get(); // Camera provider is now guaranteed to be available

                final Preview preview = new Preview.Builder().build();  // Set up the view finder use case to display camera preview

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(Surface.ROTATION_0)
                        .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                        .setTargetResolution(new Size(480, 720))
//                        .setTargetResolution(new Size(360, 480))
                        .build();    // Set up the capture use case to allow users to take photos

                CameraSelector cameraSelector = null;
//                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
                if (("FRONT").equals(key)) {
                    cameraFacing = "BACK";
                    cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;    // Choose the camera by requiring a lens facing
                }

                if (("BACK").equals(key)) {
                    cameraFacing = "FRONT";
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;    // Choose the camera by requiring a lens facing
                }

                preview.setSurfaceProvider(binding.previewViewFinder.getSurfaceProvider());     // Connect the preview use case to the previewView
//                preview.setSurfaceProvider(previewView.createSurfaceProvider(camera.getCameraInfo()));

                cameraProvider.unbindAll();

                final Camera camera = cameraProvider.bindToLifecycle(
                        ((LifecycleOwner) this),
                        cameraSelector,
                        preview,
                        imageCapture);  // Attach use cases to the camera with the same lifecycle owner

                CameraInfo cameraInfo = camera.getCameraInfo();

            } catch (InterruptedException | ExecutionException ignored) {
            }
        }, ContextCompat.getMainExecutor(this));
        return null;
    }

    private File getOutputDirectory() {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name) + "/");
        if (!file.exists()) file.mkdirs();
        return file;
    }

    public final void checkPermissions(Activity activity, Callable<Void> permissionsGrantedFunction, Callable<Void> permissionsDeniedFunction, String... permissionsVarArgsArray) {
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

    public void showSettingsDialog(Context context) {
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
    public void openSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}