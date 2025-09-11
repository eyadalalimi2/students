package com.eyadalalimi.students.ui.activity.assets;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityAssetsListBinding;

public class AssetsListActivity extends BaseActivity {
    private ActivityAssetsListBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssetsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.title_assets), true);
        // لاحقًا: Recycler + فلاتر
    }
}
