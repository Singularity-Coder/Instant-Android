package com.singularitycoder.recyclerviewcustomheader;

public class PhotoGalleryItem {
    int intPhoto;

    // Large Photo
    public PhotoGalleryItem(int intPhoto) {
        this.intPhoto = intPhoto;
    }

    // Small Photo
    public PhotoGalleryItem(int intPhoto, String empty) {
        this.intPhoto = intPhoto;
    }

    public int getIntPhoto() {
        return intPhoto;
    }

    public void setIntPhoto(int intPhoto) {
        this.intPhoto = intPhoto;
    }
}
