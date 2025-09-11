package com.eyadalalimi.students.ui.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityHomeBinding;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.AuthRepository;
import com.eyadalalimi.students.response.MessageResponse;
import com.eyadalalimi.students.ui.activity.auth.LoginActivity;

public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding binding;
    private AuthRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo = new AuthRepository(this);
        setupToolbar(binding.toolbar, getString(R.string.app_name), false);

        // خروج مؤقت بالنقر على الجذر
        binding.getRoot().setOnClickListener(v -> {
            repo.logout(new ApiCallback<MessageResponse>() {
                @Override public void onSuccess(MessageResponse data) {
                    Toast.makeText(HomeActivity.this, "تم تسجيل الخروج", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
                @Override public void onError(String message) {
                    Toast.makeText(HomeActivity.this, "تعذر تسجيل الخروج: " + message, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            });
        });
    }
}
