package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.VisibilityInfo;
import com.eyadalalimi.students.response.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisibilityRepository {

    private final ApiService api;

    public VisibilityRepository(Context ctx) {
        this.api = ApiClient.get(ctx);
    }

    public void getVisibility(ApiCallback<VisibilityInfo> cb) {
        api.meVisibility().enqueue(new Callback<ApiResponse<VisibilityInfo>>() {
            @Override
            public void onResponse(Call<ApiResponse<VisibilityInfo>> call, Response<ApiResponse<VisibilityInfo>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    cb.onSuccess(resp.body().data);
                } else {
                    cb.onError("تعذّر جلب معلومات الرؤية");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<VisibilityInfo>> call, Throwable t) {
                cb.onError(t.getMessage() != null ? t.getMessage() : "فشل الشبكة");
            }
        });
    }
}
