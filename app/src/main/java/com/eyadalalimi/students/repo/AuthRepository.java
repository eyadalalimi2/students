package com.eyadalalimi.students.repo;

import android.content.Context;

import androidx.annotation.Nullable;

import com.eyadalalimi.students.core.data.PreferencesStore;
import com.eyadalalimi.students.core.data.SessionManager;
import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.request.auth.ForgotPasswordRequest;
import com.eyadalalimi.students.request.auth.LoginRequest;
import com.eyadalalimi.students.request.auth.RegisterRequest;
import com.eyadalalimi.students.request.auth.ResendEmailRequest;
import com.eyadalalimi.students.request.auth.ResetPasswordRequest;
import com.eyadalalimi.students.request.auth.VerifyEmailRequest;
import com.eyadalalimi.students.response.ApiResponse;
import com.eyadalalimi.students.response.MessageResponse;
import com.eyadalalimi.students.response.TokenResponse;
import com.eyadalalimi.students.response.VerifyEmailResponse;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService api;
    private final SessionManager session;
    private final PreferencesStore store;
    private final Gson gson = new Gson();

    public AuthRepository(Context ctx) {
        this.api = ApiClient.get(ctx);
        this.session = new SessionManager(ctx);
        this.store = new PreferencesStore(ctx);
    }

    private String parseError(ResponseBody err) {
        try {
            String body = err.string();
            ApiResponse<?> r = gson.fromJson(body, ApiResponse.class);
            if (r != null && r.error != null && r.error.message != null) {
                return r.error.message;
            }
            return "حدث خطأ غير متوقع";
        } catch (Exception e) {
            return "تعذّر قراءة الخطأ";
        }
    }

    private <T> Callback<ApiResponse<T>> wrap(ApiCallback<T> cb, @Nullable OnSuccess<T> ok) {
        return new Callback<ApiResponse<T>>() {
            @Override
            public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<T> r = response.body();
                    if (r.data != null) {
                        if (ok != null) ok.run(r.data);
                        cb.onSuccess(r.data);
                    } else if (r.error != null) {
                        cb.onError(r.error.message != null ? r.error.message : "خطأ غير معروف");
                    } else {
                        cb.onError("استجابة غير متوقعة");
                    }
                } else {
                    cb.onError(response.errorBody() != null ? parseError(response.errorBody()) : "فشل الاتصال");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<T>> call, Throwable t) {
                cb.onError(t.getMessage() != null ? t.getMessage() : "فشل الشبكة");
            }
        };
    }

    private interface OnSuccess<T> { void run(T v); }

    public void login(String email, String password, String loginDevice, ApiCallback<TokenResponse> cb) {
        api.login(new LoginRequest(email, password, loginDevice))
                .enqueue(wrap(cb, data -> {
                    if (data.token != null) {
                        session.loginWithToken(data.token);
                        store.setEmail(email);
                    }
                }));
    }

    public void register(String name, String email, String password, String passwordConfirmation, String loginDevice,
                         ApiCallback<TokenResponse> cb) {
        RegisterRequest req = new RegisterRequest(name, email, password, passwordConfirmation, loginDevice);
        api.register(req).enqueue(wrap(cb, data -> {
            if (data.token != null) {
                session.loginWithToken(data.token);
                store.setEmail(email);
            }
        }));
    }

    public void resendEmail(String email, ApiCallback<MessageResponse> cb) {
        api.resendVerify(new ResendEmailRequest(email)).enqueue(wrap(cb, null));
    }

    public void verifyEmail(String email, String code, ApiCallback<VerifyEmailResponse> cb) {
        api.verifyEmail(new VerifyEmailRequest(email, code)).enqueue(wrap(cb, data -> {
            if (data.verified) session.markEmailVerified();
        }));
    }

    public void forgotPassword(String email, ApiCallback<MessageResponse> cb) {
        api.forgot(new ForgotPasswordRequest(email)).enqueue(wrap(cb, null));
    }

    public void resetPassword(String email, String token, String newPassword, String confirmPassword,
                              ApiCallback<MessageResponse> cb) {
        api.reset(new ResetPasswordRequest(email, token, newPassword, confirmPassword))
                .enqueue(wrap(cb, null));
    }

    public void logout(ApiCallback<MessageResponse> cb) {
        api.logout().enqueue(wrap(cb, data -> session.logout()));
    }

    public String getStoredEmail() { return store.getEmail(); }
}
