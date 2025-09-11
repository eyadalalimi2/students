package com.eyadalalimi.students.core.network;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class IdempotencyInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        String method = req.method();
        if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
            req = req.newBuilder()
                    .addHeader("Idempotency-Key", UUID.randomUUID().toString())
                    .build();
        }
        return chain.proceed(req);
    }
}
