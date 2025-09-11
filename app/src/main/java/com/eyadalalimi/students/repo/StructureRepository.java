package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.response.BaseResponse;

import retrofit2.Call;

public class StructureRepository {
    private final ApiService api;
    public StructureRepository(Context ctx){ this.api = new ApiClient(ctx).create(ApiService.class); }

    public Call<BaseResponse> countries(){ return api.countries(); }
    public Call<BaseResponse> universities(){ return api.universities(); }
    public Call<BaseResponse> colleges(long id){ return api.colleges(id); }
    public Call<BaseResponse> majors(long id){ return api.majors(id); }
}
