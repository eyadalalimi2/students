package com.eyadalalimi.students.ui.activity.splash;

import android.content.Intent;
import android.os.Bundle;

import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.core.data.SessionManager;
import com.eyadalalimi.students.databinding.ActivitySplashBinding;
import com.eyadalalimi.students.ui.activity.auth.ActivationActivity;
import com.eyadalalimi.students.ui.activity.auth.LoginActivity;
import com.eyadalalimi.students.ui.activity.auth.VerifyEmailActivity;
import com.eyadalalimi.students.ui.activity.home.HomeActivity;

public class SplashActivity extends BaseActivity {
    private ActivitySplashBinding binding;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);

        binding.getRoot().postDelayed(this::route, 300); // تأخير بسيط لشاشة البداية
    }

    private void route() {
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        if (!session.isEmailVerified()) {
            startActivity(new Intent(this, VerifyEmailActivity.class));
            finish();
            return;
        }
        if (!session.isActivated()) {
            startActivity(new Intent(this, ActivationActivity.class));
            finish();
            return;
        }
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
