package com.eyadalalimi.students.core.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context appCtx;

    public AuthInterceptor(Context ctx) {
        this.appCtx = ctx.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        // نضبط Accept دائمًا
        Request.Builder builder = original.newBuilder()
                .header("Accept", "application/json");

        // نقرأ التوكن من نفس الـ SharedPreferences التي يكتب لها AuthRepository
        SharedPreferences sp = appCtx.getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = sp.getString("token", null);

        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }

        return chain.proceed(builder.build());
    }
}
