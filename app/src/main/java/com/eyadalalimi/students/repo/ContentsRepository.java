package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.response.ContentResponse;

import retrofit2.Call;

public class ContentsRepository {
    private final ApiService api;
    public ContentsRepository(Context ctx){ this.api = new ApiClient(ctx).create(ApiService.class); }

    public Call<ContentResponse> list(){ return api.contents(); }
    public Call<ContentResponse> one(long id){ return api.content(id); }
}
