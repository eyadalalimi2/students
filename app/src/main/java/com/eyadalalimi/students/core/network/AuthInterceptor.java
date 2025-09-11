package com.eyadalalimi.students.core.network;

import androidx.annotation.Nullable;

import com.eyadalalimi.students.core.data.PreferencesStore;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class AuthInterceptor implements Interceptor {
    private final PreferencesStore store;

    AuthInterceptor(PreferencesStore store) {
        this.store = store;
    }

    @Nullable
    private String token() { return store.getToken(); }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        String t = token();
        if (t != null && !t.isEmpty()) {
            req = req.newBuilder()
                    .addHeader("Authorization", "Bearer " + t)
                    .build();
        }
        return chain.proceed(req);
    }
}
