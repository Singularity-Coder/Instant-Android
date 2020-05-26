package com.singularitycoder.mvvmarchitecture;

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

    @Expose(deserialize = false, serialize = false)
    private String responseStatus;

    @Expose(deserialize = false, serialize = false)
    private String responseMessage;

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

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}