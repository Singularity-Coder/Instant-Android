package com.singularitycoder.firestoregrocerylist;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class GroceryItemModel implements Serializable {

    private String id;
    private String groceryName, groceryQuantity, timeAdded, epochTimeAdded;

    public GroceryItemModel() {
    }

    public GroceryItemModel(String groceryName, String groceryQuantity, String timeAdded, String epochTimeAdded) {
        this.groceryName = groceryName;
        this.groceryQuantity = groceryQuantity;
        this.timeAdded = timeAdded;
        this.epochTimeAdded = epochTimeAdded;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroceryName() {
        return groceryName;
    }

    public void setGroceryName(String groceryName) {
        this.groceryName = groceryName;
    }

    public String getGroceryQuantity() {
        return groceryQuantity;
    }

    public void setGroceryQuantity(String groceryQuantity) {
        this.groceryQuantity = groceryQuantity;
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
}
