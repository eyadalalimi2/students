package com.eyadalalimi.students.core.network;

import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    public interface TokenProvider { @Nullable String getToken(); }

    private final TokenProvider provider;

    public AuthInterceptor(TokenProvider provider) {
        this.provider = provider;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = provider.getToken();
        if (token == null || token.isEmpty()) {
            return chain.proceed(original);
        }
        Request withAuth = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(withAuth);
    }
}
