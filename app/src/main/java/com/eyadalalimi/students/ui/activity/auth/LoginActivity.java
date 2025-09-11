package com.eyadalalimi.students.ui.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.core.data.SessionManager;
import com.eyadalalimi.students.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);
        setupToolbar(binding.toolbar, getString(R.string.auth_login), true);

        binding.btnLogin.setOnClickListener(v -> {
            // لاحقاً: استبدال بهذا نداء API حقيقي
            session.loginWithToken("DUMMY_TOKEN");
            Toast.makeText(this, "تم تسجيل الدخول (تجريبي). تحقق البريد مطلوب.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, VerifyEmailActivity.class));
            finish();
        });
    }
}
