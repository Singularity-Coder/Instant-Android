package com.singularitycoder.recyclerviewcustomheader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PhotoGalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "PhotoGalleryAdapter";

    private static final int GALLERY_LARGE_IMAGE = 0;
    private static final int GALLERY_SMALL_IMAGE = 1;

    ArrayList<PhotoGalleryItem> photoList;
    Context context;

    public PhotoGalleryAdapter(ArrayList<PhotoGalleryItem> photoList, Context context) {
        this.photoList = photoList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == GALLERY_SMALL_IMAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_small_photo, parent, false);
            return new PhotoSmallViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_large_photo, parent, false);
            return new PhotoLargeViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PhotoGalleryItem photoGalleryItem = photoList.get(position);
        if (null != holder) {
            if (holder instanceof PhotoSmallViewHolder) {
                PhotoSmallViewHolder photoSmallViewHolder = (PhotoSmallViewHolder) holder;
                photoSmallViewHolder.ivPhotoSmall.setImageResource(photoGalleryItem.getIntPhoto());
            }

            if (holder instanceof PhotoLargeViewHolder) {
                PhotoLargeViewHolder photoLargeViewHolder = (PhotoLargeViewHolder) holder;
                photoLargeViewHolder.ivPhotoLarge.setImageResource(photoGalleryItem.getIntPhoto());
            }
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return GALLERY_LARGE_IMAGE;
        } else {
            return GALLERY_SMALL_IMAGE;
        }
    }

    class PhotoSmallViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhotoSmall;

        public PhotoSmallViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhotoSmall = itemView.findViewById(R.id.img_small_photo);
        }
    }

    class PhotoLargeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhotoLarge;

        public PhotoLargeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhotoLarge = itemView.findViewById(R.id.img_large_photo);
        }
    }
}
