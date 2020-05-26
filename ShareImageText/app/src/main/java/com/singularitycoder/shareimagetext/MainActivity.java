package com.singularitycoder.shareimagetext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String strImageUrl = "https://cdn.pixabay.com/photo/2018/03/29/11/59/snow-3272072_960_720.jpg";
    private ImageView ivFullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivFullImage = findViewById(R.id.iv_image);
    }

    public void shareImageText(View view) {
        shareProfile(strImageUrl, ivFullImage);
    }


    public void shareTextOnly(View view) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Saring is Caring");
        share.putExtra(Intent.EXTRA_TEXT, "Sharing to share how to share the sharing!");
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        startActivity(Intent.createChooser(share, "Share to"));
    }

    private void shareProfile(final String imageUrl, final ImageView imageView) {
        if (!("").equals(imageUrl)) {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {

                                Glide.with(MainActivity.this)
                                        .asBitmap()
                                        .load(imageUrl)
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                imageView.setImageBitmap(resource);
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                            }
                                        });

                                Uri bmpUri = getLocalBitmapUri(imageView);
                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                sharingIntent.setType("image/.*");
                                sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Saring is Caring");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Sharing to share how to share the sharing!");
                                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        } else {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, "Saring is Caring");
            share.putExtra(Intent.EXTRA_TEXT, "Sharing to share how to share the sharing!");
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(Intent.createChooser(share, "Share to"));
        }
    }

    private Uri getLocalBitmapUri(ImageView imageView) {
        // Get Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // Will fail for API >= 24, better to use FileProvider
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
