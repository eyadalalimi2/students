package com.eyadalalimi.students.ui.activity.contents;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityContentsListBinding;

public class ContentsListActivity extends BaseActivity {
    private ActivityContentsListBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContentsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.title_contents), true);
        // لاحقًا: Recycler + تحقق u-scope
    }
}
