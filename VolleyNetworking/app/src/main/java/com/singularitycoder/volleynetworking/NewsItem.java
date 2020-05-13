package com.singularitycoder.volleynetworking;

public class NewsItem {

    private String strSource;
    private String strAuthor;
    private String strTitle;
    private String strDescription;
    private String strUrlToImage;
    private String strPublishedAt;

    public NewsItem(String strSource, String strAuthor, String strTitle, String strDescription, String strUrlToImage, String strPublishedAt) {
        this.strSource = strSource;
        this.strAuthor = strAuthor;
        this.strTitle = strTitle;
        this.strDescription = strDescription;
        this.strUrlToImage = strUrlToImage;
        this.strPublishedAt = strPublishedAt;
    }

    public String getStrSource() {
        return strSource;
    }

    public String getStrAuthor() {
        return strAuthor;
    }

    public String getStrTitle() {
        return strTitle;
    }

    public String getStrDescription() {
        return strDescription;
    }

    public String getStrUrlToImage() {
        return strUrlToImage;
    }

    public String getStrPublishedAt() {
        return strPublishedAt;
    }
}