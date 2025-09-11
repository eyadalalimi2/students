package com.eyadalalimi.students.ui.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityLoginBinding;
import com.eyadalalimi.students.model.User;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.AuthRepository;
import com.eyadalalimi.students.response.TokenResponse;
import com.eyadalalimi.students.ui.activity.home.HomeActivity;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private AuthRepository authRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepo = new AuthRepository(this);
        setupToolbar(binding.toolbar, getString(R.string.auth_login), true);

        binding.btnLogin.setOnClickListener(v -> doLogin());

        if (binding.tvGoRegister != null) {
            binding.tvGoRegister.setOnClickListener(v ->
                    startActivity(new Intent(this, RegisterActivity.class)));
        }
        if (binding.tvForgot != null) {
            binding.tvForgot.setOnClickListener(v ->
                    startActivity(new Intent(this, ForgotPasswordActivity.class)));
        }
    }

    private void doLogin() {
        String email = val(binding.etEmail);
        String pass  = val(binding.etPassword);
        if (email.isEmpty() || pass.isEmpty()) {
            toast("أدخل البريد وكلمة المرور");
            return;
        }

        setLoading(true);
        String loginDevice = "android-" + android.os.Build.VERSION.SDK_INT + "-" + android.os.Build.MODEL;

        authRepo.login(email, pass, loginDevice, new ApiCallback<TokenResponse>() {
            @Override public void onSuccess(TokenResponse data) {
                setLoading(false);
                toast("تم تسجيل الدخول");

                if (data != null && data.user != null) {
                    routeAfterAuth(data.user);
                } else {
                    authRepo.me(new ApiCallback<User>() {
                        @Override public void onSuccess(User me) { routeAfterAuth(me); }
                        @Override public void onError(String message) {
                            toast("تعذر استرجاع الحساب: " + message);
                        }
                    });
                }
            }
            @Override public void onError(String message) {
                setLoading(false);
                toast(message);
            }
        });
    }

    private void routeAfterAuth(User u) {
        if (u == null) {
            toast("تعذر تحديد حالة الحساب");
            return;
        }
        if (u.email_verified_at == null || u.email_verified_at.trim().isEmpty()) {
            startActivity(new Intent(this, VerifyEmailActivity.class));
            finish();
            return;
        }
        Boolean activated = (u.has_active_subscription != null && u.has_active_subscription);
        if (!activated) {
            startActivity(new Intent(this, ActivationActivity.class));
            finish();
            return;
        }
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void setLoading(boolean loading) {
        binding.btnLogin.setEnabled(!loading);
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private String val(com.google.android.material.textfield.TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_LONG).show(); }
}
