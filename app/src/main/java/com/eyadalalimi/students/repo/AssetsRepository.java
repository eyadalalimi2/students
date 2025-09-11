package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.Asset;
import com.eyadalalimi.students.model.PagedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetsRepository {

    private final ApiService api;

    public AssetsRepository(Context ctx) {
        this.api = ApiClient.get(ctx);
    }

    public void list(Integer limit, Long materialId, String category, String cursor,
                     ApiCallback<List<Asset>> cb) {
        api.assets(limit, materialId, category, cursor)
                .enqueue(new Callback<PagedResponse<Asset>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<Asset>> call, Response<PagedResponse<Asset>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            cb.onSuccess(resp.body().data);
                        } else {
                            cb.onError("تعذّر تحميل المحتوى العام");
                        }
                    }

                    @Override
                    public void onFailure(Call<PagedResponse<Asset>> call, Throwable t) {
                        cb.onError(t.getMessage() != null ? t.getMessage() : "فشل الشبكة");
                    }
                });
    }
}
