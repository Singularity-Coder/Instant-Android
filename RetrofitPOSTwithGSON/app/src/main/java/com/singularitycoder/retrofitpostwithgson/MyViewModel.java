package com.singularitycoder.retrofitpostwithgson;

import android.util.Log;

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

    public LiveData<RequestStateMediator> createAccountFromRepository(String encodedImage, String name, String email, String phone, String password) throws IllegalArgumentException {
        myRepository = myRepository.getInstance();
        mutableLiveData = myRepository.createAccountWithApi(encodedImage, name, email, phone, password);
        return mutableLiveData;
    }

    public LiveData<RequestStateMediator> createAccountFromRepository2(String encodedImage, String name, String email, String phone, String password) throws IllegalArgumentException {
        myRepository = myRepository.getInstance();

        requestStateMediator.set(null, Status.LOADING, "Loading...", null);
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
                                    requestStateMediator.set(o, Status.SUCCESS, "Got Data!", "CREATE ACCOUNT");
                                    mutableLiveData.postValue(requestStateMediator);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                requestStateMediator.set(null, Status.ERROR, e.getMessage(), null);
                                mutableLiveData.postValue(requestStateMediator);
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
