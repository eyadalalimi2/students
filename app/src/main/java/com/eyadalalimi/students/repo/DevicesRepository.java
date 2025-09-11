package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.response.BaseResponse;

import retrofit2.Call;

public class DevicesRepository {
    private final ApiService api;
    public DevicesRepository(Context ctx){ this.api = new ApiClient(ctx).create(ApiService.class); }

    public Call<BaseResponse> list(){ return api.devices(); }
    public Call<BaseResponse> delete(long id){ return api.deleteDevice(id); }
}
