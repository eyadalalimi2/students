package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.request.sub.ActivateCodeRequest;
import com.eyadalalimi.students.response.SubscriptionResponse;

import retrofit2.Call;

public class ActivationRepository {
    private final ApiService api;
    public ActivationRepository(Context ctx){ this.api = new ApiClient(ctx).create(ApiService.class); }

    public Call<SubscriptionResponse> activate(String code){
        return api.activate(new ActivateCodeRequest(code));
    }
}
