package com.example.cameraxtest;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.extensions.BokehImageCaptureExtender;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.extensions.ImageCaptureExtender;
import androidx.camera.extensions.NightImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cameraxtest.databinding.FragmentPhotoCameraBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PhotoCameraFragment extends Fragment implements ListDialogFragment.ListDialogListener {

    @NonNull
    private final String TAG = "PhotoCameraFragment";

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
    private ImageCapture imageCapture;

    @Nullable
    private File imageOutputDirectory;

    @Nullable
    private Size defaultResolution = new Size(480, 720);

    @Nullable
    private ExecutorService cameraExecutor;

    @Nullable
    private FragmentPhotoCameraBinding binding;

    // todo flash
    // todo camera modes
    // todo aspect ratio

    public PhotoCameraFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPhotoCameraBinding.inflate(inflater, container, false);
        final View viewRoot = binding.getRoot();
        initialise();
        appUtils.checkPermissionsThenDo(getActivity(), () -> startImageCamera("FRONT"), null, CAMERA_PERMISSIONS);
        setUpListeners();
        return viewRoot;
    }

    private void initialise() {
        imageOutputDirectory = appUtils.getOutputDirectory(getActivity(), "PHOTOS");
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void setUpListeners() {
        binding.conLayPhotoRoot.setOnClickListener(v -> {
        });
        binding.ivSnapImage.setOnClickListener(view -> takePhoto());
        binding.ivBack.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStackImmediate());
        binding.tvResolution.setOnClickListener(v -> dialogImageResolutions());
        binding.ivFlipCamera.setOnClickListener(view -> {
            if (("FRONT").equals(cameraFacing)) {
                appUtils.checkPermissionsThenDo(getActivity(), () -> startImageCamera("FRONT"), null, CAMERA_PERMISSIONS);
            }

            if (("BACK").equals(cameraFacing)) {
                appUtils.checkPermissionsThenDo(getActivity(), () -> startImageCamera("BACK"), null, CAMERA_PERMISSIONS);
            }
        });
        binding.ivCancelImageCamera.setOnClickListener(view -> getActivity().getSupportFragmentManager().popBackStackImmediate());
        binding.ivConfirmImage.setOnClickListener(view -> getActivity().getSupportFragmentManager().popBackStackImmediate());
        binding.ivRetakeImage.setOnClickListener(view -> {
            binding.conLayViewFinder.setVisibility(View.VISIBLE);
            binding.conLayImagePreview.setVisibility(View.GONE);
        });
    }

    private void takePhoto() {
        final String DATE_FORMAT_FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS";
        final File photoFile = new File(imageOutputDirectory, new SimpleDateFormat(DATE_FORMAT_FILENAME, Locale.getDefault()).format(System.currentTimeMillis()) + ".jpg");   // Create time-stamped output file to hold the image
        final ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();   // Create output options object which contains file + metadata
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(getContext()), new ImageCapture.OnImageSavedCallback() {  // Set up image capture listener, which is triggered after photo has been taken
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                final Uri savedUri = Uri.fromFile(photoFile);
                final String msg = "Photo capture succeeded: " + savedUri;
                final Bitmap bitmap = BitmapFactory.decodeFile(savedUri.getPath());
                binding.ivSnappedImagePreview.setImageBitmap(bitmap);
                binding.conLayViewFinder.setVisibility(View.GONE);
                binding.conLayImagePreview.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                Log.d(TAG, msg);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
            }
        });
    }

    private Void startImageCamera(@NonNull final String cameraFacing) {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        cameraProviderFuture.addListener(() -> {
            try {
                final ProcessCameraProvider cameraProvider = cameraProviderFuture.get(); // Camera provider is now guaranteed to be available

                final Preview preview = new Preview.Builder().build();  // Set up the view finder use case to display camera preview

                final ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();

                final ImageCapture.Builder imageCaptureBuilder = new ImageCapture.Builder();

                imageCapture = imageCaptureBuilder
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(Surface.ROTATION_0)
                        .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                        .setTargetResolution(defaultResolution)
                        .build();    // Set up the capture use case to allow users to take photos

                CameraSelector cameraSelector = null;
                if (("FRONT").equals(cameraFacing)) {
                    this.cameraFacing = "BACK";
                    cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
                }

                if (("BACK").equals(cameraFacing)) {
                    this.cameraFacing = "FRONT";
                    cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                }

                setUpVendorExtensions(imageCaptureBuilder, cameraSelector);

                preview.setSurfaceProvider(binding.previewViewFinder.getSurfaceProvider());     // Connect the preview use case to the previewView

                cameraProvider.unbindAll();

                final Camera camera = cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis,
                        imageCapture);  // Attach use cases to the camera with the same lifecycle owner

                final CameraInfo cameraInfo = camera.getCameraInfo();

            } catch (InterruptedException | ExecutionException ignored) {
            }
        }, ContextCompat.getMainExecutor(getContext()));
        return null;
    }

    private void setUpVendorExtensions(ImageCapture.Builder imageCaptureBuilder, CameraSelector cameraSelector) {
        final HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(imageCaptureBuilder);    // Vendor-Extensions
        final BokehImageCaptureExtender bokehImageCaptureExtender = BokehImageCaptureExtender.create(imageCaptureBuilder);
        final NightImageCaptureExtender nightImageCaptureExtender = NightImageCaptureExtender.create(imageCaptureBuilder);

        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {     // if extension is available
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        } else {
            Toast.makeText(getContext(), "You don't have HDR Mode!", Toast.LENGTH_SHORT).show();
        }

        if (bokehImageCaptureExtender.isExtensionAvailable(cameraSelector)) {     // if extension is available
            bokehImageCaptureExtender.enableExtension(cameraSelector);
        } else {
            Toast.makeText(getContext(), "You don't have Bokeh Mode!", Toast.LENGTH_SHORT).show();
        }

        if (nightImageCaptureExtender.isExtensionAvailable(cameraSelector)) {     // if extension is available
            nightImageCaptureExtender.enableExtension(cameraSelector);
        } else {
            Toast.makeText(getContext(), "You don't have Night Mode!", Toast.LENGTH_SHORT).show();
        }
    }

    private void dialogImageResolutions() {
        final Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "list");
        bundle.putString("KEY_LIST_DIALOG_TYPE", "Image Resolutions");
        bundle.putString("KEY_TITLE", "Choose Image Resolution");
        bundle.putString("KEY_CONTEXT_TYPE", "fragment");
        bundle.putString("KEY_CONTEXT_OBJECT", "PhotoCameraFragment");
        bundle.putStringArray("KEY_LIST", resolutionArray);

        final int REQUEST_CODE_LIST_DIALOG_FRAGMENT_IMAGE_RESOLUTIONS = 666;
        final DialogFragment dialogFragment = new ListDialogFragment();
        dialogFragment.setTargetFragment(this, REQUEST_CODE_LIST_DIALOG_FRAGMENT_IMAGE_RESOLUTIONS);
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
        if (("Choose Image Resolution").equals(listTitle)) {
            String[] resStrArr = listItemText.split(" ", 0);
            binding.tvResolution.setText(resStrArr[0]);
            for (int i = 0; i < resolutionArray.length; i++) {
                if (resolutionArray[i].equals(listItemText)) {
                    defaultResolution = resolutionSizes[i];
                }
            }
            appUtils.checkPermissionsThenDo(getActivity(), () -> startImageCamera("FRONT"), null, CAMERA_PERMISSIONS);
        }
    }
}