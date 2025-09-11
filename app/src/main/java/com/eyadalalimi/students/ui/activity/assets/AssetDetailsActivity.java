package com.eyadalalimi.students.ui.activity.assets;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityAssetDetailsBinding;

public class AssetDetailsActivity extends BaseActivity {
    private ActivityAssetDetailsBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssetDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.title_asset_details), true);

        binding.btnOpen.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.msg_open_external), Toast.LENGTH_SHORT).show();
            String demo = "https://www.example.com";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(demo)));
        });
    }
}
