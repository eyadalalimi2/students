package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.request.auth.ForgotPasswordRequest;
import com.eyadalalimi.students.request.auth.LoginRequest;
import com.eyadalalimi.students.request.auth.RegisterRequest;
import com.eyadalalimi.students.request.auth.ResetPasswordRequest;
import com.eyadalalimi.students.request.auth.VerifyEmailRequest;
import com.eyadalalimi.students.response.BaseResponse;
import com.eyadalalimi.students.response.TokenResponse;
import com.eyadalalimi.students.response.UserResponse;

import retrofit2.Call;

public class AuthRepository {
    private final ApiService api;
    public AuthRepository(Context ctx) { this.api = new ApiClient(ctx).create(ApiService.class); }

    public Call<TokenResponse> login(LoginRequest req){ return api.login(req); }
    public Call<TokenResponse> register(RegisterRequest req){ return api.register(req); }
    public Call<BaseResponse> verify(VerifyEmailRequest req){ return api.emailVerify(req); }
    public Call<BaseResponse> resend(VerifyEmailRequest req){ return api.emailResend(req); }
    public Call<BaseResponse> forgot(ForgotPasswordRequest req){ return api.passwordForgot(req); }
    public Call<BaseResponse> reset(ResetPasswordRequest req){ return api.passwordReset(req); }
    public Call<UserResponse> me(){ return api.me(); }
}
