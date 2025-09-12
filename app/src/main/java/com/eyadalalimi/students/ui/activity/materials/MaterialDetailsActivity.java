package com.eyadalalimi.students.ui.activity.materials;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.model.Material;
import com.eyadalalimi.students.repo.CatalogRepository;
import com.eyadalalimi.students.response.ApiResponse;
import com.eyadalalimi.students.ui.activity.assets.AssetsListActivity;
import com.eyadalalimi.students.ui.activity.contents.ContentsListActivity;

public class MaterialDetailsActivity extends BaseActivity {

    private static final String EXTRA_ID = "extra_id";
    private static final String EXTRA_NAME = "extra_name";

    public static void open(Context ctx, long id, String name) {
        Intent i = new Intent(ctx, MaterialDetailsActivity.class);
        i.putExtra(EXTRA_ID, id);
        i.putExtra(EXTRA_NAME, name);
        ctx.startActivity(i);
    }
    private android.view.View progressView; // اربطها في onCreate إذا أردت

    private void showLoading(boolean show) {
        if (progressView != null) {
            progressView.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
        }
    }

    private TextView tvName, tvMeta, tvScope;
    private Button btnAssets, btnContents;
    private CatalogRepository repo;
    private long materialId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_details);
        setupToolbar(findViewById(R.id.toolbar), getString(R.string.title_material_details), true);

        tvName = findViewById(R.id.tvName);
        tvMeta = findViewById(R.id.tvMeta);
        tvScope = findViewById(R.id.tvScope);
        btnAssets = findViewById(R.id.btnAssets);
        btnContents = findViewById(R.id.btnContents);

        materialId = getIntent().getLongExtra(EXTRA_ID, -1);
        String name = getIntent().getStringExtra(EXTRA_NAME);
        if (name != null) tvName.setText(name);

        repo = new CatalogRepository(this);
        fetch();

        btnAssets.setOnClickListener(v -> {
            Intent i = new Intent(this, AssetsListActivity.class);
            i.putExtra("extra_material_id", materialId); // سيُستخدم كفلتر
            startActivity(i);
        });

        btnContents.setOnClickListener(v -> {
            Intent i = new Intent(this, ContentsListActivity.class);
            i.putExtra("extra_material_id", materialId); // سيُستخدم كفلتر
            startActivity(i);
        });
    }

    private void fetch() {
        if (materialId <= 0) return;
        showLoading(true);
        repo.details(materialId, new CatalogRepository.ApiCallback<ApiResponse<Material>>() {
            @Override public void onSuccess(ApiResponse<Material> data) {
                showLoading(false);
                if (data != null && data.data != null) bind(data.data);
            }
            @Override public void onError(String msg) {
                showLoading(false);
                Toast.makeText(MaterialDetailsActivity.this, msg != null ? msg : "تعذّر جلب التفاصيل", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bind(Material m) {
        if (m.name != null) tvName.setText(m.name);
        tvScope.setText("النطاق: " + m.displayScope());
        String meta = "";
        if (m.level != null) meta += "المستوى: " + m.level;
        if (m.university_id != null) meta += (meta.isEmpty()? "" : " • ") + "جامعة#" + m.university_id;
        if (m.college_id != null)     meta += " / كلية#" + m.college_id;
        if (m.major_id != null)       meta += " / تخصص#" + m.major_id;
        tvMeta.setText(meta.isEmpty()? "-" : meta);
    }
}
