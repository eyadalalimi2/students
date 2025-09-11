package com.eyadalalimi.students.ui.activity.auth;

import android.os.Bundle;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityResetPasswordBinding;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.AuthRepository;
import com.eyadalalimi.students.response.MessageResponse;

public class ResetPasswordActivity extends BaseActivity {
    private ActivityResetPasswordBinding binding;
    private AuthRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo = new AuthRepository(this);
        setupToolbar(binding.toolbar, getString(R.string.auth_reset), true);

        binding.btnReset.setOnClickListener(v -> {
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
            String token = binding.etToken.getText() != null ? binding.etToken.getText().toString().trim() : "";
            String p1 = binding.etPass1.getText() != null ? binding.etPass1.getText().toString().trim() : "";
            String p2 = binding.etPass2.getText() != null ? binding.etPass2.getText().toString().trim() : "";

            if (email.isEmpty() || token.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
                Toast.makeText(this, "أكمل الحقول", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!p1.equals(p2)) {
                Toast.makeText(this, "كلمتا المرور غير متطابقتين", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.btnReset.setEnabled(false);
            repo.resetPassword(email, token, p1, p2, new ApiCallback<MessageResponse>() {
                @Override public void onSuccess(MessageResponse data) {
                    Toast.makeText(ResetPasswordActivity.this, data != null && data.message != null ? data.message : "تم التعيين", Toast.LENGTH_LONG).show();
                    finish();
                }
                @Override public void onError(String message) {
                    binding.btnReset.setEnabled(true);
                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
