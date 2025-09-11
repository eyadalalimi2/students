package com.eyadalalimi.students.core.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.data.SessionManager;
import com.eyadalalimi.students.databinding.LayoutBaseBinding;
import com.eyadalalimi.students.ui.activity.assets.AssetsListActivity;
import com.eyadalalimi.students.ui.activity.auth.ActivationActivity;
import com.eyadalalimi.students.ui.activity.auth.LoginActivity;
import com.eyadalalimi.students.ui.activity.auth.VerifyEmailActivity;
import com.eyadalalimi.students.ui.activity.contents.ContentsListActivity;
import com.eyadalalimi.students.ui.activity.home.HomeActivity;
import com.eyadalalimi.students.ui.activity.materials.MaterialsActivity;
import com.eyadalalimi.students.ui.activity.notifications.NotificationsActivity;
import com.eyadalalimi.students.ui.activity.profile.ProfileActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected LayoutBaseBinding baseBinding;
    protected SessionManager session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
        baseBinding = LayoutBaseBinding.inflate(getLayoutInflater());
        super.setContentView(baseBinding.getRoot());

        setSupportActionBar(baseBinding.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, baseBinding.drawer, baseBinding.toolbar,
                R.string.action_ok, R.string.action_cancel);
        baseBinding.drawer.addDrawerListener(toggle);
        toggle.syncState();

        baseBinding.navView.setNavigationItemSelectedListener(item -> {
            handleDrawer(item);
            return true;
        });

        baseBinding.bottomNav.setOnItemSelectedListener(item -> {
            handleBottom(item);
            return true;
        });
    }

    /** يحقن محتوى الشاشة داخل الحاوية الموحدة */
    protected void setBaseContent(@LayoutRes int layout, CharSequence title, Integer bottomItemIdOrNull) {
        ViewGroup container = baseBinding.contentContainer;
        container.removeAllViews();
        LayoutInflater.from(this).inflate(layout, container, true);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
        if (bottomItemIdOrNull != null) baseBinding.bottomNav.setSelectedItemId(bottomItemIdOrNull);
    }

    /** حواجز الوصول (ADR) */
    protected boolean gateRequireLogin() { return true; }
    protected boolean gateRequireVerified() { return true; }
    protected boolean gateRequireActivated() { return true; }

    @Override
    protected void onResume() {
        super.onResume();
        // ترتيب الحواجز: تسجيل → تحقق بريد → تفعيل
        if (gateRequireLogin() && !session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish(); return;
        }
        if (gateRequireVerified() && !session.isEmailVerified()) {
            if (!(this instanceof VerifyEmailActivity)) {
                startActivity(new Intent(this, VerifyEmailActivity.class));
                finish(); return;
            }
        }
        if (gateRequireActivated() && !session.isActivated()) {
            if (!(this instanceof ActivationActivity)) {
                startActivity(new Intent(this, ActivationActivity.class));
                finish(); return;
            }
        }
    }

    private void handleBottom(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            if (!(this instanceof HomeActivity)) startActivity(new Intent(this, HomeActivity.class));
        } else if (id == R.id.nav_assets) {
            if (!(this instanceof AssetsListActivity)) startActivity(new Intent(this, AssetsListActivity.class));
        } else if (id == R.id.nav_contents) {
            if (!(this instanceof ContentsListActivity)) startActivity(new Intent(this, ContentsListActivity.class));
        } else if (id == R.id.nav_materials) {
            if (!(this instanceof MaterialsActivity)) startActivity(new Intent(this, MaterialsActivity.class));
        } else if (id == R.id.nav_profile) {
            if (!(this instanceof ProfileActivity)) startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    private void handleDrawer(MenuItem item) {
        int id = item.getItemId();
        baseBinding.drawer.closeDrawers();
        if (id == R.id.menu_notifications) {
            if (!(this instanceof NotificationsActivity)) startActivity(new Intent(this, NotificationsActivity.class));
        } else if (id == R.id.menu_devices) {
            // Profile -> Devices
            try {
                Class<?> cls = Class.forName("com.eyadalalimi.students.ui.activity.profile.DevicesActivity");
                if (!cls.isInstance(this)) startActivity(new Intent(this, cls));
            } catch (Exception ignored) { }
        } else if (id == R.id.menu_logout) {
            session.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
