package com.eyadalalimi.students.ui.activity.splash;

import android.os.Bundle;

import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivitySplashBinding;

public class SplashActivity extends BaseActivity {
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // مبدئيًا بدون Toolbar في السبلاش
        // TODO: لاحقًا: توجيه حسب حالة الحساب
    }
}
