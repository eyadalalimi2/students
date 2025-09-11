package com.eyadalalimi.students.ui.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityRegisterBinding;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.AuthRepository;
import com.eyadalalimi.students.response.TokenResponse;

public class RegisterActivity extends BaseActivity {
    private ActivityRegisterBinding binding;
    private AuthRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo = new AuthRepository(this);
        setupToolbar(binding.toolbar, getString(R.string.auth_register), true);

        binding.btnRegister.setOnClickListener(v -> {
            String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
            String p1 = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";
            String p2 = binding.etPassword2.getText() != null ? binding.etPassword2.getText().toString().trim() : "";

            if (name.isEmpty() || email.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
                Toast.makeText(this, "أكمل الحقول المطلوبة", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!p1.equals(p2)) {
                Toast.makeText(this, "كلمتا المرور غير متطابقتين", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.btnRegister.setEnabled(false);
            repo.register(name, email, p1, p2, "android-" + android.os.Build.VERSION.SDK_INT + "-" + android.os.Build.MODEL,
                    new ApiCallback<TokenResponse>() {
                        @Override public void onSuccess(TokenResponse data) {
                            Toast.makeText(RegisterActivity.this, "تم إنشاء الحساب. تحقق البريد مطلوب.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, VerifyEmailActivity.class));
                            finish();
                        }
                        @Override public void onError(String message) {
                            binding.btnRegister.setEnabled(true);
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
