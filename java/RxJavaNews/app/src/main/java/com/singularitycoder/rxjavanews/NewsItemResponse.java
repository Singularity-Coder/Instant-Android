package com.singularitycoder.rxjavanews;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsItemResponse {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("totalResults")
    @Expose
    private Integer totalResults;

    @SerializedName("articles")
    @Expose
    private List<NewsSubItemArticle> articles = null;

    public List<NewsSubItemArticle> getArticles() {
        return articles;
    }
}