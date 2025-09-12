package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.College;
import com.eyadalalimi.students.model.Country;
import com.eyadalalimi.students.model.Major;
import com.eyadalalimi.students.model.Material;
import com.eyadalalimi.students.model.University;
import com.eyadalalimi.students.response.ApiResponse;
import com.eyadalalimi.students.response.ListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogRepository {
    private final ApiService api;

    public CatalogRepository(Context ctx) {
        this.api = ApiClient.get(ctx); // بدون .create(...)
    }

    public void countries(ApiCallback<List<Country>> cb) {
        api.countries().enqueue(new Callback<com.eyadalalimi.students.response.ApiResponse<List<Country>>>() {
            @Override public void onResponse(Call<com.eyadalalimi.students.response.ApiResponse<List<Country>>> call,
                                             Response<com.eyadalalimi.students.response.ApiResponse<List<Country>>> resp) {
                if (resp.isSuccessful() && resp.body() != null) cb.onSuccess(resp.body().data);
                else cb.onError("فشل جلب الدول");
            }
            @Override public void onFailure(Call<com.eyadalalimi.students.response.ApiResponse<List<Country>>> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void universities(ApiCallback<List<University>> cb) {
        api.universities().enqueue(new Callback<com.eyadalalimi.students.response.ApiResponse<List<University>>>() {
            @Override public void onResponse(Call<com.eyadalalimi.students.response.ApiResponse<List<University>>> call,
                                             Response<com.eyadalalimi.students.response.ApiResponse<List<University>>> resp) {
                if (resp.isSuccessful() && resp.body() != null) cb.onSuccess(resp.body().data);
                else cb.onError("فشل جلب الجامعات");
            }
            @Override public void onFailure(Call<com.eyadalalimi.students.response.ApiResponse<List<University>>> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void colleges(long universityId, ApiCallback<List<College>> cb) {
        api.colleges(universityId).enqueue(new Callback<com.eyadalalimi.students.response.ApiResponse<List<College>>>() {
            @Override public void onResponse(Call<com.eyadalalimi.students.response.ApiResponse<List<College>>> call,
                                             Response<com.eyadalalimi.students.response.ApiResponse<List<College>>> resp) {
                if (resp.isSuccessful() && resp.body() != null) cb.onSuccess(resp.body().data);
                else cb.onError("فشل جلب الكليات");
            }
            @Override public void onFailure(Call<com.eyadalalimi.students.response.ApiResponse<List<College>>> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void majors(long collegeId, ApiCallback<List<Major>> cb) {
        api.majors(collegeId).enqueue(new Callback<com.eyadalalimi.students.response.ApiResponse<List<Major>>>() {
            @Override public void onResponse(Call<com.eyadalalimi.students.response.ApiResponse<List<Major>>> call,
                                             Response<com.eyadalalimi.students.response.ApiResponse<List<Major>>> resp) {
                if (resp.isSuccessful() && resp.body() != null) cb.onSuccess(resp.body().data);
                else cb.onError("فشل جلب التخصصات");
            }
            @Override public void onFailure(Call<com.eyadalalimi.students.response.ApiResponse<List<Major>>> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }
    // ===== Materials  =====
    public void listMaterials(Integer limit, String cursor, ApiCallback<ListResponse<Material>> cb) {
        api.materials(limit, cursor, null, null, null, null)
                .enqueue(new Callback<ListResponse<Material>>() {
                    @Override public void onResponse(Call<ListResponse<Material>> call,
                                                     Response<ListResponse<Material>> resp) {
                        if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body());
                        else cb.onError("فشل جلب المواد");
                    }
                    @Override public void onFailure(Call<ListResponse<Material>> call, Throwable t) {
                        cb.onError(t.getMessage());
                    }
                });
    }

    // إن كنت تحتاج موادًا مختصرة من هنا:
    public void list(Integer limit, String cursor, String scope,
                     Long universityId, Long collegeId, Long majorId,
                     ApiCallback<ListResponse<Material>> cb) {
        api.materials(limit, cursor, scope, universityId, collegeId, majorId)
                .enqueue(new Callback<ListResponse<Material>>() {
                    @Override public void onResponse(Call<ListResponse<Material>> call, Response<ListResponse<Material>> resp) {
                        if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body());
                        else cb.onError("فشل جلب المواد");
                    }
                    @Override public void onFailure(Call<ListResponse<Material>> call, Throwable t) {
                        cb.onError(t.getMessage());
                    }
                });
    }

    public void details(long id, ApiCallback<ApiResponse<Material>> cb) {
        api.material(id).enqueue(new Callback<ApiResponse<Material>>() {
            @Override public void onResponse(Call<ApiResponse<Material>> call, Response<ApiResponse<Material>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body());
                else cb.onError("فشل جلب تفاصيل المادة");
            }
            @Override public void onFailure(Call<ApiResponse<Material>> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }



public interface ApiCallback<T> {
        void onSuccess(T data);
        void onError(String msg);
    }
}
