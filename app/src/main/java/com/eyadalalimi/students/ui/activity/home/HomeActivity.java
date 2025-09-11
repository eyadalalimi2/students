package com.eyadalalimi.students.ui.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityHomeBinding;
import com.eyadalalimi.students.model.Visibility;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.VisibilityRepository;
import com.eyadalalimi.students.ui.activity.assets.AssetsListActivity;
import com.eyadalalimi.students.ui.activity.auth.LoginActivity;
import com.eyadalalimi.students.ui.activity.contents.ContentsListActivity;
import com.eyadalalimi.students.ui.activity.materials.MaterialsActivity;
import com.eyadalalimi.students.ui.activity.profile.ProfileActivity;

public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding binding;
    private VisibilityRepository visibilityRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        visibilityRepo = new VisibilityRepository(this);
        setupToolbar(binding.toolbar, getString(R.string.app_name), false);

        // Bottom Navigation
        binding.bottomNav.setOnItemSelectedListener(this::onBottomItem);

        // تلميح أولي ثم مزامنة الرؤية
        binding.tvHint.setText("جارِ مزامنة صلاحيات العرض...");
        visibilityRepo.fetch(new ApiCallback<Visibility>() {
            @Override public void onSuccess(Visibility data) {
                boolean a = visibilityRepo.canSeeAssets();
                boolean c = visibilityRepo.canSeeContents();
                String msg = "المتاح: " + (a ? "المحتوى العام" : "—")
                        + (c ? " + المحتوى الخاص" : "");
                binding.tvHint.setText(msg);
            }
            @Override public void onError(String message) {
                binding.tvHint.setText("تعذر المزامنة: " + message);
            }
        });

        // ضغط قصير لعرض رسالة، ضغط طويل للخروج المؤقت (للاختبار فقط)
        binding.tvHint.setOnLongClickListener(v -> {
            Toast.makeText(this, "تسجيل خروج تجريبي", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        });
    }

    private boolean onBottomItem(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_home) {
            // أنت في الرئيسية
            return true;
        } else if (id == R.id.menu_assets) {
            startActivity(new Intent(this, AssetsListActivity.class));
            return true;
        } else if (id == R.id.menu_contents) {
            if (!visibilityRepo.canSeeContents()) {
                Toast.makeText(this, "اربط مؤسستك لرؤية المحتوى الخاص", Toast.LENGTH_LONG).show();
                return false;
            }
            startActivity(new Intent(this, ContentsListActivity.class));
            return true;
        } else if (id == R.id.menu_materials) {
            startActivity(new Intent(this, MaterialsActivity.class));
            return true;
        } else if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return false;
    }
}
