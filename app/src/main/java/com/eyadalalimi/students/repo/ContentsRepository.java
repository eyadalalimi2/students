package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.Content;
import com.eyadalalimi.students.model.PagedResponse;
import com.eyadalalimi.students.response.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentsRepository {

    private final ApiService api;

    public ContentsRepository(Context ctx) {
        this.api = ApiClient.get(ctx);
    }

    public void list(Integer limit, String cursor, ApiCallback<List<Content>> cb) {
        api.contents(limit, cursor).enqueue(new Callback<PagedResponse<Content>>() {
            @Override public void onResponse(Call<PagedResponse<Content>> call, Response<PagedResponse<Content>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    cb.onSuccess(resp.body().data);
                } else if (resp.code() == 403) {
                    cb.onError("غير مخوّل لعرض المحتوى الخاص (تحقّق من الارتباط بالمؤسسة).");
                } else {
                    cb.onError("تعذّر تحميل المحتوى الخاص");
                }
            }
            @Override public void onFailure(Call<PagedResponse<Content>> call, Throwable t) {
                cb.onError(t.getMessage() != null ? t.getMessage() : "فشل الشبكة");
            }
        });
    }

    public void show(long id, ApiCallback<Content> cb) {
        api.contentDetails(id).enqueue(new Callback<ApiResponse<Content>>() {
            @Override public void onResponse(Call<ApiResponse<Content>> call, Response<ApiResponse<Content>> resp) {
                if (resp.isSuccessful() && resp.body() != null) cb.onSuccess(resp.body().data);
                else cb.onError("تعذّر جلب تفاصيل المحتوى");
            }
            @Override public void onFailure(Call<ApiResponse<Content>> call, Throwable t) {
                cb.onError(t.getMessage() != null ? t.getMessage() : "فشل الشبكة");
            }
        });
    }
}
