package com.singularitycoder.retrofitpostwithgson;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MyRepository {

    private static final String TAG = "MyRepository";

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static MyRepository _instance;

    public MyRepository() {
        // Initialize Firebase if necessary
    }

    public static MyRepository getInstance() {
        if (_instance == null) {
            _instance = new MyRepository();
        }
        return _instance;
    }

    private RequestBody sendParametersTypeOne(String encodedImage, String name, String email, String phone, String password) {
        // U can use a Map instead of JSONObject as well. This is how you pass it to RequestBody - String.valueOf(new JSONObject(mapParams)))
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_profile_image", encodedImage);
            jsonObject.put("user_name", name);
            jsonObject.put("user_email", email);
            jsonObject.put("user_phone", phone);
            jsonObject.put("user_password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonObject));
        return body;
    }

    private CreateAccountRequest sendParametersTypeTwo(String encodedImage, String name, String email, String phone, String password) {
        CreateAccountRequest createAccountRequest = new CreateAccountRequest(
                encodedImage,
                name,
                email,
                phone,
                password
        );
        return createAccountRequest;
    }

    private HashMap<String, String> sendParametersTypeThree(String encodedImage, String name, String email, String phone, String password) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("user_profile_image", encodedImage);
        parameters.put("user_name", name);
        parameters.put("user_email", email);
        parameters.put("user_phone", password);
        parameters.put("user_password", password);
        return parameters;
    }

    public MutableLiveData<RequestStateMediator> createAccountWithApi(
            @Nullable final String encodedImage,
            @NonNull final String name,
            @NonNull final String email,
            @NonNull final String phone,
            @NonNull final String password,
            @Nullable final ApiIdlingResource idlingResource) {

        if (null != idlingResource) idlingResource.setIdleState(false);

        final MutableLiveData<RequestStateMediator> createAccountLiveData = new MutableLiveData<>();
        final RequestStateMediator requestStateMediator = new RequestStateMediator();

        requestStateMediator.set(null, UiState.LOADING, "Loading...", null);
        createAccountLiveData.postValue(requestStateMediator);

        ApiEndPoints apiService = RetrofitService.getRetrofitInstance().create(ApiEndPoints.class);

        RequestBody requestBodyImage = RequestBody.create(MediaType.parse("image/*"), encodedImage);
        MultipartBody.Part partImage = MultipartBody.Part.createFormData("user_profile_image", "file name", requestBodyImage);

        RequestBody requestBodyName = RequestBody.create(MediaType.parse("text/plain"), name);
        MultipartBody.Part partName = MultipartBody.Part.createFormData("user_name", "text name", requestBodyName);

        RequestBody requestBodyEmail = RequestBody.create(MediaType.parse("text/plain"), email);
        MultipartBody.Part partEmail = MultipartBody.Part.createFormData("user_email", "text name", requestBodyEmail);

        RequestBody requestBodyPhone = RequestBody.create(MediaType.parse("text/plain"), phone);
        MultipartBody.Part partPhone = MultipartBody.Part.createFormData("user_phone", "text name", requestBodyPhone);

        RequestBody requestBodyPassword = RequestBody.create(MediaType.parse("text/plain"), password);
        MultipartBody.Part partPassword = MultipartBody.Part.createFormData("user_password", "text name", requestBodyPassword);

        compositeDisposable.add(
                apiService
                        .setUserDataWithMultiPart("YOUR_OPTIONAL_AUTH_KEY", partImage, partName, partEmail, partPhone, partPassword)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d(TAG, "onResponse: resp: " + o);
                                if (null != o) {
                                    requestStateMediator.set(o, UiState.SUCCESS, "Got Data!", "CREATE ACCOUNT");
                                    createAccountLiveData.postValue(requestStateMediator);
                                    if (null != idlingResource) idlingResource.setIdleState(true);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                                createAccountLiveData.postValue(requestStateMediator);
                                if (null != idlingResource) idlingResource.setIdleState(true);
                            }
                        })
        );
        return createAccountLiveData;
    }

    public Single<String> createAccountWithApi2(
            @Nullable final String encodedImage,
            @NonNull final String name,
            @NonNull final String email,
            @NonNull final String phone,
            @NonNull final String password) {

        ApiEndPoints apiService = RetrofitService.getRetrofitInstance().create(ApiEndPoints.class);

        RequestBody requestBodyImage = RequestBody.create(MediaType.parse("image/*"), encodedImage);
        MultipartBody.Part partImage = MultipartBody.Part.createFormData("user_profile_image", "file name", requestBodyImage);

        RequestBody requestBodyName = RequestBody.create(MediaType.parse("text/plain"), name);
        MultipartBody.Part partName = MultipartBody.Part.createFormData("user_name", "text name", requestBodyName);

        RequestBody requestBodyEmail = RequestBody.create(MediaType.parse("text/plain"), email);
        MultipartBody.Part partEmail = MultipartBody.Part.createFormData("user_email", "text name", requestBodyEmail);

        RequestBody requestBodyPhone = RequestBody.create(MediaType.parse("text/plain"), phone);
        MultipartBody.Part partPhone = MultipartBody.Part.createFormData("user_phone", "text name", requestBodyPhone);

        RequestBody requestBodyPassword = RequestBody.create(MediaType.parse("text/plain"), password);
        MultipartBody.Part partPassword = MultipartBody.Part.createFormData("user_password", "text name", requestBodyPassword);

        Single<String> observer = apiService.setUserDataWithMultiPart("YOUR_OPTIONAL_AUTH_KEY", partImage, partName, partEmail, partPhone, partPassword);

        return observer;
    }
}
