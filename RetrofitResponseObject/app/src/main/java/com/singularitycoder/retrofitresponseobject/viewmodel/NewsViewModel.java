package com.singularitycoder.retrofitresponseobject.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.singularitycoder.retrofitresponseobject.helper.StateMediator;
import com.singularitycoder.retrofitresponseobject.helper.UiState;
import com.singularitycoder.retrofitresponseobject.repository.NewsRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public final class NewsViewModel extends ViewModel {

    @NonNull
    private final String TAG = "NewsViewModel";

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @NonNull
    private final NewsRepository newsRepository = NewsRepository.getInstance();

    @NonNull
    public final LiveData<StateMediator<Object, UiState, String, String>> getNewsFromRepository(
            @Nullable final String country,
            @NonNull final String category) throws IllegalArgumentException {

        final StateMediator<Object, UiState, String, String> stateMediator = new StateMediator<>();
        final MutableLiveData<StateMediator<Object, UiState, String, String>> mutableLiveData = new MutableLiveData<>();

        stateMediator.set(null, UiState.LOADING, "Loading...", null);
        mutableLiveData.postValue(stateMediator);

        final DisposableSingleObserver disposableSingleObserver =
                newsRepository.getNewsWithRetrofit(country, category)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d(TAG, "onResponse: resp: " + o);
                                if (null != o) {
                                    stateMediator.set(o, UiState.SUCCESS, "Got Data!", "NEWS");
                                    mutableLiveData.postValue(stateMediator);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                stateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                                mutableLiveData.postValue(stateMediator);
                            }
                        });
        compositeDisposable.add(disposableSingleObserver);
        return mutableLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
