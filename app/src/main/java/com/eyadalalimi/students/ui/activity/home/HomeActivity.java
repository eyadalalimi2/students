package com.eyadalalimi.students.ui.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityHomeBinding;
import com.eyadalalimi.students.model.VisibilityInfo;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.ProfileRepository;
import com.eyadalalimi.students.ui.activity.assets.AssetsListActivity;
import com.eyadalalimi.students.ui.activity.contents.ContentsListActivity;
import com.eyadalalimi.students.ui.activity.materials.MaterialsActivity;
import com.eyadalalimi.students.ui.activity.profile.ProfileActivity;

import java.util.List;

public class HomeActivity extends BaseActivity {

    private ActivityHomeBinding binding;
    private ProfileRepository profileRepo;
    private boolean allowContents = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar(binding.toolbar, getString(R.string.home_title), false);
        setupBottomBar(binding.bottomBar, R.id.nav_home);

        profileRepo = new ProfileRepository(this);

        // تنقل البطاقات
        binding.cardAssets.setOnClickListener(v -> startActivity(new Intent(this, AssetsListActivity.class)));
        binding.cardContents.setOnClickListener(v -> {
            if (!allowContents) {
                toast("هذا القسم يتطلب ربطًا مؤسسيًا (جامعة/كلية/تخصص).");
                return;
            }
            startActivity(new Intent(this, ContentsListActivity.class));
        });
        binding.cardMaterials.setOnClickListener(v -> startActivity(new Intent(this, MaterialsActivity.class)));
        binding.cardProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        // جلب الرؤية لتمكين/تعطيل المحتوى الخاص
        loadVisibility();
    }

    private void loadVisibility() {
        binding.banner.setVisibility(View.GONE);
        binding.progress.setVisibility(View.VISIBLE);

        profileRepo.getVisibility(new ApiCallback<VisibilityInfo>() {
            @Override public void onSuccess(VisibilityInfo data) {
                binding.progress.setVisibility(View.GONE);
                allowContents = hasSource(data != null ? data.allowedSources : null, "contents");

                binding.cardContents.setAlpha(allowContents ? 1f : 0.5f);

                boolean linked = data != null && data.linkedToUniversity;

                if (!linked) {
                    binding.banner.setText("أنت غير مرتبط مؤسسيًا. اربط (جامعة/كلية/تخصص) من ملفك الشخصي لرؤية المحتوى الخاص.");
                    binding.banner.setVisibility(View.VISIBLE);
                    binding.btnBannerAction.setVisibility(View.VISIBLE);
                    binding.btnBannerAction.setOnClickListener(v ->
                            startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));
                } else {
                    binding.banner.setVisibility(View.GONE);
                    binding.btnBannerAction.setVisibility(View.GONE);
                }
            }
            @Override public void onError(String message) {
                binding.progress.setVisibility(View.GONE);
                toast(message);
            }
        });
    }

    private boolean hasSource(List<String> list, String value) {
        if (list == null || value == null) return false;
        for (String s : list) if (value.equalsIgnoreCase(s)) return true;
        return false;
    }

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_LONG).show(); }
}
