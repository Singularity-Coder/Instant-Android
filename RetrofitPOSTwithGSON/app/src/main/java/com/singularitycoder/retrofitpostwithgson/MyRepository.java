package com.singularitycoder.retrofitpostwithgson;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

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

    public MutableLiveData<RequestStateMediator> createAccountWithApi(String encodedImage, String name, String email, String phone, String password) {
        final MutableLiveData<RequestStateMediator> createAccountLiveData = new MutableLiveData<>();
        final RequestStateMediator requestStateMediator = new RequestStateMediator();

        requestStateMediator.set(null, Status.LOADING, "Loading...", null);
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
                                    requestStateMediator.set(o, Status.SUCCESS, "Got Data!", "CREATE ACCOUNT");
                                    createAccountLiveData.postValue(requestStateMediator);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                requestStateMediator.set(null, Status.ERROR, e.getMessage(), null);
                                createAccountLiveData.postValue(requestStateMediator);
                            }
                        })
        );
        return createAccountLiveData;
    }

    public Single<String> createAccountWithApi2(String encodedImage, String name, String email, String phone, String password) {

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


    // Test Stuff
    public MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

    public String getData() {
        return "This is data";
    }

    public void getValues(String a, String b) {
        Log.d(TAG, "getValues: " + a + " " + b);
    }

    public MutableLiveData<String> getMutableLiveData(String name, String password) {
        mutableLiveData.setValue("name is " + name + " password is " + password);
        return mutableLiveData;
    }
}
