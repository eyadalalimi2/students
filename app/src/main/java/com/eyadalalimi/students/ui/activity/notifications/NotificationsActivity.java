package com.eyadalalimi.students.ui.activity.notifications;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityNotificationsBinding;

public class NotificationsActivity extends BaseActivity {
    private ActivityNotificationsBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.title_notifications), true);
        // لاحقًا: Room + قائمة محلية
    }
}
