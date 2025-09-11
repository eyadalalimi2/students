package com.eyadalalimi.students.ui.activity.auth;

import android.os.Bundle;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.core.util.Validator;
import com.eyadalalimi.students.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends BaseActivity {
    private ActivityForgotPasswordBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.auth_forgot), true);

        binding.btnSend.setOnClickListener(v -> {
            String email = binding.etEmail.getText() == null ? "" : binding.etEmail.getText().toString().trim();
            if (!Validator.isEmail(email)) {
                Toast.makeText(this, getString(R.string.msg_invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "تم الإرسال (تجريبي)", Toast.LENGTH_SHORT).show();
        });
    }
}
