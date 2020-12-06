package com.singularitycoder.retrofitpostwithgson;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MyViewModel extends ViewModel {

    private static final String TAG = "MyViewModel";

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final RequestStateMediator requestStateMediator = new RequestStateMediator();

    private MutableLiveData<RequestStateMediator> mutableLiveData = new MutableLiveData<>();
    private MyRepository myRepository;

    public LiveData<RequestStateMediator> createAccountFromRepository(
            @Nullable final String encodedImage,
            @NonNull final String name,
            @NonNull final String email,
            @NonNull final String phone,
            @NonNull final String password,
            @Nullable final ApiIdlingResource idlingResource) throws IllegalArgumentException {
        myRepository = myRepository.getInstance();
        mutableLiveData = myRepository.createAccountWithApi(encodedImage, name, email, phone, password, idlingResource);
        return mutableLiveData;
    }

    public LiveData<RequestStateMediator> createAccountFromRepository2(
            @Nullable final String encodedImage,
            @NonNull final String name,
            @NonNull final String email,
            @NonNull final String phone,
            @NonNull final String password,
            @Nullable final ApiIdlingResource idlingResource) throws IllegalArgumentException {

        if (null != idlingResource) idlingResource.setIdleState(false);

        myRepository = myRepository.getInstance();

        requestStateMediator.set(null, UiState.LOADING, "Loading...", null);
        mutableLiveData.postValue(requestStateMediator);

        compositeDisposable.add(
                myRepository.createAccountWithApi2(encodedImage, name, email, phone, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d(TAG, "onResponse: resp: " + o);
                                if (null != o) {
                                    requestStateMediator.set(o, UiState.SUCCESS, "Got Data!", "CREATE ACCOUNT");
                                    mutableLiveData.postValue(requestStateMediator);
                                    if (null != idlingResource) idlingResource.setIdleState(true);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                requestStateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                                mutableLiveData.postValue(requestStateMediator);
                                if (null != idlingResource) idlingResource.setIdleState(true);
                            }
                        })
        );
        return mutableLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
