package com.eyadalalimi.students.ui.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityLoginBinding;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.AuthRepository;
import com.eyadalalimi.students.response.TokenResponse;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    private AuthRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo = new AuthRepository(this);
        setupToolbar(binding.toolbar, getString(R.string.auth_login), true);

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
            String pass  = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "أدخل البريد وكلمة المرور", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.btnLogin.setEnabled(false);
            repo.login(email, pass, "android-" + android.os.Build.VERSION.SDK_INT + "-" + android.os.Build.MODEL,
                    new ApiCallback<TokenResponse>() {
                        @Override public void onSuccess(TokenResponse data) {
                            Toast.makeText(LoginActivity.this, "تم تسجيل الدخول. تحقق البريد مطلوب.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, VerifyEmailActivity.class));
                            finish();
                        }
                        @Override public void onError(String message) {
                            binding.btnLogin.setEnabled(true);
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        binding.btnGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        binding.btnGoForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }
}
