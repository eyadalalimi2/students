package com.eyadalalimi.students.ui.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.core.data.SessionManager;
import com.eyadalalimi.students.databinding.ActivityActivationBinding;
import com.eyadalalimi.students.ui.activity.home.HomeActivity;

public class ActivationActivity extends BaseActivity {
    private ActivityActivationBinding binding;
    private SessionManager session;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActivationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        session = new SessionManager(this);
        setupToolbar(binding.toolbar, getString(R.string.auth_activation), true);

        binding.btnActivate.setOnClickListener(v -> {
            String code = binding.etCode.getText() == null ? "" : binding.etCode.getText().toString().trim();
            if (code.isEmpty()) { Toast.makeText(this, getString(R.string.msg_enter_activation), Toast.LENGTH_SHORT).show(); return; }
            session.markActivated();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }
    @Override
    protected boolean gateRequireLogin() { return true; }
    @Override
    protected boolean gateRequireVerified() { return true; }
    @Override
    protected boolean gateRequireActivated() { return false; }

}
