package com.eyadalalimi.students.ui.activity.profile;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityProfileBinding;

public class ProfileActivity extends BaseActivity {
    private ActivityProfileBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.title_profile), true);
        // لاحقًا: تحميل/تحديث الملف من /me/profile
    }
}
