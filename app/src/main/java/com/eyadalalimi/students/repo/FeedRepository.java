package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.response.FeedResponse;

import retrofit2.Call;

public class FeedRepository {
    private final ApiService api;
    public FeedRepository(Context ctx){ this.api = new ApiClient(ctx).create(ApiService.class); }

    public Call<FeedResponse> feed(Integer limit, String cursor){ return api.feed(limit, cursor); }
}
