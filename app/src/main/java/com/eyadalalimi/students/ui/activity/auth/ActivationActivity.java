package com.eyadalalimi.students.ui.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityActivationBinding;
import com.eyadalalimi.students.repo.ActivationRepository;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.response.SubscriptionResponse;
import com.eyadalalimi.students.ui.activity.home.HomeActivity;

public class ActivationActivity extends BaseActivity {
    private ActivityActivationBinding binding;
    private ActivationRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActivationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo = new ActivationRepository(this);
        setupToolbar(binding.toolbar, getString(R.string.auth_activation), true);

        binding.btnActivate.setOnClickListener(v -> {
            String code = binding.etCode.getText() != null ? binding.etCode.getText().toString().trim() : "";
            if (code.isEmpty()) {
                Toast.makeText(this, "أدخل كود التفعيل", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.btnActivate.setEnabled(false);
            repo.activate(code, new ApiCallback<SubscriptionResponse>() {
                @Override public void onSuccess(SubscriptionResponse data) {
                    Toast.makeText(ActivationActivity.this, "تم التفعيل.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ActivationActivity.this, HomeActivity.class));
                    finish();
                }
                @Override public void onError(String message) {
                    binding.btnActivate.setEnabled(true);
                    Toast.makeText(ActivationActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
