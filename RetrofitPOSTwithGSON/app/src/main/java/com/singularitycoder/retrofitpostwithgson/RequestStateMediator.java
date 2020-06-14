package com.singularitycoder.retrofitpostwithgson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RequestStateMediator<T, E, V, K> {

    @Nullable
    private T dataObject;

    @NonNull
    private E status;

    @Nullable
    private V message;

    @Nullable
    private K key;

    public void set(@Nullable T dataObject, @NonNull E status, @Nullable V message, @Nullable K key) {
        this.dataObject = dataObject;
        this.status = status;
        this.message = message;
        this.key = key;
    }

    public T getData() {
        return dataObject;
    }

    public E getStatus() {
        return status;
    }

    public V getMessage() {
        return message;
    }

    public K getKey() {
        return key;
    }
}