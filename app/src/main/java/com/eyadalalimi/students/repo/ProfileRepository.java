package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.User;
import com.eyadalalimi.students.response.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {
    private final ApiService api;
    private final Gson gson = new Gson();

    public ProfileRepository(Context ctx) {
        this.api = ApiClient.get(ctx);
    }

    private String parseError(ResponseBody err) {
        try {
            String body = err.string();
            JsonObject o = JsonParser.parseString(body).getAsJsonObject();
            if (o.has("message")) return o.get("message").getAsString();
            if (o.has("error") && o.getAsJsonObject("error").has("message"))
                return o.getAsJsonObject("error").get("message").getAsString();
            return "تعذر جلب البيانات";
        } catch (Exception e) { return "تعذر قراءة الخطأ"; }
    }

    // جلب بيانات المستخدم الحالية
    public void me(ApiCallback<User> cb) {
        api.me().enqueue(new Callback<ApiResponse<User>>() {
            @Override public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
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

    // يمكنك إضافة تحديث الملف الشخصي هنا لاحقًا (PUT /me/profile) إن لزم
}
