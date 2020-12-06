package com.singularitycoder.receivedatafromotherapps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private TextView tvReceiveText;
    private ImageView ivReceiveImage1, ivReceiveImage2, ivReceiveImage3;
    private ImageView ivReceiveDoc1, ivReceiveDoc2, ivReceiveDoc3;
    private VideoView vvReceiveVideo1, vvReceiveVideo2, vvReceiveVideo3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        verifyStoragePermissions(this);
        receivedIntentData();
    }

    private void initializeViews() {
        tvReceiveText = findViewById(R.id.tv_receive_text);
        ivReceiveImage1 = findViewById(R.id.iv_receive_image_1);
        ivReceiveImage2 = findViewById(R.id.iv_receive_image_2);
        ivReceiveImage3 = findViewById(R.id.iv_receive_image_3);
        vvReceiveVideo1 = findViewById(R.id.vv_receive_video_1);
        vvReceiveVideo2 = findViewById(R.id.vv_receive_video_2);
        vvReceiveVideo3 = findViewById(R.id.vv_receive_video_3);
        ivReceiveDoc1 = findViewById(R.id.iv_receive_doc_1);
        ivReceiveDoc2 = findViewById(R.id.iv_receive_doc_2);
        ivReceiveDoc3 = findViewById(R.id.iv_receive_doc_3);
    }

    // Grant Read Write Device Storage permission check
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // If permission not granted then prompt user
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void receivedIntentData() {
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        // Single Item
        if (Intent.ACTION_SEND.equals(action) && null != type) {

            // Text
            if ("text/plain".equals(type)) {
                handleReceivedText(intent);
            }

            // Image
            if (type.startsWith("image/")) {
                handleReceivedImage(intent);
            }

            // Video
            if (type.startsWith("video/")) {
                handleReceivedVideo(intent);
            }

            // PDF Document
            if (type.startsWith("application/pdf")) {
                handleReceivedDocument(intent);
            }
        }

        // Multiple Items
        if (Intent.ACTION_SEND_MULTIPLE.equals(action) && null != type) {

            // Multiple Images
            if (type.startsWith("image/")) {
                handleMultipleReceivedImages(intent);
            }

            // Multiple Videos
            if (type.startsWith("video/")) {
                handleMultipleReceivedVideos(intent);
            }

            // Multiple PDF Documents
            if (type.startsWith("application/pdf")) {
                handleMultipleReceivedDocuments(intent);
            }
        }
    }

    // Text
    private void handleReceivedText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (null != sharedText) {
            tvReceiveText.setText(sharedText);
        }
    }

    // Single Image
    private void handleReceivedImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (null != imageUri) {
            AsyncTask.execute(() -> {
                try {
                    Bitmap getBitmapFromUri = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(getBitmapFromUri, getBitmapFromUri.getWidth(), getBitmapFromUri.getHeight(), true);
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    runOnUiThread(() -> ivReceiveImage1.setImageBitmap(scaledBitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // Single Video
    private void handleReceivedVideo(Intent intent) {
        Uri videoUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (null != videoUri) {
            playVideo(vvReceiveVideo1, videoUri);
        }
    }

    // Single Document
    private void handleReceivedDocument(Intent intent) {
        Uri docUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (null != docUri) {
            openDoc(ivReceiveDoc1, docUri);
        }
    }

    // Multiple Images
    private void handleMultipleReceivedImages(Intent intent) {
        ArrayList<Uri> imageUriList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        ArrayList<Bitmap> bitmapFromUriList = new ArrayList<>();
        if (null != imageUriList) {
            if (imageUriList.size() <= 3) {
                for (int i = 0; i < imageUriList.size(); i++) {
                    final int finalI = i;
                    AsyncTask.execute(() -> {
                        try {
                            InputStream imageStream = this.getContentResolver().openInputStream(imageUriList.get(finalI));
                            Bitmap getBitmapFromUri = BitmapFactory.decodeStream(imageStream);
                            bitmapFromUriList.add(getBitmapFromUri);
                            runOnUiThread(() -> {
                                if (finalI == 0) {
                                    ivReceiveImage1.setImageBitmap(bitmapFromUriList.get(0));
                                }

                                if (finalI == 1) {
                                    ivReceiveImage2.setImageBitmap(bitmapFromUriList.get(1));
                                }

                                if (finalI == 2) {
                                    ivReceiveImage3.setImageBitmap(bitmapFromUriList.get(2));
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Max 3 Images!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Multiple Videos
    private void handleMultipleReceivedVideos(Intent intent) {
        ArrayList<Uri> videoUriList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (null != videoUriList) {
            if (videoUriList.size() <= 3) {
                for (int i = 0; i < videoUriList.size(); i++) {
                    if (i == 0) {
                        playVideo(vvReceiveVideo1, videoUriList.get(0));
                    }

                    if (i == 1) {
                        playVideo(vvReceiveVideo2, videoUriList.get(1));
                    }

                    if (i == 2) {
                        playVideo(vvReceiveVideo3, videoUriList.get(2));
                    }
                }
            } else {
                Toast.makeText(this, "Max 3 Videos!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Multiple Documents
    private void handleMultipleReceivedDocuments(Intent intent) {
        ArrayList<Uri> documentUriList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (null != documentUriList) {
            if (documentUriList.size() <= 3) {
                for (int i = 0; i < documentUriList.size(); i++) {
                    if (i == 0) {
                        openDoc(ivReceiveDoc1, documentUriList.get(0));
                    }

                    if (i == 1) {
                        openDoc(ivReceiveDoc2, documentUriList.get(1));
                    }

                    if (i == 2) {
                        openDoc(ivReceiveDoc3, documentUriList.get(2));
                    }
                }
            } else {
                Toast.makeText(this, "Max 3 Docs!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playVideo(VideoView vvReceiveVideo, Uri videoUri) {
        vvReceiveVideo.setVideoURI(videoUri);
        vvReceiveVideo.requestFocus();
        vvReceiveVideo.start();
    }

    private void openDoc(ImageView ivReceiveDoc, Uri docUri) {
        // If doc uri exists then the image view color changes to colorAccent and on clicking the image it directs u to a default pdf viewer
        ivReceiveDoc.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        ivReceiveDoc.setOnClickListener(view -> {
            Intent intentUrl = new Intent(Intent.ACTION_VIEW);
            intentUrl.setDataAndType(docUri, "application/pdf");
            intentUrl.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentUrl);
        });
    }
}
