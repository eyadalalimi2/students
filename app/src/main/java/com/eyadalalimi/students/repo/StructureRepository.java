package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.College;
import com.eyadalalimi.students.model.Country;
import com.eyadalalimi.students.model.Major;
import com.eyadalalimi.students.model.University;
import com.eyadalalimi.students.response.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StructureRepository {
    private final ApiService api;
    private final Gson gson = new Gson();

    public StructureRepository(Context ctx) {
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

    public void getCountries(ApiCallback<List<Country>> cb) {
        api.countries().enqueue(new Callback<ApiResponse<List<Country>>>() {
            @Override public void onResponse(Call<ApiResponse<List<Country>>> call, Response<ApiResponse<List<Country>>> resp) {
                if (resp.isSuccessful() && resp.body() != null) cb.onSuccess(resp.body().data);
                else cb.onError(resp.errorBody()!=null?parseError(resp.errorBody()):"فشل الاتصال");
            }
            @Override public void onFailure(Call<ApiResponse<List<Country>>> call, Throwable t) {
                cb.onError(t.getMessage()!=null?t.getMessage():"فشل الشبكة");
            }
        });
    }

    public void getUniversities(ApiCallback<List<University>> cb) {
        api.universities().enqueue(new Callback<ApiResponse<List<University>>>() {
            @Override public void onResponse(Call<ApiResponse<List<University>>> call, Response<ApiResponse<List<University>>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body().data);
                else cb.onError(resp.errorBody()!=null?parseError(resp.errorBody()):"فشل الاتصال");
            }
            @Override public void onFailure(Call<ApiResponse<List<University>>> call, Throwable t) {
                cb.onError(t.getMessage()!=null?t.getMessage():"فشل الشبكة");
            }
        });
    }

    public void getColleges(long universityId, ApiCallback<List<College>> cb) {
        api.colleges(universityId).enqueue(new Callback<ApiResponse<List<College>>>() {
            @Override public void onResponse(Call<ApiResponse<List<College>>> call, Response<ApiResponse<List<College>>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body().data);
                else cb.onError(resp.errorBody()!=null?parseError(resp.errorBody()):"فشل الاتصال");
            }
            @Override public void onFailure(Call<ApiResponse<List<College>>> call, Throwable t) {
                cb.onError(t.getMessage()!=null?t.getMessage():"فشل الشبكة");
            }
        });
    }

    public void getMajors(long collegeId, ApiCallback<List<Major>> cb) {
        api.majors(collegeId).enqueue(new Callback<ApiResponse<List<Major>>>() {
            @Override public void onResponse(Call<ApiResponse<List<Major>>> call, Response<ApiResponse<List<Major>>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body().data);
                else cb.onError(resp.errorBody()!=null?parseError(resp.errorBody()):"فشل الاتصال");
            }
            @Override public void onFailure(Call<ApiResponse<List<Major>>> call, Throwable t) {
                cb.onError(t.getMessage()!=null?t.getMessage():"فشل الشبكة");
            }
        });
    }
}
