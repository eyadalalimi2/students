package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.Content;
import com.eyadalalimi.students.response.ApiResponse;
import com.eyadalalimi.students.response.ListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentsRepository {

    public interface ApiCallback<T> {
        void onSuccess(T data);
        void onError(String msg);
    }

    private final ApiService api;

    public ContentsRepository(Context ctx) {
        this.api = ApiClient.get(ctx);
    }

    public void list(Integer limit, String cursor, Long materialId, String type, final ApiCallback<ListResponse<Content>> cb) {
        api.contents(limit, cursor, materialId, type).enqueue(new Callback<ListResponse<Content>>() {
            @Override public void onResponse(Call<ListResponse<Content>> call, Response<ListResponse<Content>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body());
                else cb.onError("فشل تحميل المحتوى الخاص");
            }
            @Override public void onFailure(Call<ListResponse<Content>> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void get(long id, final ApiCallback<ApiResponse<Content>> cb) {
        api.content(id).enqueue(new Callback<ApiResponse<Content>>() {
            @Override public void onResponse(Call<ApiResponse<Content>> call, Response<ApiResponse<Content>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body());
                else cb.onError("فشل جلب التفاصيل");
            }
            @Override public void onFailure(Call<ApiResponse<Content>> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }
}
