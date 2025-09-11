package com.eyadalalimi.students.ui.activity.profile;

import android.os.Bundle;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityChangePasswordBinding;

public class ChangePasswordActivity extends BaseActivity {
    private ActivityChangePasswordBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.title_change_password), true);

        binding.btnChange.setOnClickListener(v ->
                Toast.makeText(this, "تم التغيير (تجريبي)", Toast.LENGTH_SHORT).show());
    }
}
