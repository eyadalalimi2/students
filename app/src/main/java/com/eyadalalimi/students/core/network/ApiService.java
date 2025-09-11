package com.eyadalalimi.students.core.network;

import com.eyadalalimi.students.model.User;
import com.eyadalalimi.students.request.auth.ForgotPasswordRequest;
import com.eyadalalimi.students.request.auth.LoginRequest;
import com.eyadalalimi.students.request.auth.RegisterRequest;
import com.eyadalalimi.students.request.auth.ResetPasswordRequest;
import com.eyadalalimi.students.request.auth.VerifyEmailRequest;
import com.eyadalalimi.students.request.sub.ActivateCodeRequest;
import com.eyadalalimi.students.response.AssetResponse;
import com.eyadalalimi.students.response.BaseResponse;
import com.eyadalalimi.students.response.ContentResponse;
import com.eyadalalimi.students.response.FeedResponse;
import com.eyadalalimi.students.response.MaterialResponse;
import com.eyadalalimi.students.response.SubscriptionResponse;
import com.eyadalalimi.students.response.TokenResponse;
import com.eyadalalimi.students.response.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Auth
    @POST("auth/register") Call<TokenResponse> register(@Body RegisterRequest body);
    @POST("auth/login")    Call<TokenResponse> login(@Body LoginRequest body);
    @POST("auth/email/resend") Call<BaseResponse> resend(@Body VerifyEmailRequest body);
    @POST("auth/email/verify") Call<BaseResponse> verify(@Body VerifyEmailRequest body);
    @POST("auth/password/forgot") Call<BaseResponse> forgot(@Body ForgotPasswordRequest body);
    @POST("auth/password/reset")  Call<BaseResponse> reset(@Body ResetPasswordRequest body);
    @GET("auth/me") Call<UserResponse> me();

    // Me/Profile
    @GET("me/profile") Call<UserResponse> profile();
    @PUT("me/profile") Call<UserResponse> updateProfile(@Body User body);
    @PUT("me/security/change-password") Call<BaseResponse> changePassword(@Body User body);

    @GET("me/visibility") Call<BaseResponse> visibility();
    @GET("me/devices") Call<BaseResponse> devices();
    @DELETE("me/devices/{id}") Call<BaseResponse> deleteDevice(@Path("id") long id);
    @GET("me/feed") Call<FeedResponse> feed(@Query("limit") Integer limit, @Query("cursor") String cursor);

    // Activation
    @POST("me/activate-code") Call<SubscriptionResponse> activate(@Body ActivateCodeRequest body);
    @GET("me/subscription") Call<SubscriptionResponse> subscription();

    // Structure
    @GET("countries") Call<BaseResponse> countries();
    @GET("universities") Call<BaseResponse> universities();
    @GET("universities/{id}/colleges") Call<BaseResponse> colleges(@Path("id") long id);
    @GET("colleges/{id}/majors") Call<BaseResponse> majors(@Path("id") long id);

    // Catalog
    @GET("materials") Call<MaterialResponse> materials();
    @GET("materials/{id}") Call<MaterialResponse> material(@Path("id") long id);

    // Assets
    @GET("assets") Call<AssetResponse> assets();
    @GET("assets/{id}") Call<AssetResponse> asset(@Path("id") long id);

    // Contents
    @GET("contents") Call<ContentResponse> contents();
    @GET("contents/{id}") Call<ContentResponse> content(@Path("id") long id);
}
