package com.eyadalalimi.students.repo;

import android.content.Context;
import android.content.SharedPreferences;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.User;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    /**
     * Alias داخلي للتوافق مع الشيفرات التي تستخدم AuthRepository.ApiCallback
     * يمتد من الواجهة العامة في نفس الحزمة com.eyadalalimi.students.repo.ApiCallback
     */
    public interface ApiCallback<T> extends com.eyadalalimi.students.repo.ApiCallback<T> {}

    private final Context appCtx;
    private final ApiService api;
    private final Gson gson = new Gson();

    public AuthRepository(Context ctx) {
        this.appCtx = ctx.getApplicationContext();
        this.api = ApiClient.get(ctx); // ApiClient.get(...) يعيد ApiService مباشرة
    }

    // ====== Auth ======

    // الاستدعاء القياسي: تمرير RegisterRequest مباشرة
    public void register(
            RegisterRequest body,
            com.eyadalalimi.students.repo.ApiCallback<TokenResponse> cb
    ) {
        api.register(body).enqueue(new Callback<ApiResponse<TokenResponse>>() {
            @Override public void onResponse(Call<ApiResponse<TokenResponse>> call, Response<ApiResponse<TokenResponse>> resp) {
                if (resp.isSuccessful() && resp.body()!=null && resp.body().data!=null) {
                    TokenResponse d = resp.body().data;
                    if (d.token != null) persistToken(d.token);
                    if (body != null && body.email != null) persistEmail(body.email);
                    cb.onSuccess(d);
                } else {
                    cb.onError(resp.errorBody()!=null? parseError(resp.errorBody()) : "فشل الاتصال");
                }
            }
            @Override public void onFailure(Call<ApiResponse<TokenResponse>> call, Throwable t) {
                cb.onError(t.getMessage()!=null? t.getMessage() : "فشل الشبكة");
            }
        });
    }

    // Overload لمطابقة استدعاء RegisterActivity الحالي
    // name, email, password, password_confirmation, loginDevice, country_id, university_id, college_id, major_id, level, gender
    public void register(
            String name,
            String email,
            String password,
            String password_confirmation,
            String loginDevice,
            Integer country_id,
            Integer university_id,
            Integer college_id,
            Integer major_id,
            Integer level,
            String gender,
            com.eyadalalimi.students.repo.ApiCallback<TokenResponse> cb
    ) {
        RegisterRequest req = new RegisterRequest(
                name,
                email,
                password,
                password_confirmation,
                loginDevice,
                (country_id != null ? country_id : 0), // تأكد من إرساله، الخادم يعتبره مطلوبًا
                university_id,
                college_id,
                major_id,
                level,
                gender
        );
        register(req, cb);
    }

    public void login(
            String email,
            String password,
            String loginDevice,
            com.eyadalalimi.students.repo.ApiCallback<TokenResponse> cb
    ) {
        LoginRequest body = new LoginRequest(email, password, loginDevice);
        api.login(body).enqueue(new Callback<ApiResponse<TokenResponse>>() {
            @Override public void onResponse(Call<ApiResponse<TokenResponse>> call, Response<ApiResponse<TokenResponse>> resp) {
                if (resp.isSuccessful() && resp.body()!=null && resp.body().data!=null) {
                    TokenResponse d = resp.body().data;
                    if (d.token != null) persistToken(d.token);
                    if (email != null) persistEmail(email);
                    cb.onSuccess(d);
                } else {
                    cb.onError(resp.errorBody()!=null? parseError(resp.errorBody()) : "فشل الاتصال");
                }
            }
            @Override public void onFailure(Call<ApiResponse<TokenResponse>> call, Throwable t) {
                cb.onError(t.getMessage()!=null? t.getMessage() : "فشل الشبكة");
            }
        });
    }

    public void logout(com.eyadalalimi.students.repo.ApiCallback<MessageResponse> cb) {
        api.logout().enqueue(new Callback<ApiResponse<MessageResponse>>() {
            @Override public void onResponse(Call<ApiResponse<MessageResponse>> call, Response<ApiResponse<MessageResponse>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) {
                    clearToken();
                    cb.onSuccess(resp.body().data);
                } else {
                    cb.onError(resp.errorBody()!=null? parseError(resp.errorBody()) : "فشل الاتصال");
                }
            }
            @Override public void onFailure(Call<ApiResponse<MessageResponse>> call, Throwable t) {
                cb.onError(t.getMessage()!=null? t.getMessage() : "فشل الشبكة");
            }
        });
    }

    public void resendEmail(String email, com.eyadalalimi.students.repo.ApiCallback<MessageResponse> cb) {
        api.resendVerify(new ResendEmailRequest(email))
                .enqueue(new Callback<ApiResponse<MessageResponse>>() {
                    @Override public void onResponse(Call<ApiResponse<MessageResponse>> call, Response<ApiResponse<MessageResponse>> resp) {
                        if (resp.isSuccessful() && resp.body()!=null) {
                            cb.onSuccess(resp.body().data);
                        } else {
                            cb.onError(resp.errorBody()!=null? parseError(resp.errorBody()) : "فشل الاتصال");
                        }
                    }
                    @Override public void onFailure(Call<ApiResponse<MessageResponse>> call, Throwable t) {
                        cb.onError(t.getMessage()!=null? t.getMessage() : "فشل الشبكة");
                    }
                });
    }

    // ====== Me / Profile ======

    public void me(com.eyadalalimi.students.repo.ApiCallback<User> cb) {
        api.me().enqueue(new Callback<ApiResponse<User>>() {
            @Override public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) {
                    cb.onSuccess(resp.body().data);
                } else {
                    cb.onError(resp.errorBody()!=null? parseError(resp.errorBody()) : "فشل الاتصال");
                }
            }
            @Override public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                cb.onError(t.getMessage()!=null? t.getMessage() : "فشل الشبكة");
            }
        });
    }

    // ====== Activation ======

    public void activateCode(String code, com.eyadalalimi.students.repo.ApiCallback<SubscriptionResponse> cb) {
        api.activateCode(new ActivateCodeRequest(code))
                .enqueue(new Callback<ApiResponse<SubscriptionResponse>>() {
                    @Override public void onResponse(Call<ApiResponse<SubscriptionResponse>> call, Response<ApiResponse<SubscriptionResponse>> resp) {
                        if (resp.isSuccessful() && resp.body()!=null) {
                            cb.onSuccess(resp.body().data);
                        } else {
                            cb.onError(resp.errorBody()!=null? parseError(resp.errorBody()) : "فشل الاتصال");
                        }
                    }
                    @Override public void onFailure(Call<ApiResponse<SubscriptionResponse>> call, Throwable t) {
                        cb.onError(t.getMessage()!=null? t.getMessage() : "فشل الشبكة");
                    }
                });
    }

    // ====== Password ======

    public void forgotPassword(String email, com.eyadalalimi.students.repo.ApiCallback<MessageResponse> cb) {
        api.forgot(new ForgotPasswordRequest(email))
                .enqueue(new Callback<ApiResponse<MessageResponse>>() {
                    @Override public void onResponse(Call<ApiResponse<MessageResponse>> call, Response<ApiResponse<MessageResponse>> resp) {
                        if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body().data);
                        else cb.onError(resp.errorBody()!=null? parseError(resp.errorBody()):"فشل الاتصال");
                    }
                    @Override public void onFailure(Call<ApiResponse<MessageResponse>> call, Throwable t) {
                        cb.onError(t.getMessage()!=null? t.getMessage():"فشل الشبكة");
                    }
                });
    }

    // التوقيع الذي يطلبه ResetPasswordActivity حالياً (email, token, pass, confirm)
    public void resetPassword(
            String email,
            String token,
            String newPass,
            String confirmPass,
            com.eyadalalimi.students.repo.ApiCallback<MessageResponse> cb
    ) {
        api.reset(new ResetPasswordRequest(email, token, newPass, confirmPass))
                .enqueue(new Callback<ApiResponse<MessageResponse>>() {
                    @Override public void onResponse(Call<ApiResponse<MessageResponse>> call, Response<ApiResponse<MessageResponse>> resp) {
                        if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body().data);
                        else cb.onError(resp.errorBody()!=null? parseError(resp.errorBody()):"فشل الاتصال");
                    }
                    @Override public void onFailure(Call<ApiResponse<MessageResponse>> call, Throwable t) {
                        cb.onError(t.getMessage()!=null? t.getMessage():"فشل الشبكة");
                    }
                });
    }

    // ====== Helpers ======

    private void persistToken(String token) {
        SharedPreferences sp = appCtx.getSharedPreferences("auth", Context.MODE_PRIVATE);
        sp.edit().putString("token", token).apply();
    }

    private void clearToken() {
        SharedPreferences sp = appCtx.getSharedPreferences("auth", Context.MODE_PRIVATE);
        sp.edit().remove("token").apply();
    }

    private void persistEmail(String email) {
        SharedPreferences sp = appCtx.getSharedPreferences("auth", Context.MODE_PRIVATE);
        sp.edit().putString("last_email", email).apply();
    }

    /** يُستخدم في HomeActivity */
    public String getLastLoginEmail() {
        SharedPreferences sp = appCtx.getSharedPreferences("auth", Context.MODE_PRIVATE);
        return sp.getString("last_email", null);
    }

    /** مفيد عند الحاجة لإرسال التوكن خارج الـ Interceptor */
    public String getToken() {
        SharedPreferences sp = appCtx.getSharedPreferences("auth", Context.MODE_PRIVATE);
        return sp.getString("token", null);
    }

    private String parseError(ResponseBody err) {
        try {
            String body = err.string();
            JsonObject o = JsonParser.parseString(body).getAsJsonObject();
            if (o.has("message") && !o.get("message").isJsonNull())
                return o.get("message").getAsString();
            if (o.has("error") && o.get("error").isJsonObject()) {
                JsonObject e = o.getAsJsonObject("error");
                if (e.has("message") && !e.get("message").isJsonNull())
                    return e.get("message").getAsString();
            }
            return "خطأ غير معروف";
        } catch (Exception e) {
            return "تعذر قراءة الخطأ";
        }
    }
}
