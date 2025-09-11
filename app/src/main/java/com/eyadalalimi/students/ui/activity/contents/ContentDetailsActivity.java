package com.eyadalalimi.students.ui.activity.contents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityContentDetailsBinding;

public class ContentDetailsActivity extends BaseActivity {
    private ActivityContentDetailsBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.title_content_details), true);

        binding.btnOpen.setOnClickListener(v -> {
            String demo = "https://www.example.com/private.pdf";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(demo)));
        });
    }
}
