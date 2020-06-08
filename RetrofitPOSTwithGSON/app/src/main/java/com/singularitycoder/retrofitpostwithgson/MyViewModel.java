package com.singularitycoder.retrofitpostwithgson;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;

public class MyViewModel extends ViewModel {

    private static final String TAG = "MyViewModel";

    private MutableLiveData<RequestStateMediator> mutableLiveData;
    private MyRepository myRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LiveData<RequestStateMediator> createAccountFromRepository(String encodedImage, String name, String email, String phone, String password) throws IllegalArgumentException {
        myRepository = myRepository.getInstance();
        mutableLiveData = myRepository.createAccountWithApi(encodedImage, name, email, phone, password);
        return mutableLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
