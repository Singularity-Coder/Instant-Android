package com.singularitycoder.googvizapi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.DominantColorsAnnotation;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageProperties;
import com.google.api.services.vision.v1.model.SafeSearchAnnotation;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String CLOUD_VISION_API_KEY = "YOUR_GOOGLE_CLOUD_VISION_API_KEY";

    private final String[] visionSearchTypeArray = new String[]{"LABEL_DETECTION", "LOGO_DETECTION", "SAFE_SEARCH_DETECTION", "IMAGE_PROPERTIES", "LANDMARK_DETECTION", "FACE_DETECTION", "TEXT_DETECTION", "DOCUMENT_TEXT_DETECTION"};
    private final String[] visionSearchTypeAliasArray = new String[]{"Labels", "Logos", "Kid Safety", "Image Properties", "Landmarks", "Faces", "Text", "Lot's of Text"};
    private final ArrayList<String> imageFilePathsStringArray = new ArrayList<>();

    private Button selectImage;
    private ProgressBar progressLoading;
    private ImageView ivImage;
    private TextView tvVisionApiResult;
    private TextView tvSelectSearchType;
    private Feature imageFeature;
    private Bitmap bitmap;
    private String searchType = visionSearchTypeArray[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        setContentView(R.layout.activity_main);
        initializeViews();
        setInitialFeature();
        setDefaultSearchType();
        clickListeners();
    }

    private void setStatusBarColor() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    private void initializeViews() {
        tvSelectSearchType = findViewById(R.id.tv_select_search_type);
        selectImage = findViewById(R.id.btn_select_image);
        progressLoading = findViewById(R.id.progress_loading);
        ivImage = findViewById(R.id.iv_image);
        tvVisionApiResult = findViewById(R.id.tv_vision_api_result);
    }

    private void setInitialFeature() {
        imageFeature = new Feature();
        imageFeature.setType(visionSearchTypeArray[0]);
        imageFeature.setMaxResults(10);
    }

    private void setDefaultSearchType() {
        tvSelectSearchType.setText(visionSearchTypeArray[0]);
        searchType = visionSearchTypeArray[0];
        imageFeature.setType(searchType);
        if (bitmap != null) {
            getCloudVisionApiData(bitmap, imageFeature);
        }
    }

    private void clickListeners() {
        tvSelectSearchType.setOnClickListener(view -> dialogSearchType());
        selectImage.setOnClickListener(view -> takePictureFromCamera());
    }

    private void takePictureFromCamera() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog(MainActivity.this);
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
        builder.setMessage("We need you to grant the permissions for the camera image to work!");
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

    private void showImagePickerOptions() {
        FilePickerBuilder.getInstance()
                .setSelectedFiles(imageFilePathsStringArray)
                .setActivityTheme(R.style.LibAppTheme)
                .setMaxCount(1)
                .pickPhoto(this);
    }

    private void dialogSearchType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search for");
        builder.setItems(visionSearchTypeAliasArray, (dialog, which) -> {
            for (int i = 0; i < visionSearchTypeArray.length; i++) {
                if (which == i) {
                    tvSelectSearchType.setText(visionSearchTypeArray[i]);
                    searchType = visionSearchTypeArray[i];
                    imageFeature.setType(searchType);
                    if (bitmap != null) {
                        getCloudVisionApiData(bitmap, imageFeature);
                    }
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private void getCloudVisionApiData(final Bitmap bitmap, final Feature feature) {
        progressLoading.setVisibility(View.VISIBLE);

        final List<Feature> featureList = new ArrayList<>();
        featureList.add(feature);

        final List<AnnotateImageRequest> annotateImageRequestList = new ArrayList<>();
        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
        annotateImageRequest.setFeatures(featureList);
        annotateImageRequest.setImage(getEncodedImage(bitmap));
        annotateImageRequestList.add(annotateImageRequest);

        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
                    VisionRequestInitializer visionRequestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);
                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(visionRequestInitializer);
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(annotateImageRequestList);
                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);
                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "Failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "Failed to make API request because of other IOException " + e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details!";
            }

            protected void onPostExecute(String result) {
                tvVisionApiResult.setText(result);
                progressLoading.setVisibility(View.GONE);
            }
        }.execute();
    }

    @NonNull
    private Image getEncodedImage(Bitmap bitmap) {
        Image base64EncodedImage = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        base64EncodedImage.encodeContent(imageBytes);
        return base64EncodedImage;
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        AnnotateImageResponse imageResponses = response.getResponses().get(0);
        List<EntityAnnotation> entityAnnotations;
        List<FaceAnnotation> faceAnnotations;
        List<AnnotateImageRequest> textAnnotations;

        String visionApiResult = "";
        switch (searchType) {
            case "LABEL_DETECTION":
                entityAnnotations = imageResponses.getLabelAnnotations();
                visionApiResult = getAnnotationData(entityAnnotations);
                break;
            case "LOGO_DETECTION":
                entityAnnotations = imageResponses.getLogoAnnotations();
                visionApiResult = getAnnotationData(entityAnnotations);
                break;
            case "SAFE_SEARCH_DETECTION":
                SafeSearchAnnotation annotation = imageResponses.getSafeSearchAnnotation();
                visionApiResult = getImageAnnotationData(annotation);
                break;
            case "IMAGE_PROPERTIES":
                ImageProperties imageProperties = imageResponses.getImagePropertiesAnnotation();
                visionApiResult = getImagePropertyData(imageProperties);
                break;
            case "LANDMARK_DETECTION":
                entityAnnotations = imageResponses.getLandmarkAnnotations();
                visionApiResult = getAnnotationData(entityAnnotations);
                break;
            case "FACE_DETECTION":
                faceAnnotations = imageResponses.getFaceAnnotations();
                visionApiResult = getFaceAnnotationData(faceAnnotations);
                break;
            case "TEXT_DETECTION":
                entityAnnotations = imageResponses.getTextAnnotations();
                visionApiResult = getAnnotationData(entityAnnotations);
                break;
            case "DOCUMENT_TEXT_DETECTION":
                entityAnnotations = imageResponses.getTextAnnotations();
                visionApiResult = getAnnotationData(entityAnnotations);
                break;
        }
        return visionApiResult;
    }

    private String getImageAnnotationData(SafeSearchAnnotation annotation) {
        String visionApiResult = "";

        if (null != annotation) {
            visionApiResult = "Adult: "
                    + annotation.getAdult()
                    + "\n\nMedical: "
                    + annotation.getMedical()
                    + "\n\nSpoofed: "
                    + annotation.getSpoof()
                    + "\n\nViolence: "
                    + annotation.getViolence();
        } else {
            visionApiResult = "Cannot Identify!";
        }
        return visionApiResult;
    }

    private String getImagePropertyData(ImageProperties imageProperties) {
        String visionApiResult = "";

        if (null != imageProperties) {
            DominantColorsAnnotation colors = imageProperties.getDominantColors();
            for (int i = 0; i < colors.getColors().size(); i++) {
                visionApiResult = visionApiResult
                        + "Number of Objects: "
                        + colors.getColors().size()
                        + "\n\nObject Number: "
                        + i
                        + "\n\nColor: "
                        + colors.getColors().get(i).getColor()
                        + "\n\nPixel Fraction: "
                        + colors.getColors().get(i).getPixelFraction()
                        + "\n\nRed: "
                        + colors.getColors().get(i).getColor().getRed()
                        + "\n\nGreen: "
                        + colors.getColors().get(i).getColor().getGreen()
                        + "\n\nBlue: "
                        + colors.getColors().get(i).getColor().getBlue()
                        + "\n\nScore: "
                        + colors.getColors().get(i).getScore()
                        + "\n\n---------- Other Possibilities -----------\n\n";
            }
        } else {
            visionApiResult = "Cannot Identify!";
        }
        return visionApiResult;
    }

    private String getAnnotationData(List<EntityAnnotation> entityAnnotation) {
        String visionApiResult = "";

        if (null != entityAnnotation) {
            for (int i = 0; i < entityAnnotation.size(); i++) {
                visionApiResult = visionApiResult
                        + "Number of Objects: "
                        + entityAnnotation.size()
                        + "\n\nObject Number: "
                        + i
                        + "\n\nDescription: "
                        + entityAnnotation.get(i).getDescription()
                        + "\n\nScore: "
                        + entityAnnotation.get(i).getScore()
                        + "\n\nLocale: "
                        + entityAnnotation.get(i).getLocale()
                        + "\n\nMid: "
                        + entityAnnotation.get(i).getMid()
                        + "\n\nBounding Poly: "
                        + entityAnnotation.get(i).getBoundingPoly()
                        + "\n\nConfidence: "
                        + entityAnnotation.get(i).getConfidence()
                        + "\n\nLocations: "
                        + entityAnnotation.get(i).getLocations()
                        + "\n\nProperties: "
                        + entityAnnotation.get(i).getProperties()
                        + "\n\nTopicality: "
                        + entityAnnotation.get(i).getTopicality()
                        + "\n\n---------- Other Possibilities -----------\n\n";
            }
        } else {
            visionApiResult = "Cannot Identify!";
        }
        return visionApiResult;
    }

    private String getFaceAnnotationData(List<FaceAnnotation> faceAnnotations) {
        String visionApiResult = "";

        if (null != faceAnnotations) {
            for (int i = 0; i < faceAnnotations.size(); i++) {
                visionApiResult = visionApiResult
                        + "Number of Faces: "
                        + faceAnnotations.size()
                        + "\n\nFace Number: "
                        + i
                        + "\n\nAnger Likelihood: "
                        + faceAnnotations.get(i).getAngerLikelihood()
                        + "\n\nBlurred Likelihood: "
                        + faceAnnotations.get(i).getBlurredLikelihood()
                        + "\n\nGet Headwear Likelihood: "
                        + faceAnnotations.get(i).getHeadwearLikelihood()
                        + "\n\nJoy Likelihood: "
                        + faceAnnotations.get(i).getJoyLikelihood()
                        + "\n\nSorrow Likelihood: "
                        + faceAnnotations.get(i).getSorrowLikelihood()
                        + "\n\nSurprise Likelihood: "
                        + faceAnnotations.get(i).getSurpriseLikelihood()
                        + "\n\nUnder Exposed Likelihood: "
                        + faceAnnotations.get(i).getUnderExposedLikelihood()
                        + "\n\nBounding Poly: "
                        + faceAnnotations.get(i).getBoundingPoly()
                        + "\n\nDetection Confidence: "
                        + faceAnnotations.get(i).getDetectionConfidence()
                        + "\n\nFd Bounding Poly: "
                        + faceAnnotations.get(i).getFdBoundingPoly()
                        + "\n\nLandmarking Confidence: "
                        + faceAnnotations.get(i).getLandmarkingConfidence()
                        + "\n\nLandmarks: "
                        + faceAnnotations.get(i).getLandmarks()
                        + "\n\nPan Angle: "
                        + faceAnnotations.get(i).getPanAngle()
                        + "\n\nRoll Angle: "
                        + faceAnnotations.get(i).getRollAngle()
                        + "\n\nTilt Angle: "
                        + faceAnnotations.get(i).getTiltAngle()
                        + "\n\n---------- Other Possibilities -----------\n\n";
            }

        } else {
            visionApiResult = "Cannot Identify!";
        }
        return visionApiResult;
    }

    private Bitmap getBitmap(String filePath) {
        File imageFile = new File(filePath);
        Bitmap myBitmap = null;
        if (imageFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        }
        return myBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == FilePickerConst.REQUEST_CODE_PHOTO && resultCode == Activity.RESULT_OK) {
            imageFilePathsStringArray.addAll(Objects.requireNonNull(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)));
            String newImagePath = imageFilePathsStringArray.get(0);
            Log.d(TAG, "onActivityResult: filepath: " + newImagePath);
            bitmap = getBitmap(newImagePath);
            ivImage.setImageBitmap(bitmap);
            getCloudVisionApiData(bitmap, imageFeature);
            imageFilePathsStringArray.clear();
        }
    }
}