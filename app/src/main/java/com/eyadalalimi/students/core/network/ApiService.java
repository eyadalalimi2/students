package com.eyadalalimi.students.core.network;

import com.eyadalalimi.students.model.Asset;
import com.eyadalalimi.students.model.College;
import com.eyadalalimi.students.model.Content;
import com.eyadalalimi.students.model.Country;
import com.eyadalalimi.students.model.FeedItem;
import com.eyadalalimi.students.model.Major;
import com.eyadalalimi.students.model.Material;
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
import com.eyadalalimi.students.response.ListResponse;
import com.eyadalalimi.students.response.MessageResponse;
import com.eyadalalimi.students.response.SubscriptionResponse;
import com.eyadalalimi.students.response.TokenResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    // التحقق بالبريد عبر "رابط" فقط (لا يوجد إدخال كود في العميل)
    @POST("auth/email/resend")
    Call<ApiResponse<MessageResponse>> resendVerify(@Body ResendEmailRequest body);

    @POST("auth/password/forgot")
    Call<ApiResponse<MessageResponse>> forgot(@Body ForgotPasswordRequest body);

    @POST("auth/password/reset")
    Call<ApiResponse<MessageResponse>> reset(@Body ResetPasswordRequest body);

    // المستخدم الحالي
    @GET("auth/me")
    Call<ApiResponse<User>> me();


    // ===== Profile & Security =====
    @GET("me/profile")
    Call<ApiResponse<User>> getProfile();

    // body ديناميكي: name, phone, university_id, college_id, major_id, level, gender, ...
    @PUT("me/profile")
    Call<ApiResponse<User>> updateProfile(@Body Map<String, Object> body);

    // body: { "current_password":"...", "new_password":"...", "new_password_confirmation":"..." }
    @PUT("me/security/change-password")
    Call<ApiResponse<MessageResponse>> changePassword(@Body Map<String, String> body);


    // ===== Visibility =====
    @GET("me/visibility")
    Call<ApiResponse<VisibilityInfo>> meVisibility();


    // ===== Activation =====
    @POST("me/activate-code")
    Call<ApiResponse<SubscriptionResponse>> activateCode(@Body ActivateCodeRequest body);


    // ===== Structure =====
    @GET("countries")
    Call<ApiResponse<List<Country>>> countries();

    @GET("universities")
    Call<ApiResponse<List<University>>> universities();

    @GET("universities/{id}/colleges")
    Call<ApiResponse<List<College>>> colleges(@Path("id") long universityId);

    @GET("colleges/{id}/majors")
    Call<ApiResponse<List<Major>>> majors(@Path("id") long collegeId);


    // ===== Feed =====
    @GET("me/feed")
    Call<PagedResponse<FeedItem>> meFeed(
            @Query("limit") Integer limit,
            @Query("cursor") String cursor
    );


    // ===== Assets (عام) =====
    @GET("assets")
    Call<ListResponse<Asset>> assets(
            @Query("limit") Integer limit,
            @Query("cursor") String cursor,
            @Query("material_id") Long materialId,
            @Query("category") String category
    );

    @GET("assets/{id}")
    Call<ApiResponse<Asset>> asset(@Path("id") long id);


    // ===== Contents (خاص بالمؤسسة) =====
    @GET("contents")
    Call<ListResponse<Content>> contents(
            @Query("limit") Integer limit,
            @Query("cursor") String cursor,
            @Query("material_id") Long materialId,   // اختياري
            @Query("type") String type               // اختياري: file|video|link
    );

    @GET("contents/{id}")
    Call<ApiResponse<Content>> content(@Path("id") long id);


    // ===== Materials =====
    @GET("materials")
    Call<ListResponse<Material>> materials(
            @Query("limit") Integer limit,
            @Query("cursor") String cursor,
            @Query("scope") String scope,            // اختياري: global|university
            @Query("university_id") Long universityId,
            @Query("college_id") Long collegeId,
            @Query("major_id") Long majorId
    );

    @GET("materials/{id}")
    Call<ApiResponse<Material>> material(@Path("id") long id);
}
