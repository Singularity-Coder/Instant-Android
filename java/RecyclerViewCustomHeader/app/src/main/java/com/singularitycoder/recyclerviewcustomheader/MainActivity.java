package com.singularitycoder.recyclerviewcustomheader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerPhotoGallery;
    private PhotoGalleryAdapter photoGalleryAdapter;
    private GridLayoutManager photoGalleryLayoutManager;
    private ArrayList<PhotoGalleryItem> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatuBarColor();
        setContentView(R.layout.activity_main);
        setUpRecyclerView();
    }

    private void setStatuBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            // Clear FLAG_TRANSLUCENT_STATUS flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setUpRecyclerView() {
        recyclerPhotoGallery = findViewById(R.id.recycler_view);
        photoList = new ArrayList<>();
        photoList.add(new PhotoGalleryItem(R.drawable.p1));
        photoList.add(new PhotoGalleryItem(R.drawable.p2, ""));
        photoList.add(new PhotoGalleryItem(R.drawable.p3, ""));
        photoList.add(new PhotoGalleryItem(R.drawable.p4, ""));
        photoList.add(new PhotoGalleryItem(R.drawable.p4, ""));
        photoList.add(new PhotoGalleryItem(R.drawable.p1, ""));
        photoList.add(new PhotoGalleryItem(R.drawable.p2, ""));
        photoList.add(new PhotoGalleryItem(R.drawable.p2, ""));
        photoList.add(new PhotoGalleryItem(R.drawable.p3, ""));
        photoList.add(new PhotoGalleryItem(R.drawable.p4, ""));
        photoList.add(new PhotoGalleryItem(R.drawable.p4, ""));
        photoList.add(new PhotoGalleryItem(R.drawable.p1, ""));
        photoList.add(new PhotoGalleryItem(R.drawable.p2, ""));
        photoList.add(new PhotoGalleryItem(R.drawable.p2, ""));
        photoList.add(new PhotoGalleryItem(R.drawable.p3, ""));
        photoList.add(new PhotoGalleryItem(R.drawable.p4, ""));

        photoGalleryAdapter = new PhotoGalleryAdapter(photoList, this);
        photoGalleryLayoutManager = new GridLayoutManager(this, 3);
        photoGalleryLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });
        recyclerPhotoGallery.setLayoutManager(photoGalleryLayoutManager);
        recyclerPhotoGallery.setAdapter(photoGalleryAdapter);
    }
}
