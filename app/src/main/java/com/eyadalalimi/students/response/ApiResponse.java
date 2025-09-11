package com.eyadalalimi.students.response;

import androidx.annotation.Nullable;

public class ApiResponse<T> {
    @Nullable public T data;
    @Nullable public ApiError error;
}
