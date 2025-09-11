package com.eyadalalimi.students.core.base;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.eyadalalimi.students.R;

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
    protected void setupBottomBar(com.google.android.material.bottomnavigation.BottomNavigationView bottomBar, int selectedId) {
        if (bottomBar == null) return;
        bottomBar.setSelectedItemId(selectedId);
        bottomBar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) return true;
            android.content.Intent i = null;
            if (id == R.id.nav_home) i = new android.content.Intent(this, com.eyadalalimi.students.ui.activity.home.HomeActivity.class);
            else if (id == R.id.nav_assets) i = new android.content.Intent(this, com.eyadalalimi.students.ui.activity.assets.AssetsListActivity.class);
            else if (id == R.id.nav_contents) i = new android.content.Intent(this, com.eyadalalimi.students.ui.activity.contents.ContentsListActivity.class);
            else if (id == R.id.nav_materials) i = new android.content.Intent(this, com.eyadalalimi.students.ui.activity.materials.MaterialsActivity.class);
            else if (id == R.id.nav_profile) i = new android.content.Intent(this, com.eyadalalimi.students.ui.activity.profile.ProfileActivity.class);
            if (i != null) {
                i.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            return false;
        });
    }

}
