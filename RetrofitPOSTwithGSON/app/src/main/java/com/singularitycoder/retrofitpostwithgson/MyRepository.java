package com.singularitycoder.retrofitpostwithgson;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.String.valueOf;

public class MyRepository {

    private static final String TAG = "MyRepository";

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

        Call<String> call = apiService.setUserDataWithMultiPart(
                "YOUR_OPTIONAL_AUTH_KEY",
                partImage,
                partName,
                partEmail,
                partPhone,
                partPassword
        );

//        Call<JSONObject> call = apiService.setUserDataWithTypeThree("YOUR_OPTIONAL_AUTH_KEY", sendParametersTypeThree());
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: resp: " + response.body());
                    if (null != response.body()) {
                        requestStateMediator.set(response.body(), Status.SUCCESS, "Got Data!", "CREATE ACCOUNT");
                        createAccountLiveData.postValue(requestStateMediator);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                requestStateMediator.set(null, Status.ERROR, t.getMessage(), null);
                createAccountLiveData.postValue(requestStateMediator);
            }
        });
        return createAccountLiveData;
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
