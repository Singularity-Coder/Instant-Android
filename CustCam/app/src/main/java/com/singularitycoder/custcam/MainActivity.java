package com.singularitycoder.custcam;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private final String SAVED_INSTANCE_KEY_IS_SNAPPING = "is_snapping";

    private Camera camera;
    private ImageView ivCameraImage;
    private SurfaceView surfaceViewCameraPreview;
    private Button btnSnapPic, btnFrontCamera, btnRearCamera;
    private byte[] byteArrayCameraImageData;
    private DisplayMetrics displayMetrics;
    private SurfaceHolder surfaceHolder;
    private Bitmap bitmap;

    private boolean isSnapping;
    private boolean isInPreview;
    private int DEVICE_WIDTH = 0;
    private int DEVICE_HEIGHT = 0;
    private int cameraCount = 0;
    private int currentCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBarStuff();
        setContentView(R.layout.activity_main);
        checkPermissions(this, null, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
        initializeViews();
        initializeData();
        getDeviceWidthHeight();
        hideImageViewByDefault();
        setUpSurfaceViewHolder();
        clickListeners();
    }

    private void hideTitleBarStuff() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // Hide Title
        getSupportActionBar().hide();   // Hide Title Bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // Hide Status Bar
    }

    public void checkPermissions(Activity activity, Callable<Void> permissionsGrantedFunction, String... permissionsArray) {
        Dexter.withActivity(activity)
                .withPermissions(permissionsArray)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            try {
                                permissionsGrantedFunction.call();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Give Permissions!");
        builder.setMessage("We need you to grant the permissions for the camera feature to work!");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            openDeviceSettings(context);
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void openDeviceSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void initializeViews() {
        ivCameraImage = findViewById(R.id.iv_camera_image);
        btnSnapPic = findViewById(R.id.btn_snap);
        btnFrontCamera = findViewById(R.id.btn_front_camera);
        btnRearCamera = findViewById(R.id.btn_rear_camera);
        surfaceViewCameraPreview = findViewById(R.id.surface_view_camera_preview);
    }

    private void initializeData() {
        displayMetrics = new DisplayMetrics();
        cameraCount = Camera.getNumberOfCameras();
        isSnapping = true;
    }

    private void getDeviceWidthHeight() {
        DEVICE_HEIGHT = displayMetrics.heightPixels;
        DEVICE_WIDTH = displayMetrics.widthPixels;
    }

    private void hideImageViewByDefault() {
        ivCameraImage.setVisibility(View.INVISIBLE);
    }

    private void setUpSurfaceViewHolder() {
        surfaceHolder = surfaceViewCameraPreview.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
                if (camera != null) {
                    try {
                        if (isInPreview) {
                            camera.stopPreview();
                        }
                        Camera.Parameters parameters = camera.getParameters();
                        parameters.set("jpeg-quality", 100);
                        parameters.setPictureFormat(PixelFormat.JPEG);
                        parameters.setPreviewSize(DEVICE_WIDTH, DEVICE_HEIGHT);
                        parameters.setPictureSize(DEVICE_WIDTH, DEVICE_HEIGHT);
                        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

                        if (display.getRotation() == Surface.ROTATION_0) {
                            parameters.setPreviewSize(height, width);
                            camera.setDisplayOrientation(90);
                            camera.setPreviewDisplay(surfaceHolder);
                        }

                        if (display.getRotation() == Surface.ROTATION_90) {
                            parameters.setPreviewSize(width, height);
                            camera.setDisplayOrientation(0);
                            camera.setPreviewDisplay(surfaceHolder);
                        }

                        if (display.getRotation() == Surface.ROTATION_180) {
                            parameters.setPreviewSize(height, width);
                            camera.setDisplayOrientation(270);
                            camera.setPreviewDisplay(surfaceHolder);
                        }

                        if (display.getRotation() == Surface.ROTATION_270) {
                            parameters.setPreviewSize(width, height);
                            camera.setDisplayOrientation(180);
                            camera.setPreviewDisplay(surfaceHolder);
                        }
                        camera.setParameters(parameters);
                        camera.setPreviewDisplay(surfaceHolder);
                        if (isSnapping) {
                            camera.startPreview();
                            isInPreview = true;
                        }
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "Unable to start camera preview.", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void clickListeners() {
        btnSnapPic.setOnClickListener(view -> checkPermissions(this, () -> snap(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA));
        btnFrontCamera.setOnClickListener(view -> checkPermissions(this, () -> frontFacingCamera(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA));
        btnRearCamera.setOnClickListener(view -> checkPermissions(this, () -> rearFacingCamera(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA));
    }

    private Void frontFacingCamera() {
        if (camera != null) {
            if (cameraCount > 1) {
                btnRearCamera.setEnabled(true);

                Camera.CameraInfo currentCameraInfo = new Camera.CameraInfo();
                if (currentCameraId == currentCameraInfo.CAMERA_FACING_FRONT) {
                    btnFrontCamera.setEnabled(false);
                }

                camera.release();

                currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                Toast.makeText(this, "Front Cam", Toast.LENGTH_SHORT).show();

                camera = Camera.open(currentCameraId);
                setCameraDisplayOrientation(MainActivity.this, currentCameraId, camera);
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.startPreview();

                    Camera.Parameters parameters = camera.getParameters();
                    parameters.set("jpeg-quality", 100);
                    parameters.setPictureFormat(PixelFormat.JPEG);
                    parameters.setPreviewSize(DEVICE_WIDTH, DEVICE_HEIGHT);
                    parameters.setPictureSize(DEVICE_WIDTH, DEVICE_HEIGHT);
                    camera.setParameters(parameters);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private Void rearFacingCamera() {
        if (camera != null) {
            if (cameraCount > 1) {
                btnFrontCamera.setEnabled(true);

                Camera.CameraInfo currentCameraInfo = new Camera.CameraInfo();
                if (currentCameraId == currentCameraInfo.CAMERA_FACING_BACK) {
                    btnRearCamera.setEnabled(false);
                }

                camera.release();

                currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                Toast.makeText(this, "Rear Cam", Toast.LENGTH_SHORT).show();

                camera = Camera.open(currentCameraId);
                setCameraDisplayOrientation(MainActivity.this, currentCameraId, camera);
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.startPreview();

                    Camera.Parameters parameters = camera.getParameters();
                    parameters.set("jpeg-quality", 100);
                    parameters.setPictureFormat(PixelFormat.JPEG);
                    parameters.setPreviewSize(DEVICE_WIDTH, DEVICE_HEIGHT);
                    parameters.setPictureSize(DEVICE_WIDTH, DEVICE_HEIGHT);
                    camera.setParameters(parameters);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private Bitmap rotateBitmap(Bitmap bitmap, String imageFileLocation) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(imageFileLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();

        if (orientation == ExifInterface.ORIENTATION_UNDEFINED) {
            Camera.CameraInfo currentCameraInfo = new Camera.CameraInfo();
            if (currentCameraId == currentCameraInfo.CAMERA_FACING_FRONT) {
                matrix.postRotate(-90);
            } else {
                matrix.postRotate(90);
            }
        } else {
            matrix.postRotate(0);
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        return rotatedBitmap;
    }

    private void setupImageDisplay() {
        AsyncTask.execute(() -> saveImageToFile());
        bitmap = BitmapFactory.decodeByteArray(byteArrayCameraImageData, 0, byteArrayCameraImageData.length);
        ivCameraImage.setImageBitmap(rotateBitmap(bitmap, getRealPathFromURI(getImageUri(this, bitmap))));
        camera.stopPreview();
        surfaceViewCameraPreview.setVisibility(View.INVISIBLE);
        ivCameraImage.setVisibility(View.VISIBLE);
        btnFrontCamera.setVisibility(View.GONE);
        btnRearCamera.setVisibility(View.GONE);
        btnSnapPic.setText("RESNAP");
        btnSnapPic.setOnClickListener(view -> setupImageCapture());
    }

    private void setupImageCapture() {
        ivCameraImage.setVisibility(View.INVISIBLE);
        surfaceViewCameraPreview.setVisibility(View.VISIBLE);
        btnFrontCamera.setVisibility(View.VISIBLE);
        btnRearCamera.setVisibility(View.VISIBLE);
        btnSnapPic.setText("SNAP");
        btnSnapPic.setOnClickListener(view -> checkPermissions(this, () -> snap(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA));
    }

    private Void snap() {
        camera.takePicture(null, null, (bytes, camera) -> {
            byteArrayCameraImageData = bytes;
            setupImageDisplay();
        });
        return null;
    }

    private void saveImageToFile() {
        File saveFile = openFileOfImage();
        if (saveFile != null) {
            if (bitmap != null) {
                FileOutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(saveFile);
                    if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error saving image to file.", Toast.LENGTH_LONG).show());
                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Saved image to: " + saveFile.getPath(), Toast.LENGTH_LONG).show());
                    }
                    outStream.close();
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error saving image to file.", Toast.LENGTH_LONG).show());
                }
            }
        } else {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error opening file for saving image.", Toast.LENGTH_LONG).show());
        }
    }

    private File openFileOfImage() {
        File imageDirectory = null;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            imageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCustomCameraImages");
            if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
                imageDirectory = null;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm", Locale.getDefault());
                return new File(imageDirectory.getPath() + File.separator + "image_" + dateFormat.format(new Date()) + ".png");
            }
        }
        return null;
    }

    public Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(SAVED_INSTANCE_KEY_IS_SNAPPING, isSnapping);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isSnapping = savedInstanceState.getBoolean(SAVED_INSTANCE_KEY_IS_SNAPPING, byteArrayCameraImageData == null);
        if (byteArrayCameraImageData != null) {
            setupImageDisplay();
        } else {
            setupImageCapture();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (camera == null) {
            try {
                camera = Camera.open();
                camera.setPreviewDisplay(surfaceViewCameraPreview.getHolder());
                if (isSnapping) {
                    camera.startPreview();
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Error opening camera.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}