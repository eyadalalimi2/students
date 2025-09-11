package com.eyadalalimi.students.core.network;

import com.eyadalalimi.students.model.Asset;
import com.eyadalalimi.students.model.College;
import com.eyadalalimi.students.model.Country;
import com.eyadalalimi.students.model.Major;
import com.eyadalalimi.students.model.PagedResponse;
import com.eyadalalimi.students.model.University;
import com.eyadalalimi.students.model.User;
import com.eyadalalimi.students.model.VisibilityInfo;
import com.eyadalalimi.students.request.auth.ForgotPasswordRequest;
import com.eyadalalimi.students.request.auth.LoginRequest;
import com.eyadalalimi.students.request.auth.RegisterRequest;
import com.eyadalalimi.students.request.auth.ResendEmailRequest;
import com.eyadalalimi.students.request.auth.ResetPasswordRequest;
import com.eyadalalimi.students.request.sub.ActivateCodeRequest;
import com.eyadalalimi.students.response.ApiResponse;
import com.eyadalalimi.students.response.MessageResponse;
import com.eyadalalimi.students.response.SubscriptionResponse;
import com.eyadalalimi.students.response.TokenResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ===== Auth =====
    @POST("auth/register")
    Call<ApiResponse<TokenResponse>> register(@Body RegisterRequest body);
    @POST("auth/login")
    Call<ApiResponse<TokenResponse>> login(@Body LoginRequest body);

    @POST("auth/logout")
    Call<ApiResponse<MessageResponse>> logout();
    @POST("auth/email/resend")
    Call<ApiResponse<MessageResponse>> resendVerify(@Body ResendEmailRequest body);
    @POST("auth/password/forgot")
    Call<ApiResponse<MessageResponse>> forgot(@Body ForgotPasswordRequest body);

    @POST("auth/password/reset")
    Call<ApiResponse<MessageResponse>> reset(@Body ResetPasswordRequest body);


    // Me
    @GET("auth/me")
    Call<ApiResponse<User>> me();

    @GET("me/visibility")
    Call<ApiResponse<VisibilityInfo>> meVisibility();


    // ===== Structure =====
    @GET("countries")                         Call<ApiResponse<List<Country>>> countries();
    @GET("universities")                      Call<ApiResponse<List<University>>> universities();
    @GET("universities/{id}/colleges")        Call<ApiResponse<List<College>>> colleges(@Path("id") long universityId);
    @GET("colleges/{id}/majors")              Call<ApiResponse<List<Major>>> majors(@Path("id") long collegeId);

    // ===== Activation =====
    @POST("me/activate-code")
    Call<ApiResponse<SubscriptionResponse>> activateCode(@Body ActivateCodeRequest body);
    // Assets (عام)
    @GET("assets")
    Call<PagedResponse<Asset>> assets(
            @Query("limit") Integer limit,
            @Query("material_id") Long materialId,
            @Query("category") String category,
            @Query("cursor") String cursor
    );

    @GET("assets/{id}")
    Call<ApiResponse<Asset>> assetDetails(@Path("id") long id);
}
