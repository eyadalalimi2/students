package com.eyadalalimi.students.ui.activity.auth;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityVerifyEmailBinding;

public class VerifyEmailActivity extends BaseActivity {
    private ActivityVerifyEmailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar(binding.toolbar, getString(R.string.auth_verify_email), true);

        // TODO: لاحقًا: إدخال OTP وإرساله
    }
}
