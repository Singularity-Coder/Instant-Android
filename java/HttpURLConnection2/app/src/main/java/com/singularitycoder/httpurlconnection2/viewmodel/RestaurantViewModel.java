package com.singularitycoder.httpurlconnection2.viewmodel;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.singularitycoder.httpurlconnection2.helper.AppConstants;
import com.singularitycoder.httpurlconnection2.helper.StateMediator;
import com.singularitycoder.httpurlconnection2.helper.UiState;
import com.singularitycoder.httpurlconnection2.model.ApiResponseModel;
import com.singularitycoder.httpurlconnection2.model.ErrorModel;
import com.singularitycoder.httpurlconnection2.model.RestaurantModel;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class RestaurantViewModel extends ViewModel {

    @NonNull
    private final String TAG = "RestaurantViewModel";

    @NonNull
    public final LiveData<StateMediator<Object, UiState, String, String>> getRestaurantsFromZomato(
            @NonNull final Activity activity,
            @NonNull final String requestUrl,
            @NonNull final String apiKey) throws IllegalArgumentException {
        final StateMediator<Object, UiState, String, String> stateMediator = new StateMediator<>();
        final MutableLiveData<StateMediator<Object, UiState, String, String>> mutableLiveData = new MutableLiveData<>();

        stateMediator.set(null, UiState.LOADING, "Loading...", null);
        mutableLiveData.postValue(stateMediator);

        backgroundTaskWithAsync(activity, requestUrl, apiKey, stateMediator, mutableLiveData);

        return mutableLiveData;
    }

    private void backgroundTaskWithAsync(
            @NonNull final Activity activity,
            @NonNull final String requestUrl,
            @NonNull final String apiKey,
            @NonNull final StateMediator<Object, UiState, String, String> stateMediator,
            @NonNull final MutableLiveData<StateMediator<Object, UiState, String, String>> mutableLiveData) {
        AsyncTask.execute(() -> {
            try {
                final int TIMEOUT_IN_20_SECONDS = 20000;
                final int TIMEOUT_IN_30_SECONDS = 30000;
                final URL url = new URL(requestUrl);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("user-key", apiKey);
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Accept-Encoding", "identity");
                connection.setInstanceFollowRedirects(false);
                connection.setReadTimeout(TIMEOUT_IN_20_SECONDS /* milliseconds */);
                connection.setConnectTimeout(TIMEOUT_IN_30_SECONDS /* milliseconds */);
                connection.connect();

                final int responseCode = connection.getResponseCode();
                final Gson gson = new GsonBuilder().setLenient().excludeFieldsWithoutExposeAnnotation().create();
                final ApiResponseModel apiResponseModel = new ApiResponseModel();
                apiResponseModel.setResponseCode(responseCode);

                if (HttpURLConnection.HTTP_OK == responseCode) {
                    try {
                        final InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                        final String jsonString = inputStreamToString(connection, inputStream);
                        final JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
                        final RestaurantModel.RestaurantResponse restaurantResponse = gson.fromJson(jsonObject, RestaurantModel.RestaurantResponse.class);
                        apiResponseModel.setRestaurantResponse(restaurantResponse);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        final InputStream errorStream = connection.getErrorStream();
                        final String errorJsonString = inputStreamToString(connection, errorStream);
                        final JsonObject errorJsonObject = JsonParser.parseString(errorJsonString).getAsJsonObject();
                        final ErrorModel errorModel = gson.fromJson(errorJsonObject, ErrorModel.class);
                        apiResponseModel.setErrorModel(errorModel);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }

                if (HttpURLConnection.HTTP_OK == responseCode) {
                    activity.runOnUiThread(() -> {
                        stateMediator.set(apiResponseModel, UiState.SUCCESS, "Got Data!", AppConstants.KEY_GET_RESTAURANT_LIST_API_SUCCESS_STATE);
                        mutableLiveData.postValue(stateMediator);
                    });
                }

                if (HttpURLConnection.HTTP_BAD_REQUEST == responseCode) {
                    activity.runOnUiThread(() -> {
                        stateMediator.set(null, UiState.ERROR, apiResponseModel.getErrorModel().getMessage(), null);
                        mutableLiveData.postValue(stateMediator);
                    });
                }

                if (HttpURLConnection.HTTP_UNAUTHORIZED == responseCode) {
                    activity.runOnUiThread(() -> {
                        stateMediator.set(null, UiState.ERROR, apiResponseModel.getErrorModel().getMessage(), null);
                        mutableLiveData.postValue(stateMediator);
                    });
                }

                if (HttpURLConnection.HTTP_FORBIDDEN == responseCode) {
                    activity.runOnUiThread(() -> {
                        stateMediator.set(null, UiState.ERROR, apiResponseModel.getErrorModel().getMessage(), null);
                        mutableLiveData.postValue(stateMediator);
                    });
                }

                if (HttpURLConnection.HTTP_INTERNAL_ERROR == responseCode) {
                    activity.runOnUiThread(() -> {
                        stateMediator.set(null, UiState.ERROR, apiResponseModel.getErrorModel().getMessage(), null);
                        mutableLiveData.postValue(stateMediator);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Nullable
    private String inputStreamToString(
            @NonNull final HttpURLConnection connection,
            @NonNull final InputStream inputStream) {
        String line = "";
        final StringBuilder stringBuilder = new StringBuilder();
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                if (connection != null) connection.disconnect();
            } catch (Exception ignored) {
            }
        }
        return stringBuilder.toString();
    }
}
