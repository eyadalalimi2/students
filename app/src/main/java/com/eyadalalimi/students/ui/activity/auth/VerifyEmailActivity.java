package com.eyadalalimi.students.ui.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityVerifyEmailBinding;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.AuthRepository;
import com.eyadalalimi.students.response.MessageResponse;
import com.eyadalalimi.students.response.VerifyEmailResponse;

public class VerifyEmailActivity extends BaseActivity {
    private ActivityVerifyEmailBinding binding;
    private AuthRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo = new AuthRepository(this);
        setupToolbar(binding.toolbar, getString(R.string.auth_verify_email), true);

        binding.btnVerify.setOnClickListener(v -> {
            String code = binding.etOtp.getText() != null ? binding.etOtp.getText().toString().trim() : "";
            if (code.length() != 6) {
                Toast.makeText(this, "أدخل رمز 6 أرقام", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = repo.getStoredEmail();
            if (email == null || email.isEmpty()) {
                Toast.makeText(this, "بريد غير معروف — أعد تسجيل الدخول", Toast.LENGTH_LONG).show();
                return;
            }
            binding.btnVerify.setEnabled(false);
            repo.verifyEmail(email, code, new ApiCallback<VerifyEmailResponse>() {
                @Override public void onSuccess(VerifyEmailResponse data) {
                    Toast.makeText(VerifyEmailActivity.this, "تم توثيق البريد.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(VerifyEmailActivity.this, ActivationActivity.class));
                    finish();
                }
                @Override public void onError(String message) {
                    binding.btnVerify.setEnabled(true);
                    Toast.makeText(VerifyEmailActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });

        binding.btnResend.setOnClickListener(v -> {
            String email = repo.getStoredEmail();
            if (email == null || email.isEmpty()) {
                Toast.makeText(this, "بريد غير معروف — أعد تسجيل الدخول", Toast.LENGTH_LONG).show();
                return;
            }
            binding.btnResend.setEnabled(false);
            repo.resendEmail(email, new ApiCallback<MessageResponse>() {
                @Override public void onSuccess(MessageResponse data) {
                    Toast.makeText(VerifyEmailActivity.this, data != null && data.message != null ? data.message : "تم الإرسال", Toast.LENGTH_LONG).show();
                    binding.btnResend.postDelayed(() -> binding.btnResend.setEnabled(true), 60000);
                }
                @Override public void onError(String message) {
                    binding.btnResend.setEnabled(true);
                    Toast.makeText(VerifyEmailActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
