package com.eyadalalimi.students.ui.activity.splash;

import android.content.Intent;
import android.os.Bundle;

import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.core.data.SessionManager;
import com.eyadalalimi.students.databinding.ActivitySplashBinding;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.ProfileRepository;
import com.eyadalalimi.students.response.UserResponse;
import com.eyadalalimi.students.ui.activity.auth.ActivationActivity;
import com.eyadalalimi.students.ui.activity.auth.LoginActivity;
import com.eyadalalimi.students.ui.activity.auth.VerifyEmailActivity;
import com.eyadalalimi.students.ui.activity.home.HomeActivity;

public class SplashActivity extends BaseActivity {
    private ActivitySplashBinding binding;
    private SessionManager session;
    private ProfileRepository profileRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);
        profileRepo = new ProfileRepository(this);

        if (!session.isLoggedIn()) {
            go(LoginActivity.class);
            return;
        }

        // مزامنة سريعة مع الخادم: تؤكد التفعيل إذا كان موجوداً هناك
        profileRepo.fetchMe(new ApiCallback<UserResponse>() {
            @Override public void onSuccess(UserResponse data) { route(); }
            @Override public void onError(String message) { route(); } // فشل الشبكة: نكمل بالمنطق المحلي
        });
    }

    private void route() {
        if (!session.isEmailVerified()) { go(VerifyEmailActivity.class); return; }
        if (!session.isActivated()) { go(ActivationActivity.class); return; }
        go(HomeActivity.class);
    }

    private void go(Class<?> cls) {
        startActivity(new Intent(this, cls));
        finish();
    }
}
