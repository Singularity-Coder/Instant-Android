package com.singularitycoder.parallaxtabs;

public class FriendsModel {

    private String name;
    private String description;
    private String firstLetter;

    public FriendsModel(String name, String description) {
        this.name = name;
        this.firstLetter = String.valueOf(name.charAt(0));
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }
}