package com.eyadalalimi.students.ui.activity.profile;

import android.os.Bundle;

import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivitySimpleListBinding;

public class ProfileActivity extends BaseActivity {
    private ActivitySimpleListBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySimpleListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, "ملفي", true);
        binding.tvInfo.setText("شاشة الملف الشخصي — ستعرض/تعدّل /me/profile لاحقًا");
    }
}
