package com.eyadalalimi.students.repo;

import android.content.Context;
import android.net.Uri;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.User;
import com.eyadalalimi.students.response.ApiResponse;
import com.eyadalalimi.students.response.MessageResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {

    private final Context appCtx;
    private final ApiService api;

    public ProfileRepository(Context ctx) {
        this.appCtx = ctx.getApplicationContext();
        this.api = ApiClient.get(appCtx);
    }

    public void getProfile(ApiCallback<User> cb) {
        api.getProfile().enqueue(new Callback<ApiResponse<User>>() {
            @Override public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body().data);
                else cb.onError(parseError(resp.errorBody()));
            }
            @Override public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                cb.onError(t.getMessage()!=null?t.getMessage():"فشل الشبكة");
            }
        });
    }

    public void updateProfile(Map<String, Object> body, ApiCallback<User> cb) {
        if (body == null) body = new HashMap<>();
        api.updateProfile(body).enqueue(new Callback<ApiResponse<User>>() {
            @Override public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body().data);
                else cb.onError(parseError(resp.errorBody()));
            }
            @Override public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                cb.onError(t.getMessage()!=null?t.getMessage():"فشل الشبكة");
            }
        });
    }

    public void changePassword(String current, String password, String passwordConfirmation,
                               ApiCallback<MessageResponse> cb) {
        Map<String, String> body = new HashMap<>();
        body.put("current_password", current);
        body.put("password", password);
        body.put("password_confirmation", passwordConfirmation);

        api.changePassword(body).enqueue(new Callback<ApiResponse<MessageResponse>>() {
            @Override public void onResponse(Call<ApiResponse<MessageResponse>> call, Response<ApiResponse<MessageResponse>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body().data);
                else cb.onError(parseError(resp.errorBody()));
            }
            @Override public void onFailure(Call<ApiResponse<MessageResponse>> call, Throwable t) {
                cb.onError(t.getMessage()!=null?t.getMessage():"فشل الشبكة");
            }
        });
    }

    public void uploadPhoto(Context ctx, Uri uri, ApiCallback<User> cb) {
        try {
            String mime = ctx.getContentResolver().getType(uri);
            if (mime == null) mime = "image/*";

            InputStream is = ctx.getContentResolver().openInputStream(uri);
            if (is == null) { cb.onError("تعذّر قراءة الملف"); return; }

            byte[] bytes;
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[8192];
                int r;
                while ((r = is.read(buf)) != -1) bos.write(buf, 0, r);
                bytes = bos.toByteArray();
            }

            RequestBody rb = RequestBody.create(MediaType.parse(mime), bytes);
            MultipartBody.Part part = MultipartBody.Part.createFormData(
                    "photo", "profile.jpg", rb // غيّر الاسم لو السيرفر يتوقع حقلاً مختلفاً
            );

            api.uploadProfilePhoto(part).enqueue(new Callback<ApiResponse<User>>() {
                @Override public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> resp) {
                    if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body().data);
                    else cb.onError(parseError(resp.errorBody()));
                }
                @Override public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                    cb.onError(t.getMessage()!=null?t.getMessage():"فشل الشبكة");
                }
            });
        } catch (Exception e) {
            cb.onError("تعذّر تجهيز الملف");
        }
    }

    private String parseError(ResponseBody err) {
        try {
            String body = err != null ? err.string() : null;
            if (body == null || body.trim().isEmpty()) return "خطأ غير معروف";

            JsonElement el = JsonParser.parseString(body);
            if (el.isJsonObject()) {
                JsonObject o = el.getAsJsonObject();
                if (o.has("message") && !o.get("message").isJsonNull())
                    return o.get("message").getAsString();
                if (o.has("status") && !o.get("status").isJsonNull()) {
                    String s = o.get("status").getAsString();
                    if (!s.isEmpty()) return s;
                }
                if (o.has("errors") && o.get("errors").isJsonObject()) {
                    JsonObject errs = o.getAsJsonObject("errors");
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, JsonElement> e : errs.entrySet()) {
                        JsonElement v = e.getValue();
                        if (v.isJsonArray()) {
                            for (JsonElement m : v.getAsJsonArray()) {
                                if (sb.length() > 0) sb.append('\n');
                                sb.append(m.getAsString());
                            }
                        } else if (v.isJsonPrimitive()) {
                            if (sb.length() > 0) sb.append('\n');
                            sb.append(v.getAsString());
                        }
                    }
                    if (sb.length() > 0) return sb.toString();
                }
                if (o.has("error") && o.get("error").isJsonObject()) {
                    JsonObject e = o.getAsJsonObject("error");
                    if (e.has("message") && !e.get("message").isJsonNull())
                        return e.get("message").getAsString();
                }
            }
            String plain = body.replaceAll("<[^>]*>", "").trim();
            if (!plain.isEmpty()) return plain;
            return "خطأ غير معروف";
        } catch (Exception e) {
            return "تعذّر قراءة الخطأ";
        }
    }

    public interface ApiCallback<T> {
        void onSuccess(T data);
        void onError(String msg);
    }
}
