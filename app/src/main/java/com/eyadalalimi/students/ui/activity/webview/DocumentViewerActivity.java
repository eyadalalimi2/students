package com.eyadalalimi.students.ui.activity.webview;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityDocumentViewerBinding;

public class DocumentViewerActivity extends BaseActivity {
    public static final String EXTRA_URL = "url";
    private ActivityDocumentViewerBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocumentViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar, getString(R.string.title_viewer), true);

        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url == null || url.isEmpty()) {
            android.widget.Toast.makeText(this, "لا يوجد رابط", android.widget.Toast.LENGTH_SHORT).show();
            finish(); return;
        }
        com.eyadalalimi.students.core.util.Browser.open(this, url);
        finish();
    }

}
