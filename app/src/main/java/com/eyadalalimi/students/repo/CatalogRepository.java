package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.response.MaterialResponse;

import retrofit2.Call;

public class CatalogRepository {
    private final ApiService api;
    public CatalogRepository(Context ctx){ this.api = new ApiClient(ctx).create(ApiService.class); }

    public Call<MaterialResponse> materials(){ return api.materials(); }
    public Call<MaterialResponse> material(long id){ return api.material(id); }
}
