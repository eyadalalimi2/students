package com.eyadalalimi.students.ui.activity.auth;

import android.os.Bundle;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityResetPasswordBinding;

public class ResetPasswordActivity extends BaseActivity {
    private ActivityResetPasswordBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.auth_reset), true);

        binding.btnReset.setOnClickListener(v -> {
            // لاحقًا: تحقق رمز/كلمة — /auth/password/reset
            Toast.makeText(this, "تمت إعادة التعيين (تجريبي)", Toast.LENGTH_SHORT).show();
        });
    }
}
