package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.Material;
import com.eyadalalimi.students.response.ApiResponse;
import com.eyadalalimi.students.response.ListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaterialsRepository {
    private final ApiService api;

    public MaterialsRepository(Context ctx) {
        this.api = ApiClient.get(ctx);
    }

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
}
