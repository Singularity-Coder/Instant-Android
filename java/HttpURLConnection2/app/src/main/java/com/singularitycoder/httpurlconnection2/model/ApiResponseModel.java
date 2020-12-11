package com.singularitycoder.httpurlconnection2.model;

public final class ApiResponseModel {
    private int responseCode;
    private RestaurantModel.RestaurantResponse restaurantResponse;
    private ErrorModel errorModel;

    public ApiResponseModel() {
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public RestaurantModel.RestaurantResponse getRestaurantResponse() {
        return restaurantResponse;
    }

    public void setRestaurantResponse(RestaurantModel.RestaurantResponse restaurantResponse) {
        this.restaurantResponse = restaurantResponse;
    }

    public ErrorModel getErrorModel() {
        return errorModel;
    }

    public void setErrorModel(ErrorModel errorModel) {
        this.errorModel = errorModel;
    }
}