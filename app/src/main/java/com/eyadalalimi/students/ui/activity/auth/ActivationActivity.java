package com.eyadalalimi.students.ui.activity.auth;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityActivationBinding;

public class ActivationActivity extends BaseActivity {
    private ActivityActivationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActivationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar(binding.toolbar, getString(R.string.auth_activation), true);

        // TODO: لاحقًا: إدخال كود التفعيل
    }
}
