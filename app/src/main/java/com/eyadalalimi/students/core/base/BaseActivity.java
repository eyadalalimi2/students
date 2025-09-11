package com.eyadalalimi.students.core.base;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity {

    protected void setupToolbar(Toolbar toolbar, String title, boolean showBack) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
            if (showBack) {
                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            }
        }
    }
}
