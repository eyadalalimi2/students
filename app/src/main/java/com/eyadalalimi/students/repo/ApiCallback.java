package com.eyadalalimi.students.repo;

public interface ApiCallback<T> {
    void onSuccess(T data);
    void onError(String message);
}
