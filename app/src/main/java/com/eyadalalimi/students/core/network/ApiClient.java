package com.eyadalalimi.students.core.network;

import android.content.Context;

import com.eyadalalimi.students.BuildConfig;
import com.eyadalalimi.students.core.data.PreferencesStore;
import com.eyadalalimi.students.core.util.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    private static volatile ApiClient INSTANCE;
    private final ApiService api;

    private ApiClient(Context appCtx) {
        PreferencesStore store = new PreferencesStore(appCtx);

        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
                .connectTimeout(Constants.CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                .readTimeout(Constants.READ_TIMEOUT_SEC, TimeUnit.SECONDS)
                .writeTimeout(Constants.WRITE_TIMEOUT_SEC, TimeUnit.SECONDS)
                .addInterceptor(new ConnectivityInterceptor(appCtx))
                .addInterceptor(new IdempotencyInterceptor())
                .addInterceptor(new AuthInterceptor(store))
                .authenticator(new TokenAuthenticator(store));

        // لوج الشبكة: BODY في Debug، NONE في Release
        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        okBuilder.addInterceptor(log);

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        this.api = retrofit.create(ApiService.class);
    }

    public static ApiService get(Context ctx) {
        if (INSTANCE == null) {
            synchronized (ApiClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApiClient(ctx.getApplicationContext());
                }
            }
        }
        return INSTANCE.api;
    }
}
