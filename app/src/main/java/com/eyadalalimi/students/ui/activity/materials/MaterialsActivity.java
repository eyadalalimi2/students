package com.eyadalalimi.students.ui.activity.materials;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityMaterialsBinding;

public class MaterialsActivity extends BaseActivity {
    private ActivityMaterialsBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMaterialsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.title_materials), true);
        // لاحقًا: Grid/List للمواد
    }
}
