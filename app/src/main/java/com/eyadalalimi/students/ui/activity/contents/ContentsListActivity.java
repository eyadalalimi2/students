package com.eyadalalimi.students.ui.activity.contents;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityPlaceholderBinding;

public class ContentsListActivity extends BaseActivity {
    private ActivityPlaceholderBinding binding;
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceholderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.contents_title), true);
        setupBottomBar(binding.bottomBar, R.id.nav_contents);
        binding.tvTitle.setText("قائمة المحتوى الخاص بالمؤسسة (Contents) — قريبًا");
    }
}
