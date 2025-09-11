package com.eyadalalimi.students.core.network;

import com.eyadalalimi.students.request.auth.ForgotPasswordRequest;
import com.eyadalalimi.students.request.auth.LoginRequest;
import com.eyadalalimi.students.request.auth.RegisterRequest;
import com.eyadalalimi.students.request.auth.ResendEmailRequest;
import com.eyadalalimi.students.request.auth.ResetPasswordRequest;
import com.eyadalalimi.students.request.auth.VerifyEmailRequest;
import com.eyadalalimi.students.request.sub.ActivateCodeRequest;
import com.eyadalalimi.students.response.ApiResponse;
import com.eyadalalimi.students.response.MessageResponse;
import com.eyadalalimi.students.response.SubscriptionResponse;
import com.eyadalalimi.students.response.TokenResponse;
import com.eyadalalimi.students.response.UserResponse;
import com.eyadalalimi.students.response.VerifyEmailResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    // Auth
    @POST("auth/register")
    Call<ApiResponse<TokenResponse>> register(@Body RegisterRequest body);

    @POST("auth/login")
    Call<ApiResponse<TokenResponse>> login(@Body LoginRequest body);

    @POST("auth/email/resend")
    Call<ApiResponse<MessageResponse>> resendVerify(@Body ResendEmailRequest body);

    @POST("auth/email/verify")
    Call<ApiResponse<VerifyEmailResponse>> verifyEmail(@Body VerifyEmailRequest body);

    @POST("auth/password/forgot")
    Call<ApiResponse<MessageResponse>> forgot(@Body ForgotPasswordRequest body);

    @POST("auth/password/reset")
    Call<ApiResponse<MessageResponse>> reset(@Body ResetPasswordRequest body);

    @POST("auth/logout")
    Call<ApiResponse<MessageResponse>> logout();

    // Me
    @GET("auth/me")
    Call<ApiResponse<UserResponse>> me();

    // Activation
    @POST("me/activate-code")
    Call<ApiResponse<SubscriptionResponse>> activateCode(@Body ActivateCodeRequest body);
}
