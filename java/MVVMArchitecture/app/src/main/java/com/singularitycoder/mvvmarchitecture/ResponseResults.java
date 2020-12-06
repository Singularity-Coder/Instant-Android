package com.singularitycoder.mvvmarchitecture;

public class ResponseResults<T, U, V> {

    T dataClass;

    ResponseResults(T dataClass) {
        this.dataClass = dataClass;
    }

    public T started() {
        return this.dataClass;
    }

    public T success() {
        return this.dataClass;
    }

    public T empty() {
        return this.dataClass;
    }

    public T failure() {
        return this.dataClass;
    }

    public T finished() {
        return this.dataClass;
    }
}
