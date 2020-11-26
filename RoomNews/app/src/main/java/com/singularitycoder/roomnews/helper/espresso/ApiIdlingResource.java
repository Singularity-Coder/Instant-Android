package com.singularitycoder.roomnews.helper.espresso;

import androidx.annotation.Nullable;
import androidx.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

public class ApiIdlingResource implements IdlingResource {

    @Nullable
    private volatile ResourceCallback callback;

    // This boolean tells us the waiting or idle state
    private AtomicBoolean isIdle = new AtomicBoolean(true);

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdle.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }

    public void setIdleState(boolean isIdle) {
        this.isIdle.set(isIdle);
        if (isIdle && null != callback) callback.onTransitionToIdle();
    }
}