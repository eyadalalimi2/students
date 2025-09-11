package com.eyadalalimi.students.ui.activity.assets;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivitySimpleListBinding;

public class AssetsListActivity extends BaseActivity {
    private ActivitySimpleListBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySimpleListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.app_name) + " - المحتوى العام", true);
        binding.tvInfo.setText("قائمة المحتوى العام (Assets) — سيتم ربطها لاحقًا بـ /assets");
    }
}
