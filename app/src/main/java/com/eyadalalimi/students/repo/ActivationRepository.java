package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.data.SessionManager;
import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.request.sub.ActivateCodeRequest;
import com.eyadalalimi.students.response.ApiResponse;
import com.eyadalalimi.students.response.SubscriptionResponse;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivationRepository {
    private final ApiService api;
    private final SessionManager session;
    private final Gson gson = new Gson();

    public ActivationRepository(Context ctx) {
        this.api = ApiClient.get(ctx);
        this.session = new SessionManager(ctx);
    }

    private String parseError(ResponseBody err) {
        try {
            ApiResponse<?> r = gson.fromJson(err.string(), ApiResponse.class);
            if (r != null && r.error != null && r.error.message != null) return r.error.message;
        } catch (Exception ignored) {}
        return "فشل التفعيل";
    }

    public void activate(String code, ApiCallback<SubscriptionResponse> cb) {
        api.activateCode(new ActivateCodeRequest(code)).enqueue(new Callback<ApiResponse<SubscriptionResponse>>() {
            @Override public void onResponse(Call<ApiResponse<SubscriptionResponse>> call, Response<ApiResponse<SubscriptionResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    session.markActivated();
                    cb.onSuccess(response.body().data);
                } else {
                    cb.onError(response.errorBody() != null ? parseError(response.errorBody()) : "فشل الاتصال");
                }
            }
            @Override public void onFailure(Call<ApiResponse<SubscriptionResponse>> call, Throwable t) {
                cb.onError(t.getMessage() != null ? t.getMessage() : "تعذر الاتصال");
            }
        });
    }
}
