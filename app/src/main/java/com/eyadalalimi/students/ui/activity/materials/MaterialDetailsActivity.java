package com.eyadalalimi.students.ui.activity.materials;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityMaterialDetailsBinding;

public class MaterialDetailsActivity extends BaseActivity {
    private ActivityMaterialDetailsBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMaterialDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.title_material_details), true);
    }
}
