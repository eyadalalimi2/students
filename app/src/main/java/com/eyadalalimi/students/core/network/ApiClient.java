package com.eyadalalimi.students.core.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static volatile ApiService INSTANCE;

    public static ApiService get(Context ctx) {
        if (INSTANCE == null) {
            synchronized (ApiClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = build(ctx.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private static ApiService build(Context appCtx) {
        // Logging (Debug فقط)
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> {
            // تجنب طباعة التوكن نفسه (OkHttp لا يدعم redaction افتراضيًا، لكننا لا نطبع الهيدرز يدويًا هنا)
            android.util.Log.i("okhttp.OkHttpClient", message);
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(appCtx))          // << يحقن Bearer من SharedPreferences("auth")
                .addInterceptor(new IdempotencyInterceptor())
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        return new Retrofit.Builder()
                .baseUrl("https://obdcodehub.com/api/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ApiService.class);
    }

    private ApiClient() {}
}
