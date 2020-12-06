package com.singularitycoder.firebasestorage;

import android.net.Uri;

import com.google.firebase.firestore.Exclude;

public class FileItem {
    private String id;
    private String fileType;
    private String fileName;
    private String timeAdded;
    private String epochTimeAdded;
    private String fileUrl;
    private Uri fileUri;

    public FileItem() {
    }

    public FileItem(String fileType, String fileName, String timeAdded, String epochTimeAdded, Uri fileUri) {
        this.fileType = fileType;
        this.fileName = fileName;
        this.timeAdded = timeAdded;
        this.epochTimeAdded = epochTimeAdded;
        this.fileUri = fileUri;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(String timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getEpochTimeAdded() {
        return epochTimeAdded;
    }

    public void setEpochTimeAdded(String epochTimeAdded) {
        this.epochTimeAdded = epochTimeAdded;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Exclude
    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }
}
