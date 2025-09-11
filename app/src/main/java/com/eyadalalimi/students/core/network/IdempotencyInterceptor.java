package com.eyadalalimi.students.core.network;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class IdempotencyInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        String method = req.method().toUpperCase(Locale.ROOT);

        boolean isMutating = method.equals("POST") || method.equals("PUT") || method.equals("PATCH") || method.equals("DELETE");
        if (isMutating && req.header("Idempotency-Key") == null) {
            req = req.newBuilder()
                    .header("Idempotency-Key", UUID.randomUUID().toString())
                    .build();
        }
        return chain.proceed(req);
    }
}
