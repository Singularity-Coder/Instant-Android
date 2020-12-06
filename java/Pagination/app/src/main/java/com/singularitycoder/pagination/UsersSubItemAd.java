package com.singularitycoder.pagination;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UsersSubItemAd {

    @SerializedName("company")
    @Expose
    private String company;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("text")
    @Expose
    private String text;

    public String getCompany() {
        return company;
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }
}
