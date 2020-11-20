package com.singularitycoder.roomnews.helper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class StateMediator<T, E, V, K> {

    @Nullable
    private T dataObject;

    @Nullable
    private E status;

    @Nullable
    private V message;

    @Nullable
    private K key;

    public void set(@Nullable final T dataObject, @NonNull final E status,
                    @Nullable final V message, @Nullable final K key) {
        this.dataObject = dataObject;
        this.status = status;
        this.message = message;
        this.key = key;
    }

    @Nullable
    public final T getData() {
        return dataObject;
    }

    @Nullable
    public final E getStatus() {
        return status;
    }

    @Nullable
    public final V getMessage() {
        return message;
    }

    @Nullable
    public final K getKey() {
        return key;
    }
}