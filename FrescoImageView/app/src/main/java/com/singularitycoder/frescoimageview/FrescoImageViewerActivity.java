package com.singularitycoder.frescoimageview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class FrescoImageViewerActivity extends AppCompatActivity {

    private SimpleDraweeView draweeView;
    private Uri uri;
    private TextView tvRotate;
    private ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setUpStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresco_image_viewer);
        init();
        setImageView();
        listeners();
    }


    private void setUpStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  // clear FLAG_TRANSLUCENT_STATUS flag:
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);  // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.setStatusBarColor(Color.parseColor("#000000"));   // change the color
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    private void init() {
        draweeView = findViewById(R.id.img_fresco_full_image);
        tvRotate = findViewById(R.id.tv_rotate);
        ivBack = findViewById(R.id.img_back);
    }


    private void setImageView() {
        // Set Image URL
        uri = Uri.parse(getIntent().getStringExtra("image_url"));
        draweeView.setImageURI(uri);
    }


    private void listeners() {
        tvRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Rotation
                final ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                        .setRotationOptions(RotationOptions.forceRotation(RotationOptions.ROTATE_90))
                        .build();
                draweeView.setController(
                        Fresco.newDraweeControllerBuilder()
                                .setImageRequest(imageRequest)
                                .build());
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrescoImageViewerActivity.this.finish();
            }
        });
    }
}
