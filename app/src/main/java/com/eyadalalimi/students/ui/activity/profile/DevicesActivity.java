package com.eyadalalimi.students.ui.activity.profile;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityDevicesBinding;

public class DevicesActivity extends BaseActivity {
    private ActivityDevicesBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDevicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.title_devices), true);
        // لاحقًا: Recycler للجلسات مع إزالة
    }
}
