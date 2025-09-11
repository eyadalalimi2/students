package com.eyadalalimi.students.ui.activity.contents;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivitySimpleListBinding;

public class ContentsListActivity extends BaseActivity {
    private ActivitySimpleListBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySimpleListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.app_name) + " - المحتوى الخاص", true);
        binding.tvInfo.setText("قائمة المحتوى الخاص (Contents) — ستتطلب u-scope ومصدر /contents");
    }
}
