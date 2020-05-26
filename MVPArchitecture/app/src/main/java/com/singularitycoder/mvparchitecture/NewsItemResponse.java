package com.singularitycoder.mvparchitecture;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public List<NewsSubItemArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<NewsSubItemArticle> articles) {
        this.articles = articles;
    }
}