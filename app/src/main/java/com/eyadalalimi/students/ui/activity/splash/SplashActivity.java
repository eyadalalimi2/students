package com.eyadalalimi.students.ui.activity.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.eyadalalimi.students.model.User;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.AuthRepository;
import com.eyadalalimi.students.ui.activity.auth.ActivationActivity;
import com.eyadalalimi.students.ui.activity.auth.LoginActivity;
import com.eyadalalimi.students.ui.activity.auth.VerifyEmailActivity;
import com.eyadalalimi.students.ui.activity.home.HomeActivity;

public class SplashActivity extends AppCompatActivity {

    private AuthRepository authRepo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authRepo = new AuthRepository(this);

        // لا نستخدم Layout لتفادي أي موارد ناقصة. ننفّذ التوجيه فورًا.
        String token = readToken();
        if (token == null || token.isEmpty()) {
            goLogin();
            return;
        }

        // لدينا توكن: نتحقق من الحالة الفعلية عبر /auth/me
        authRepo.me(new ApiCallback<User>() {
            @Override public void onSuccess(User u) {
                if (u == null) { clearToken(); goLogin(); return; }

                boolean emailVerified = u.email_verified_at != null && !u.email_verified_at.trim().isEmpty();
                boolean activated = (u.has_active_subscription != null && u.has_active_subscription);

                if (!emailVerified) {
                    go(VerifyEmailActivity.class);
                } else if (!activated) {
                    go(ActivationActivity.class);
                } else {
                    go(HomeActivity.class);
                }
            }
            @Override public void onError(String message) {
                // أي فشل هنا نعتبره جلسة غير صالحة ونعيد للمصادقة
                clearToken();
                goLogin();
            }
        });
    }

    private void goLogin() { go(LoginActivity.class); }

    private void go(Class<?> cls) {
        Intent i = new Intent(this, cls);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private String readToken() {
        SharedPreferences sp = getSharedPreferences("auth", Context.MODE_PRIVATE);
        return sp.getString("token", null);
    }

    private void clearToken() {
        SharedPreferences sp = getSharedPreferences("auth", Context.MODE_PRIVATE);
        sp.edit().remove("token").apply();
    }
}
