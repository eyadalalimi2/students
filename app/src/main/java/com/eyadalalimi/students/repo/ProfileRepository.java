package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.data.PreferencesStore;
import com.eyadalalimi.students.core.data.SessionManager;
import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.response.ApiResponse;
import com.eyadalalimi.students.response.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {
    private final ApiService api;
    private final SessionManager session;
    private final PreferencesStore store;

    public ProfileRepository(Context ctx) {
        this.api = ApiClient.get(ctx);
        this.session = new SessionManager(ctx);
        this.store = new PreferencesStore(ctx);
    }

    public void fetchMe(ApiCallback<UserResponse> cb) {
        api.me().enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> resp) {
                if (resp.isSuccessful() && resp.body() != null && resp.body().data != null) {
                    UserResponse u = resp.body().data;
                    if (u.email != null) store.setEmail(u.email);
                    if (u.has_active_subscription) session.markActivated(); // تفعيل فعلي من الخادم
                    cb.onSuccess(u);
                } else {
                    cb.onError("تعذر جلب بيانات المستخدم");
                }
            }
            @Override public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                cb.onError(t.getMessage() != null ? t.getMessage() : "فشل الشبكة");
            }
        });
    }
}
