package com.eyadalalimi.students.ui.activity.auth;

import android.os.Bundle;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityForgotPasswordBinding;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.AuthRepository;
import com.eyadalalimi.students.response.MessageResponse;

public class ForgotPasswordActivity extends BaseActivity {
    private ActivityForgotPasswordBinding binding;
    private AuthRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo = new AuthRepository(this);
        setupToolbar(binding.toolbar, getString(R.string.auth_forgot), true);

        binding.btnSend.setOnClickListener(v -> {
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
            if (email.isEmpty()) {
                Toast.makeText(this, "أدخل البريد", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.btnSend.setEnabled(false);
            repo.forgotPassword(email, new ApiCallback<MessageResponse>() {
                @Override public void onSuccess(MessageResponse data) {
                    Toast.makeText(ForgotPasswordActivity.this, data != null && data.message != null ? data.message : "تم الإرسال", Toast.LENGTH_LONG).show();
                    finish();
                }
                @Override public void onError(String message) {
                    binding.btnSend.setEnabled(true);
                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
