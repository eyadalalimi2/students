package com.eyadalalimi.students.core.network;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class IdempotencyInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        String m = req.method();
        if ("POST".equals(m) || "PUT".equals(m) || "PATCH".equals(m) || "DELETE".equals(m)) {
            req = req.newBuilder()
                    .header("Idempotency-Key", UUID.randomUUID().toString())
                    .build();
        }
        return chain.proceed(req);
    }
}
