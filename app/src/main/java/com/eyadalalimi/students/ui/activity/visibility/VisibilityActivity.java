package com.eyadalalimi.students.ui.activity.visibility;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityVisibilityBinding;

public class VisibilityActivity extends BaseActivity {
    private ActivityVisibilityBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVisibilityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.title_visibility), true);
        // لاحقًا: عرض allowed_sources و u-scope
    }
}
