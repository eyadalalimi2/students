package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.response.AssetResponse;

import retrofit2.Call;

public class AssetsRepository {
    private final ApiService api;
    public AssetsRepository(Context ctx){ this.api = new ApiClient(ctx).create(ApiService.class); }

    public Call<AssetResponse> list(){ return api.assets(); }
    public Call<AssetResponse> one(long id){ return api.asset(id); }
}
