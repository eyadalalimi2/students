package com.eyadalalimi.students.repo;

import android.content.Context;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.User;
import com.eyadalalimi.students.response.BaseResponse;
import com.eyadalalimi.students.response.UserResponse;

import retrofit2.Call;

public class ProfileRepository {
    private final ApiService api;
    public ProfileRepository(Context ctx){ this.api = new ApiClient(ctx).create(ApiService.class); }

    public Call<UserResponse> profile(){ return api.profile(); }
    public Call<UserResponse> update(User body){ return api.updateProfile(body); }
    public Call<BaseResponse> changePassword(User body){ return api.changePassword(body); }
}
