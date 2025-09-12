package com.eyadalalimi.students.core.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    // غيّر هذه القيم إذا تغيّر الدومين أو المسارات عندك
    private static final String API_BASE = "https://obdcodehub.com/api/v1/";
    private static final String FILES_BASE = "https://obdcodehub.com/storage/";

    private static volatile ApiService cachedService;

    private ApiClient() {}

    /** استخدمها في كل الريبو: ApiClient.get(ctx) ترجع ApiService جاهز */
    public static ApiService get(Context ctx) {
        if (cachedService == null) {
            synchronized (ApiClient.class) {
                if (cachedService == null) {
                    cachedService = buildRetrofit(ctx.getApplicationContext())
                            .create(ApiService.class);
                }
            }
        }
        return cachedService;
    }

    /** متاحة لو كنت تحتاج Retrofit نفسه (نادراً) */
    private static Retrofit buildRetrofit(Context appCtx) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .client(buildOkHttp(appCtx))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    /** عميل OkHttp مع هيدرز التوكن واللغة والـ Idempotency-Key للكتابات */
    private static OkHttpClient buildOkHttp(Context appCtx) {
        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY); // لو تبغى أخف: BASIC

        Interceptor authAndHeaders = chain -> {
            Request original = chain.request();
            Request.Builder b = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-Language", "ar");

            // Authorization: Bearer <token> إذا موجود
            String token = getToken(appCtx);
            if (token != null && !token.isEmpty()) {
                b.header("Authorization", "Bearer " + token);
            }

            // أضف Idempotency-Key لأي طلب غير GET
            String method = original.method();
            if (!"GET".equalsIgnoreCase(method)) {
                b.header("Idempotency-Key", UUID.randomUUID().toString());
            }

            // تأكد من أن الـ URL صالح (لو استُخدم base غير منتهي بـ /)
            HttpUrl url = original.url();
            b.url(url);

            return chain.proceed(b.build());
        };

        return new OkHttpClient.Builder()
                .addInterceptor(authAndHeaders)
                .addInterceptor(log)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(45, TimeUnit.SECONDS)
                .build();
    }

    // =======================
    // دوال مطلوبة من بعض الأكواد
    // =======================

    /** أساس الـ API (منتهي بـ /) */
    public static String getBaseUrl() {
        return API_BASE;
    }

    /** أساس ملفات التخزين (صور/ملفات) */
    public static String getFilesBaseUrl() {
        return FILES_BASE;
    }

    /** يبني رابط كامل للملف إن كان المسار نسبيًا مثل profiles/xxx.jpg */
    public static String toAbsoluteFileUrl(String pathOrUrl) {
        if (pathOrUrl == null || pathOrUrl.trim().isEmpty()) return null;
        String p = pathOrUrl.trim();
        if (p.startsWith("http://") || p.startsWith("https://")) {
            return p;
        }
        // لا تكرر الشرطة الأمامية
        if (p.startsWith("/")) p = p.substring(1);
        return getFilesBaseUrl() + p;
    }

    /** يقرأ التوكن المحفوظ (SharedPreferences) ليُرسل مع الطلبات */
    public static String getToken(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE);
        return sp.getString("token", null);
    }
}
