package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.Asset;
import com.eyadalalimi.students.response.ApiResponse;
import com.eyadalalimi.students.response.ListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetsRepository {

    public interface ApiCallback<T> {
        void onSuccess(T data);
        void onError(String msg);
    }

    private final ApiService api;

    public AssetsRepository(Context ctx) {
        this.api = ApiClient.get(ctx);
    }

    public void list(Integer limit, String cursor, Long materialId, String category, final ApiCallback<ListResponse<Asset>> cb) {
        api.assets(limit, cursor, materialId, category).enqueue(new Callback<ListResponse<Asset>>() {
            @Override public void onResponse(Call<ListResponse<Asset>> call, Response<ListResponse<Asset>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body());
                else cb.onError("فشل تحميل المحتوى العام");
            }
            @Override public void onFailure(Call<ListResponse<Asset>> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void get(long id, final ApiCallback<ApiResponse<Asset>> cb) {
        api.asset(id).enqueue(new Callback<ApiResponse<Asset>>() {
            @Override public void onResponse(Call<ApiResponse<Asset>> call, Response<ApiResponse<Asset>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body());
                else cb.onError("فشل جلب التفاصيل");
            }
            @Override public void onFailure(Call<ApiResponse<Asset>> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }
}
