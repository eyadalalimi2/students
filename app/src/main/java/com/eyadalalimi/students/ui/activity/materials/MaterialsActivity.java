package com.eyadalalimi.students.ui.activity.materials;

import android.os.Bundle;

import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivitySimpleListBinding;

public class MaterialsActivity extends BaseActivity {
    private ActivitySimpleListBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySimpleListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, "المواد", true);
        binding.tvInfo.setText("قائمة المواد — سيتم ربطها لاحقًا بـ /materials");
    }
}
