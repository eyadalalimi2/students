package com.eyadalalimi.students.ui.activity.auth;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityRegisterBinding;

public class RegisterActivity extends BaseActivity {
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar(binding.toolbar, getString(R.string.auth_register), true);

        // TODO: لاحقًا: إرسال نموذج التسجيل
    }
}
