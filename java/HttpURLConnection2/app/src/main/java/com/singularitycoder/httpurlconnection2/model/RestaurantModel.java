package com.singularitycoder.httpurlconnection2.model;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.singularitycoder.httpurlconnection2.helper.AppUtils;

import java.util.List;

public final class RestaurantModel {

    public static final class RestaurantResponse {
        @SerializedName("results_found")
        @Expose
        private Integer resultsFound;

        @SerializedName("results_start")
        @Expose
        private Integer resultsStart;

        @SerializedName("results_shown")
        @Expose
        private Integer resultsShown;

        @SerializedName("restaurants")
        @Expose
        private List<Restaurant> restaurants = null;

        public Integer getResultsFound() {
            return resultsFound;
        }

        public Integer getResultsStart() {
            return resultsStart;
        }

        public Integer getResultsShown() {
            return resultsShown;
        }

        public List<Restaurant> getRestaurants() {
            return restaurants;
        }
    }

    public static final class Restaurant {
        @SerializedName("restaurant")
        @Expose
        private Restaurant_ restaurant;

        public Restaurant_ getRestaurant() {
            return restaurant;
        }
    }

    public static final class Restaurant_ {
        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("location")
        @Expose
        private Location location;

        @SerializedName("cuisines")
        @Expose
        private String cuisines;

        @SerializedName("timings")
        @Expose
        private String timings;

        @SerializedName("thumb")
        @Expose
        private String thumb;

        @SerializedName("user_rating")
        @Expose
        private UserRating userRating;

        @SerializedName("featured_image")
        @Expose
        private String featuredImage;

        @SerializedName("phone_numbers")
        @Expose
        private String phoneNumbers;

        @BindingAdapter("android:loadRestaurantImage")
        public static void loadImage(@NonNull final ImageView imageView, @NonNull final String imageUrl) {
            AppUtils.getInstance().loadImage(imageUrl, imageView);
        }

        public String getName() {
            return name;
        }

        public Location getLocation() {
            return location;
        }

        public String getCuisines() {
            return cuisines;
        }

        public String getTimings() {
            return timings;
        }

        public String getThumb() {
            return thumb;
        }

        public UserRating getUserRating() {
            return userRating;
        }

        public String getFeaturedImage() {
            return featuredImage;
        }

        public String getPhoneNumbers() {
            return phoneNumbers;
        }
    }

    public static final class Location {
        @SerializedName("address")
        @Expose
        private String address;

        @SerializedName("latitude")
        @Expose
        private String latitude;

        @SerializedName("longitude")
        @Expose
        private String longitude;


        public String getAddress() {
            return address;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }
    }

    public static final class UserRating {
        @SerializedName("aggregate_rating")
        @Expose
        private String aggregateRating;

        @SerializedName("rating_text")
        @Expose
        private String ratingText;

        @SerializedName("votes")
        @Expose
        private Integer votes;

        public String getAggregateRating() {
            return aggregateRating;
        }

        public String getRatingText() {
            return ratingText;
        }

        public Integer getVotes() {
            return votes;
        }
    }
}
