package com.singularitycoder.frescoimageview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private String strImageUrl = "https://cdn.pixabay.com/photo/2018/03/29/11/59/snow-3272072_960_720.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showMePhoto(View view) {
        Intent intent = new Intent(MainActivity.this, FrescoImageViewerActivity.class);
        intent.putExtra("image_url", strImageUrl);
        startActivity(intent);
    }
}
