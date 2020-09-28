package com.example.cameraxtest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.cameraxtest.databinding.FragmentVideoCameraBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class VideoCameraFragment extends Fragment {

    @NonNull
    private final String TAG = "VideoCameraFragment";

    @NonNull
    private final String DATE_FORMAT_FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS";

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @NonNull
    private final String[] CAMERA_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    @NonNull
    private String cameraFacing = "FRONT";

    @Nullable
    private VideoCapture videoCapture;

    @Nullable
    private File videoOutputDirectory;

    @Nullable
    private ExecutorService cameraExecutor;

    @Nullable
    private Uri savedUri;

    @Nullable
    private FragmentVideoCameraBinding binding;

    public VideoCameraFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVideoCameraBinding.inflate(inflater, container, false);
        final View viewRoot = binding.getRoot();
        initialise();
        appUtils.checkPermissionsThenDo(getActivity(), () -> startVideoCamera("FRONT"), null, CAMERA_PERMISSIONS);
        setUpListeners();
        return viewRoot;
    }

    private void initialise() {
        videoOutputDirectory = getOutputDirectory("VIDEOS");
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    @SuppressLint("RestrictedApi")
    private void setUpListeners() {
        binding.ivStartVideo.setOnClickListener(v -> takeVideo());
        binding.ivBack.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStackImmediate());
        binding.ivStopVideo.setOnClickListener(v -> {
            videoCapture.stopRecording();

            binding.ivStartVideo.setVisibility(View.VISIBLE);
            binding.ivStopVideo.setVisibility(View.GONE);

            binding.conLayViewFinder.setVisibility(View.GONE);
            binding.conLayVideoPreview.setVisibility(View.VISIBLE);

            binding.ivSnappedVideoPreview.setVideoURI(savedUri);
            binding.ivSnappedVideoPreview.start();
        });
        binding.ivFlipCamera.setOnClickListener(view -> {
            if (("FRONT").equals(cameraFacing)) {
                appUtils.checkPermissionsThenDo(getActivity(), () -> startVideoCamera("FRONT"), null, CAMERA_PERMISSIONS);
            }

            if (("BACK").equals(cameraFacing)) {
                appUtils.checkPermissionsThenDo(getActivity(), () -> startVideoCamera("BACK"), null, CAMERA_PERMISSIONS);
            }
        });
        binding.ivCancelVideoCamera.setOnClickListener(view -> getActivity().getSupportFragmentManager().popBackStackImmediate());
        binding.ivConfirmVideo.setOnClickListener(view -> getActivity().getSupportFragmentManager().popBackStackImmediate());
        binding.ivRetakeVideo.setOnClickListener(view -> {
            binding.conLayViewFinder.setVisibility(View.VISIBLE);
            binding.conLayVideoPreview.setVisibility(View.GONE);
        });
    }

    @SuppressLint("RestrictedApi")
    private void takeVideo() {
        binding.ivStartVideo.setVisibility(View.GONE);
        binding.ivStopVideo.setVisibility(View.VISIBLE);
        final File videoFile = new File(videoOutputDirectory, new SimpleDateFormat(DATE_FORMAT_FILENAME, Locale.getDefault()).format(System.currentTimeMillis()) + ".mp4");   // Create time-stamped output file to hold the image
        final VideoCapture.OutputFileOptions outputOptions = new VideoCapture.OutputFileOptions.Builder(videoFile).build();   // Create output options object which contains file + metadata
        videoCapture.startRecording(outputOptions, ContextCompat.getMainExecutor(getContext()), new VideoCapture.OnVideoSavedCallback() {
            @Override
            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                savedUri = Uri.fromFile(videoFile);
                String msg = "Video capture succeeded: " + savedUri;
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                Log.d(TAG, msg);
            }

            @Override
            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                Log.e(TAG, "Video capture failed: " + message, cause.getCause());
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private Void startVideoCamera(@NonNull final String cameraFacing) {
        final ListenableFuture cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        cameraProviderFuture.addListener(() -> {
            try {
                final ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(getContext()).get(); // Camera provider is now guaranteed to be available

                final Preview preview = new Preview.Builder().build();  // Set up the view finder use case to display camera preview

                videoCapture = new VideoCapture.Builder()
                        .setTargetRotation(Surface.ROTATION_0)
                        .setTargetResolution(new Size(480, 720))
                        .build();    // Set up the capture use case to allow users to take photos

                CameraSelector cameraSelector = null;
                if (("FRONT").equals(cameraFacing)) {
                    this.cameraFacing = "BACK";
                    cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;    // Choose the camera by requiring a lens facing
                }

                if (("BACK").equals(cameraFacing)) {
                    this.cameraFacing = "FRONT";
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;    // Choose the camera by requiring a lens facing
                }

                preview.setSurfaceProvider(binding.previewViewFinder.getSurfaceProvider());     // Connect the preview use case to the previewView

                cameraProvider.unbindAll();

                final Camera camera = cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        videoCapture);  // Attach use cases to the camera with the same lifecycle owner

                CameraInfo cameraInfo = camera.getCameraInfo();

            } catch (InterruptedException | ExecutionException ignored) {
            }
        }, ContextCompat.getMainExecutor(getContext()));
        return null;
    }

    private File getOutputDirectory(@NonNull final String fileType) {
        final File file = new File(Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name) + "/" + fileType + "/");
        if (!file.exists()) file.mkdirs();
        return file;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraExecutor.shutdown();
    }
}