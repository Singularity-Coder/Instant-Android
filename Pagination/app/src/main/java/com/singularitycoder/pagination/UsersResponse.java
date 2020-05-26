package com.singularitycoder.pagination;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UsersResponse {

    @SerializedName("page")
    @Expose
    private String page;

    @SerializedName("per_page")
    @Expose
    private Integer perPage;

    @SerializedName("total")
    @Expose
    private Integer total;

    @SerializedName("total_pages")
    @Expose
    private Integer total_pages;

    @SerializedName("data")
    @Expose
    private List<UsersSubItemData> data = null;

    @SerializedName("ad")
    @Expose
    private UsersSubItemAd ad;

    public List<UsersSubItemData> getData() {
        return data;
    }

    public UsersSubItemAd getAd() {
        return ad;
    }

    public Integer getTotal() {
        return total;
    }
}