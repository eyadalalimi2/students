package com.eyadalalimi.students.core.network;

import android.content.Context;

import com.eyadalalimi.students.core.data.PreferencesStore;
import com.eyadalalimi.students.core.util.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private final Retrofit retrofit;

    public ApiClient(Context ctx) {
        PreferencesStore store = new PreferencesStore(ctx);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new ConnectivityInterceptor(ctx))
                .addInterceptor(new IdempotencyInterceptor())
                .addInterceptor(new AuthInterceptor(store::getToken))
                .addInterceptor(logging)
                .authenticator(new TokenAuthenticator(() -> null)) // لا تجديد حالياً
                .build();

        Gson gson = new GsonBuilder().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }
}
