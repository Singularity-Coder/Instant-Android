package com.example.cameraxtest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cameraxtest.databinding.FragmentVideoCameraBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class VideoCameraFragment extends Fragment implements ListDialogFragment.ListDialogListener {

    @NonNull
    private final String TAG = "VideoCameraFragment";

    @NonNull
    private final String DATE_FORMAT_FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS";

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @NonNull
    private final String[] CAMERA_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};

    @NonNull
    private final Size[] resolutionSizes = {
            new Size(360, 480),
            new Size(480, 720),
            new Size(720, 1280),
            new Size(1080, 1920),
            new Size(1440, 2560),
            new Size(2160, 3840),
            new Size(4320, 7680)};

    @NonNull
    private final String[] resolutionArray = {
            "360p",
            "480p",
            "720p (HD Ready)",
            "1080p (Full HD)",
            "1440p (Quad HD)",
            "2160p (Ultra HD or 4K)",
            "4320p (8K)"};

    @NonNull
    private String cameraFacing = "FRONT";

    @Nullable
    private VideoCapture videoCapture;

    @Nullable
    private File videoOutputDirectory;

    @Nullable
    private Size defaultResolution = new Size(480, 720);

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
        videoOutputDirectory = appUtils.getOutputDirectory(getActivity(), "VIDEOS");
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    @SuppressLint("RestrictedApi")
    private void setUpListeners() {
        binding.conLayVideoRoot.setOnClickListener(v -> {
        });
        binding.ivStartVideo.setOnClickListener(v -> takeVideo());
        binding.ivBack.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStackImmediate());
        binding.tvResolution.setOnClickListener(v -> dialogVideoResolutions());
        binding.ivStopVideo.setOnClickListener(v -> {
            videoCapture.stopRecording();
            binding.ivStartVideo.setVisibility(View.VISIBLE);
            binding.ivStopVideo.setVisibility(View.GONE);
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
        binding.tvElapsedVideoTime.setVisibility(View.VISIBLE);
        final File videoFile = new File(videoOutputDirectory, new SimpleDateFormat(DATE_FORMAT_FILENAME, Locale.getDefault()).format(System.currentTimeMillis()) + ".mp4");   // Create time-stamped output file to hold the image
        final VideoCapture.OutputFileOptions outputOptions = new VideoCapture.OutputFileOptions.Builder(videoFile).build();   // Create output options object which contains file + metadata
        videoCapture.startRecording(outputOptions, ContextCompat.getMainExecutor(getContext()), new VideoCapture.OnVideoSavedCallback() {
            @Override
            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                savedUri = Uri.fromFile(videoFile);

                binding.conLayViewFinder.setVisibility(View.GONE);
                binding.conLayVideoPreview.setVisibility(View.VISIBLE);

                binding.vvSnappedVideoPreview.setVideoURI(savedUri);
                binding.vvSnappedVideoPreview.setMediaController(new MediaController(getContext()));
                binding.vvSnappedVideoPreview.requestFocus();
                binding.vvSnappedVideoPreview.start();

                final String msg = "Video capture succeeded: " + savedUri;
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
                        .setTargetResolution(defaultResolution)
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

                final CameraInfo cameraInfo = camera.getCameraInfo();

            } catch (InterruptedException | ExecutionException ignored) {
            }
        }, ContextCompat.getMainExecutor(getContext()));
        return null;
    }

    private void dialogVideoResolutions() {
        final Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "list");
        bundle.putString("KEY_LIST_DIALOG_TYPE", "Video Resolutions");
        bundle.putString("KEY_TITLE", "Choose Video Resolution");
        bundle.putString("KEY_CONTEXT_TYPE", "fragment");
        bundle.putString("KEY_CONTEXT_OBJECT", "VideoCameraFragment");
        bundle.putStringArray("KEY_LIST", resolutionArray);

        final int REQUEST_CODE_LIST_DIALOG_FRAGMENT_VIDEO_RESOLUTIONS = 777;
        final DialogFragment dialogFragment = new ListDialogFragment();
        dialogFragment.setTargetFragment(this, REQUEST_CODE_LIST_DIALOG_FRAGMENT_VIDEO_RESOLUTIONS);
        dialogFragment.setArguments(bundle);
        final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        final Fragment previousFragment = getActivity().getSupportFragmentManager().findFragmentByTag("TAG_ListDialogFragment");
        if (previousFragment != null) fragmentTransaction.remove(previousFragment);
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_ListDialogFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraExecutor.shutdown();
    }

    @Override
    public void onListDialogItemClick(DialogFragment dialog, String listItemText, String listTitle) {
        if (("Choose Video Resolution").equals(listTitle)) {
            String[] resStrArr = listItemText.split(" ", 0);
            binding.tvResolution.setText(resStrArr[0]);
            for (int i = 0; i < resolutionArray.length; i++) {
                if (resolutionArray[i].equals(listItemText)) {
                    defaultResolution = resolutionSizes[i];
                }
            }
            appUtils.checkPermissionsThenDo(getActivity(), () -> startVideoCamera("FRONT"), null, CAMERA_PERMISSIONS);
        }
    }
}