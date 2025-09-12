package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.FeedItem;
import com.eyadalalimi.students.model.PagedResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedRepository {

    private final ApiService api;

    public FeedRepository(Context ctx) {
        this.api = ApiClient.get(ctx);
    }

    public void list(Integer limit, String cursor, ApiCallback<PagedResponse<FeedItem>> cb) {
        api.meFeed(limit, cursor).enqueue(new Callback<PagedResponse<FeedItem>>() {
            @Override public void onResponse(Call<PagedResponse<FeedItem>> call, Response<PagedResponse<FeedItem>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    cb.onSuccess(resp.body());
                } else {
                    cb.onError("تعذّر تحميل الخلاصة");
                }
            }
            @Override public void onFailure(Call<PagedResponse<FeedItem>> call, Throwable t) {
                cb.onError(t.getMessage() != null ? t.getMessage() : "فشل الشبكة");
            }
        });
    }
}
