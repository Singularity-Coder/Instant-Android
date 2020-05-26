package com.singularitycoder.recordscreenlikecrazy;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {

    // pause recording
    // start pause stop in notification bar
    // start recording as a service, launch notification n finish activity
    // List of saved recordings with date - onClick new activity to show full view video
    // Save all uris in room or sqlite

    private final String TAG = "MainActivity";
    private final int REQUEST_CODE_SCREEN_RECORDING = 11;
    private final int ORIENTATION_ROTATION_0 = 90;
    private final int ORIENTATION_ROTATION_90 = 0;
    private final int ORIENTATION_ROTATION_180 = 270;
    private final int ORIENTATION_ROTATION_270 = 180;

    private int screenDensity;
    private int DEVICE_WIDTH = 0;
    private int DEVICE_HEIGHT = 0;
    private int cameraCount = 0;
    private long timeLeftInMillis5Sec = 5000;
    private String strVideoUri = "";

    private Camera camera;
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private MediaProjectionCallback mediaProjectionCallback;
    private MediaRecorder mediaRecorder;
    private DisplayMetrics displayMetrics;
    private SurfaceView surfaceViewCameraPreview;
    private Camera.CameraInfo currentCameraInfo;
    private CountDownTimer countDownTimer;

    private ConstraintLayout conLayHome;
    private ToggleButton toggleStartStopRecording, toggleFlashOnOff;
    private VideoView vvScreenRecordingVideo;
    private Button btnFrontCamera, btnRearCamera, btnStopCamera;
    private TextView tvCountDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBarStuff();
        setContentView(R.layout.activity_main);
        initializeViews();
        initializeData();
        getDeviceMeasurements();
        getIntentData();
        clickListeners();
        basicNotification();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void hideTitleBarStuff() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // Hide Title
        getSupportActionBar().hide();   // Hide Title Bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // Hide Status Bar
    }

    private void initializeViews() {
        vvScreenRecordingVideo = findViewById(R.id.vv_screen_recording_video);
        toggleStartStopRecording = findViewById(R.id.toggle_btn_start_stop_recording);
        toggleFlashOnOff = findViewById(R.id.toggle_btn_flash);
        conLayHome = findViewById(R.id.con_lay_home);
        surfaceViewCameraPreview = findViewById(R.id.surface_view_camera_preview);
        btnFrontCamera = findViewById(R.id.btn_front_camera);
        btnRearCamera = findViewById(R.id.btn_rear_camera);
        btnStopCamera = findViewById(R.id.btn_stop_camera);
        tvCountDown = findViewById(R.id.tv_countdown);
    }

    private void initializeData() {
        displayMetrics = new DisplayMetrics();
        mediaRecorder = new MediaRecorder();
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        cameraCount = Camera.getNumberOfCameras();
        currentCameraInfo = new Camera.CameraInfo();
    }

    private void getDeviceMeasurements() {
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenDensity = displayMetrics.densityDpi;
        DEVICE_HEIGHT = displayMetrics.heightPixels;
        DEVICE_WIDTH = displayMetrics.widthPixels;
    }

    private void clickListeners() {
        toggleStartStopRecording.setOnClickListener(view -> checkPermissions(this, () -> startStopScreenRecording(view), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO));
        toggleFlashOnOff.setOnClickListener(view -> checkPermissions(this, () -> turnOnOffFlash(view), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO));
        btnFrontCamera.setOnClickListener(view -> checkPermissions(this, () -> openCamera(currentCameraInfo.CAMERA_FACING_FRONT), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA));
        btnRearCamera.setOnClickListener(view -> checkPermissions(this, () -> openCamera(currentCameraInfo.CAMERA_FACING_BACK), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA));
        btnStopCamera.setOnClickListener(view -> checkPermissions(this, () -> stopCamera(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA));
    }

    private void checkPermissions(Activity activity, Callable<Void> permissionsGrantedFunction, String... permissionsArray) {
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

    private void showSettingsDialog(Context context) {
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

    private void openDeviceSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private Void openCamera(int cameraId) {
        stopCamera();
        surfaceViewCameraPreview.setVisibility(View.VISIBLE);
        vvScreenRecordingVideo.setVisibility(View.GONE);
        if (camera == null) {

            if (cameraId == currentCameraInfo.CAMERA_FACING_BACK) {
                camera = Camera.open(cameraId);
            }

            if (cameraId == currentCameraInfo.CAMERA_FACING_FRONT) {
                if (cameraCount > 1) {
                    camera = Camera.open(cameraId);
                } else {
                    Toast.makeText(this, "You don't have a front cam!", Toast.LENGTH_SHORT).show();
                }
            }

            try {
                camera.setPreviewDisplay(surfaceViewCameraPreview.getHolder());

                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(DEVICE_WIDTH, DEVICE_HEIGHT);
                setCameraRotation(parameters);

                camera.setParameters(parameters);
                camera.startPreview();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Error Opening Camera!", Toast.LENGTH_LONG).show();
            }
        }
        return null;
    }

    private void setCameraRotation(Camera.Parameters parameters) {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getRotation() == Surface.ROTATION_0) {
            parameters.setPreviewSize(DEVICE_HEIGHT, DEVICE_WIDTH);
            camera.setDisplayOrientation(90);
        }

        if (display.getRotation() == Surface.ROTATION_90) {
            parameters.setPreviewSize(DEVICE_WIDTH, DEVICE_HEIGHT);
            camera.setDisplayOrientation(0);
        }

        if (display.getRotation() == Surface.ROTATION_180) {
            parameters.setPreviewSize(DEVICE_HEIGHT, DEVICE_WIDTH);
            camera.setDisplayOrientation(270);
        }

        if (display.getRotation() == Surface.ROTATION_270) {
            parameters.setPreviewSize(DEVICE_WIDTH, DEVICE_HEIGHT);
            camera.setDisplayOrientation(180);
        }
    }

    private Void stopCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
            surfaceViewCameraPreview.setVisibility(View.GONE);
        }
        return null;
    }

    private void startTimer() {
        tvCountDown.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(timeLeftInMillis5Sec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis5Sec = millisUntilFinished;
                tvCountDown.setText(String.valueOf(timeLeftInMillis5Sec / 1000));
            }

            @Override
            public void onFinish() {
                tvCountDown.setVisibility(View.GONE);
            }
        }.start();
    }

    private Void startStopScreenRecording(View view) {
        vvScreenRecordingVideo.setVisibility(View.VISIBLE);
        surfaceViewCameraPreview.setVisibility(View.GONE);
        if (((ToggleButton) view).isChecked()) {
            toggleStartStopRecording.setChecked(true);
            startTimer();
            startMediaRecorder();
            startScreenRecording();
        } else {
            if (tvCountDown.getVisibility() == View.VISIBLE) {
                countDownTimer.cancel();
                tvCountDown.setVisibility(View.GONE);
            }
            stopMediaRecorder();
            stopScreenRecording();
            setVideoToVideoView();
        }
        return null;
    }

    private Void turnOnOffFlash(View view) {
        boolean isFlashAvailable = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (isFlashAvailable) {
            if (((ToggleButton) view).isChecked()) {
                toggleFlashOnOff.setChecked(true);

                if (currentCameraInfo.facing == currentCameraInfo.CAMERA_FACING_BACK) {
                    camera = Camera.open(currentCameraInfo.CAMERA_FACING_BACK);
                }

                if (currentCameraInfo.facing == currentCameraInfo.CAMERA_FACING_FRONT) {
                    camera = Camera.open(currentCameraInfo.CAMERA_FACING_FRONT);
                }

                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();
            } else {
                camera.stopPreview();
                camera.release();
            }
        } else {
            Toast.makeText(this, "You don't have flash! You have an ancient phone!", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private void createFolderToSaveScreenRecordings() {
        File screenRecordingsFileDirectory = new File(Environment.getExternalStorageDirectory() + "/RecordScreenLikeCrazy/");
        screenRecordingsFileDirectory.mkdirs();
        strVideoUri = screenRecordingsFileDirectory.getAbsolutePath() + "/" + "ScreenRecording_" + new SimpleDateFormat("dd-MM-yyyy-hh_mm_ss").format(new Date()) + ".mp4";
        Toast.makeText(this, "Saved in " + screenRecordingsFileDirectory, Toast.LENGTH_LONG).show();
    }

    private void startMediaRecorder() {
        try {
            createFolderToSaveScreenRecordings();

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(strVideoUri);

            mediaRecorder.setVideoSize(DEVICE_WIDTH, DEVICE_HEIGHT);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mediaRecorder.setVideoFrameRate(30);

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            if (0 == rotation + 90) {
                mediaRecorder.setOrientationHint(ORIENTATION_ROTATION_0);
            }

            if (90 == rotation + 90) {
                mediaRecorder.setOrientationHint(ORIENTATION_ROTATION_90);
            }

            if (180 == rotation + 90) {
                mediaRecorder.setOrientationHint(ORIENTATION_ROTATION_180);
            }

            if (270 == rotation + 90) {
                mediaRecorder.setOrientationHint(ORIENTATION_ROTATION_270);
            }
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startScreenRecording() {
        if (mediaProjection == null) {
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_SCREEN_RECORDING);
            return;
        }
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
    }

    private VirtualDisplay createVirtualDisplay() {
        return mediaProjection.createVirtualDisplay("MainActivity", DEVICE_WIDTH, DEVICE_HEIGHT, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder.getSurface(), null, null);
    }

    private void stopMediaRecorder() {
        mediaRecorder.stop();
        mediaRecorder.reset();
    }

    private void stopScreenRecording() {
        if (virtualDisplay == null) {
            return;
        }
        virtualDisplay.release();
        destroyMediaProjection();
    }

    private void destroyMediaProjection() {
        if (mediaProjection != null) {
            mediaProjection.unregisterCallback(mediaProjectionCallback);
            mediaProjection.stop();
            mediaProjection = null;
        }
    }

    private void setVideoToVideoView() {
        stopCamera();
        surfaceViewCameraPreview.setVisibility(View.GONE);
        vvScreenRecordingVideo.setVisibility(View.VISIBLE);
        vvScreenRecordingVideo.setVideoURI(Uri.parse(strVideoUri));
        vvScreenRecordingVideo.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCREEN_RECORDING && resultCode == RESULT_OK && null != data) {
            mediaProjectionCallback = new MediaProjectionCallback();
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
            mediaProjection.registerCallback(mediaProjectionCallback, null);
            virtualDisplay = createVirtualDisplay();
            mediaRecorder.start();
        }
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            if (toggleStartStopRecording.isChecked()) {
                toggleStartStopRecording.setChecked(false);
                mediaRecorder.stop();
                mediaRecorder.reset();
            }
            mediaProjection = null;
            stopScreenRecording();
            super.onStop();
        }
    }

    private void basicNotification() {
        // Get Custom Notification Layouts
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_large);

        // Small Notification Intents
        Intent smallNotifStartIntent = new Intent(this, MainActivity.class);
        smallNotifStartIntent.putExtra("notifIntent", "SMALL START");
        PendingIntent smallNotifStartPendingIntent = PendingIntent.getActivity(this, 1, smallNotifStartIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent smallNotifStopIntent = new Intent(this, MainActivity.class);
        smallNotifStopIntent.putExtra("notifIntent", "SMALL STOP");
        PendingIntent smallNotifStopPendingIntent = PendingIntent.getActivity(this, 1, smallNotifStopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Expanded Notification Intents
        Intent largeNotifStartIntent = new Intent(this, MainActivity.class);
        largeNotifStartIntent.putExtra("notifIntent", "LARGE START");
        PendingIntent largeNotifStartPendingIntent = PendingIntent.getActivity(this, 1, largeNotifStartIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent largeNotifStopIntent = new Intent(this, MainActivity.class);
        largeNotifStopIntent.putExtra("notifIntent", "LARGE STOP");
        PendingIntent largeNotifStopPendingIntent = PendingIntent.getActivity(this, 1, largeNotifStopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Small Notification Click Events
        notificationLayout.setOnClickPendingIntent(R.id.btn_small_notif_start, smallNotifStartPendingIntent);
        notificationLayout.setOnClickPendingIntent(R.id.btn_small_notif_stop, smallNotifStopPendingIntent);

        // Expanded Notification Click Events
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.btn_large_notif_start, largeNotifStartPendingIntent);
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.btn_large_notif_stop, largeNotifStopPendingIntent);

        // Notification For Android 8 and above. // Apply the layouts to the notification
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "channelId1";
            int id = 0;
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setChannelId(CHANNEL_ID)
                            .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                            .setCustomContentView(notificationLayout)
                            .setCustomBigContentView(notificationLayoutExpanded);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Title 1", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                manager.notify(id, mBuilder.build());
            }
        } else {
            // Notification btw Android 4 and Android 8. No channel ID. Doesn't accept Notification compat custom views
            int id = 0;
            Notification.Builder mBuilder = new Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher);  // Necessary - sets small icon
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.notify(id, mBuilder.build());
            }
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (null != intent) {
            String notifIntent = intent.getStringExtra("notifIntent");

            if (("SMALL START").equals(notifIntent)) {
                startService(new Intent(getBaseContext(), MyScreenRecordingService.class));
                Toast.makeText(MainActivity.this, "Small start got hit", Toast.LENGTH_SHORT).show();
            }

            if (("SMALL STOP").equals(notifIntent)) {
                stopService(new Intent(getBaseContext(), MyScreenRecordingService.class));
                Toast.makeText(MainActivity.this, "Small stop got hit", Toast.LENGTH_SHORT).show();
            }

            if (("LARGE START").equals(notifIntent)) {
                startService(new Intent(getBaseContext(), MyScreenRecordingService.class));
                Toast.makeText(MainActivity.this, "Small start got hit", Toast.LENGTH_SHORT).show();
            }

            if (("LARGE STOP").equals(notifIntent)) {
                stopService(new Intent(getBaseContext(), MyScreenRecordingService.class));
                Toast.makeText(MainActivity.this, "Small stop got hit", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class MyScreenRecordingService extends Service {
        MainActivity mainActivity = new MainActivity();

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            mainActivity.toggleStartStopRecording.setChecked(true);
            mainActivity.startTimer();
            mainActivity.startMediaRecorder();
            mainActivity.startScreenRecording();
            Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
            return START_STICKY;
        }

        @Override
        public void onDestroy() {
            if (mainActivity.tvCountDown.getVisibility() == View.VISIBLE) {
                mainActivity.countDownTimer.cancel();
                mainActivity.tvCountDown.setVisibility(View.GONE);
            }
            mainActivity.stopMediaRecorder();
            mainActivity.stopScreenRecording();
            mainActivity.setVideoToVideoView();
            super.onDestroy();
            Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        }
    }
}