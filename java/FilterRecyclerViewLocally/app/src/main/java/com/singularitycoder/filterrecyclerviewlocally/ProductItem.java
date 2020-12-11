package com.singularitycoder.filterrecyclerviewlocally;

public final class ProductItem {

    private String name;
    private String price;
    private String category;
    private String date;
    private String time;
    private String proximity;

    public ProductItem(String name, String price, String category, String date, String time, String proximity) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.date = date;
        this.time = time;
        this.proximity = proximity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProximity() {
        return proximity;
    }

    public void setProximity(String proximity) {
        this.proximity = proximity;
    }
}
