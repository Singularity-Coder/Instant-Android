package com.singularitycoder.retrofitpostwithgson;

public class RequestStateMediator<T, E, V, K> {

    private T dataObject;
    private E status;
    private V message;
    private K key;

    public void set(T dataObject, E status, V message, K key) {
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
