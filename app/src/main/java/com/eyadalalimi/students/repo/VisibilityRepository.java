package com.eyadalalimi.students.repo;

import android.content.Context;
import android.text.TextUtils;

import com.eyadalalimi.students.core.data.PreferencesStore;
import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.Visibility;
import com.eyadalalimi.students.response.ApiResponse;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisibilityRepository {
    private final ApiService api;
    private final PreferencesStore store;

    public VisibilityRepository(Context ctx) {
        this.api = ApiClient.get(ctx);
        this.store = new PreferencesStore(ctx);
    }

    public void fetch(ApiCallback<Visibility> cb) {
        api.visibility().enqueue(new Callback<ApiResponse<Visibility>>() {
            @Override public void onResponse(Call<ApiResponse<Visibility>> call, Response<ApiResponse<Visibility>> resp) {
                if (resp.isSuccessful() && resp.body() != null && resp.body().data != null) {
                    Visibility v = resp.body().data;
                    // خزّن allowed_sources كسلسلة مفصولة بفواصل
                    String joined = (v.allowedSources != null) ? TextUtils.join(",", v.allowedSources) : "";
                    store.setAllowedSources(joined);

                    // خزن معرّفات النطاق (اختياري الآن)
                    Long u = v.scope != null ? v.scope.universityId : null;
                    Long c = v.scope != null ? v.scope.collegeId    : null;
                    Long m = v.scope != null ? v.scope.majorId      : null;
                    store.setScope(u, c, m);

                    cb.onSuccess(v);
                } else {
                    cb.onError("تعذر جلب الرؤية");
                }
            }
            @Override public void onFailure(Call<ApiResponse<Visibility>> call, Throwable t) {
                cb.onError(t.getMessage() != null ? t.getMessage() : "فشل الشبكة");
            }
        });
    }

    // أدوات قراءة من التخزين
    public boolean canSeeAssets() {
        String s = store.getAllowedSources();
        return s != null && Arrays.asList(s.split(",")).contains("assets");
    }
    public boolean canSeeContents() {
        String s = store.getAllowedSources();
        return s != null && Arrays.asList(s.split(",")).contains("contents");
    }
}
