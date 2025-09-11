package com.eyadalalimi.students.ui.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.core.data.SessionManager;
import com.eyadalalimi.students.core.util.Validator;
import com.eyadalalimi.students.databinding.ActivityVerifyEmailBinding;

public class VerifyEmailActivity extends BaseActivity {
    private ActivityVerifyEmailBinding binding;
    private SessionManager session;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        session = new SessionManager(this);
        setupToolbar(binding.toolbar, getString(R.string.auth_verify_email), true);

        binding.btnVerify.setOnClickListener(v -> {
            String otp = binding.etOtp.getText() == null ? "" : binding.etOtp.getText().toString().trim();
            if (!Validator.isOtp(otp)) {
                Toast.makeText(this, getString(R.string.msg_invalid_otp), Toast.LENGTH_SHORT).show();
                return;
            }
            session.markEmailVerified();
            startActivity(new Intent(this, ActivationActivity.class));
            finish();
        });
    }
    @Override
    protected boolean gateRequireLogin() { return true; }
    @Override
    protected boolean gateRequireVerified() { return false; }
    @Override
    protected boolean gateRequireActivated() { return false; }

}
