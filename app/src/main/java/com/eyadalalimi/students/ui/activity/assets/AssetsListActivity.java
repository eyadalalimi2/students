package com.eyadalalimi.students.ui.activity.assets;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.core.util.LinkOpener;
import com.eyadalalimi.students.model.Asset;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.AssetsRepository;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class AssetsListActivity extends BaseActivity {

    private SwipeRefreshLayout swipe;
    private RecyclerView rv;
    private ProgressBar progress;
    private TextView emptyView;
    private Spinner spCategory;

    private AssetsAdapter adapter;
    private AssetsRepository repo;

    private String selectedCategory = null;
    private static final int PAGE_LIMIT = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, getString(R.string.assets_title), true);
        setupBottomBar(findViewById(R.id.bottomBar), R.id.nav_assets);

        swipe = findViewById(R.id.swipe);
        rv = findViewById(R.id.recycler);
        progress = findViewById(R.id.progress);
        emptyView = findViewById(R.id.emptyView);
        spCategory = findViewById(R.id.spCategory);

        repo = new AssetsRepository(this);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AssetsAdapter(new ArrayList<>(), this::openAsset);
        rv.setAdapter(adapter);

        // فئات مبدئية حسب البيانات الفعلية
        String[] cats = new String[]{"الكل", "book", "curriculum", "question_bank", "reference", "file", "youtube"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, cats);
        spCategory.setAdapter(catAdapter);
        spCategory.setSelection(0);
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String v = (String) parent.getItemAtPosition(position);
                selectedCategory = "الكل".equals(v) ? null : v;
                reload();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        swipe.setOnRefreshListener(this::reload);
        reload();
    }

    private void reload() {
        emptyView.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        adapter.setItems(new ArrayList<>());

        repo.list(PAGE_LIMIT, null, selectedCategory, null, new ApiCallback<List<Asset>>() {
            @Override public void onSuccess(List<Asset> data) {
                progress.setVisibility(View.GONE);
                swipe.setRefreshing(false);
                if (data == null || data.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    adapter.setItems(data);
                }
            }
            @Override public void onError(String message) {
                progress.setVisibility(View.GONE);
                swipe.setRefreshing(false);
                Toast.makeText(AssetsListActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openAsset(Asset a) {
        if (a == null) return;

        LinkOpener.ensureExternalConfirm(this);

        // 1) يوتيوب إن وُجد
        String youtube = a.videoUrl();
        if (youtube != null && !youtube.isEmpty()) {
            LinkOpener.openYoutube(this, youtube);
            return;
        }

        // 2) رابط خارجي
        String external = a.externalUrl();
        if (external != null && !external.isEmpty()) {
            LinkOpener.openCustomTab(this, external);
            return;
        }

        // 3) ملف
        String file = a.filePath();
        if (file != null && !file.isEmpty()) {
            LinkOpener.openFileExternal(this, file);
            return;
        }

        // 4) أصناف بدون روابط (book/curriculum/question_bank ...) حالياً قد لا تحوي media
        Toast.makeText(this, "لا يوجد رابط قابل للفتح لهذا العنصر", Toast.LENGTH_SHORT).show();
    }
}
