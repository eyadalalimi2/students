package com.eyadalalimi.students.ui.activity.assets;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.core.data.FilterPrefs;
import com.eyadalalimi.students.model.Asset;
import com.eyadalalimi.students.repo.AssetsRepository;
import com.eyadalalimi.students.response.ListResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AssetsListActivity extends BaseActivity implements AssetsAdapter.OnItemClick {

    private SwipeRefreshLayout refresh;
    private RecyclerView rv;
    private ProgressBar progress;
    private AssetsAdapter adapter;
    private ChipGroup chips;
    private Chip chipMaterial, chipCategory;
    private TextView tvEmpty;

    private final List<Asset> items = new ArrayList<>();
    private String nextCursor = null;
    private boolean loading = false;

    private AssetsRepository repo;
    private FilterPrefs prefs;

    // فلاتر
    private Long filterMaterialId = null;
    private String filterCategory = null; // "youtube"|"file"|"reference"|... اختياري

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets_list);
        setupToolbar(findViewById(R.id.toolbar), getString(R.string.title_assets), true);

        refresh  = findViewById(R.id.swipe);
        rv       = findViewById(R.id.recycler);
        progress = findViewById(R.id.progress);
        chips    = findViewById(R.id.chips);
        chipMaterial = findViewById(R.id.chipMaterial);
        chipCategory = findViewById(R.id.chipCategory);
        tvEmpty  = findViewById(R.id.tvEmpty);

        adapter = new AssetsAdapter(items, this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        repo = new AssetsRepository(this);
        prefs = new FilterPrefs(this);

        // قراءة material_id من الـ Intent
        long extraMid = getIntent().getLongExtra("extra_material_id", -1);
        if (extraMid > 0) {
            filterMaterialId = extraMid;
        } else {
            // حمّل آخر اختيار
            filterMaterialId = prefs.getAssetsMaterialId();
        }
        filterCategory = prefs.getAssetsCategory();

        bindChips();

        refresh.setOnRefreshListener(this::reload);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && !loading) {
                    LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                    if (lm == null) return;
                    int visible = lm.getChildCount();
                    int total = lm.getItemCount();
                    int first = lm.findFirstVisibleItemPosition();
                    if ((visible + first) >= total - 3) loadMore();
                }
            }
        });

        reload();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_assets, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            openFilterSheet();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFilterSheet() {
        BottomSheetDialog d = new BottomSheetDialog(this);
        View v = getLayoutInflater().inflate(R.layout.bottomsheet_assets_filter, null, false);
        d.setContentView(v);

        TextInputEditText etMaterial = v.findViewById(R.id.etMaterialId);
        if (filterMaterialId != null && filterMaterialId > 0) {
            etMaterial.setText(String.valueOf(filterMaterialId));
        }

        // اختيار الفئة الحالي
        int[] ids = new int[]{R.id.rbCatNone, R.id.rbCatFile, R.id.rbCatVideo, R.id.rbCatReference, R.id.rbCatCurriculum, R.id.rbCatQuestionBank, R.id.rbCatBook};
        if (filterCategory == null) v.findViewById(R.id.rbCatNone).performClick();
        else {
            if ("file".equals(filterCategory)) v.findViewById(R.id.rbCatFile).performClick();
            else if ("youtube".equals(filterCategory)) v.findViewById(R.id.rbCatVideo).performClick();
            else if ("reference".equals(filterCategory)) v.findViewById(R.id.rbCatReference).performClick();
            else if ("curriculum".equals(filterCategory)) v.findViewById(R.id.rbCatCurriculum).performClick();
            else if ("question_bank".equals(filterCategory)) v.findViewById(R.id.rbCatQuestionBank).performClick();
            else if ("book".equals(filterCategory)) v.findViewById(R.id.rbCatBook).performClick();
            else v.findViewById(R.id.rbCatNone).performClick();
        }

        v.findViewById(R.id.btnApply).setOnClickListener(btn -> {
            Long newMaterialId = null;
            String txt = etMaterial.getText()!=null ? etMaterial.getText().toString().trim() : "";
            if (!TextUtils.isEmpty(txt)) {
                try { newMaterialId = Long.parseLong(txt); } catch (NumberFormatException ignored) {}
            }

            String newCategory = null;
            if (v.findViewById(R.id.rbCatFile).isSelected() || ((android.widget.RadioButton)v.findViewById(R.id.rbCatFile)).isChecked()) newCategory = "file";
            else if (((android.widget.RadioButton)v.findViewById(R.id.rbCatVideo)).isChecked()) newCategory = "youtube";
            else if (((android.widget.RadioButton)v.findViewById(R.id.rbCatReference)).isChecked()) newCategory = "reference";
            else if (((android.widget.RadioButton)v.findViewById(R.id.rbCatCurriculum)).isChecked()) newCategory = "curriculum";
            else if (((android.widget.RadioButton)v.findViewById(R.id.rbCatQuestionBank)).isChecked()) newCategory = "question_bank";
            else if (((android.widget.RadioButton)v.findViewById(R.id.rbCatBook)).isChecked()) newCategory = "book";

            filterMaterialId = newMaterialId;
            filterCategory   = newCategory;

            // خزّن محليًا
            prefs.saveAssets(filterMaterialId, filterCategory);

            bindChips();
            reload();
            d.dismiss();
        });

        v.findViewById(R.id.btnClear).setOnClickListener(btn -> {
            filterMaterialId = null;
            filterCategory   = null;
            prefs.clearAssets();
            bindChips();
            reload();
            d.dismiss();
        });

        d.show();
    }

    private void bindChips() {
        if (filterMaterialId != null && filterMaterialId > 0) {
            chipMaterial.setVisibility(View.VISIBLE);
            chipMaterial.setText(getString(R.string.filter_by_material_id, String.valueOf(filterMaterialId)));
            chipMaterial.setOnCloseIconClickListener(v -> {
                filterMaterialId = null;
                prefs.saveAssets(null, filterCategory);
                reload();
                bindChips();
            });
        } else {
            chipMaterial.setVisibility(View.GONE);
        }

        if (filterCategory != null && !filterCategory.isEmpty()) {
            chipCategory.setVisibility(View.VISIBLE);
            chipCategory.setText(getString(R.string.filter_by_category, filterCategory));
            chipCategory.setOnCloseIconClickListener(v -> {
                filterCategory = null;
                prefs.saveAssets(filterMaterialId, null);
                reload();
                bindChips();
            });
        } else {
            chipCategory.setVisibility(View.GONE);
        }

        chips.setVisibility(
                (chipMaterial.getVisibility()==View.VISIBLE || chipCategory.getVisibility()==View.VISIBLE)
                        ? View.VISIBLE : View.GONE
        );
    }

    private void reload() {
        items.clear();
        adapter.notifyDataSetChanged();
        nextCursor = null;
        tvEmpty.setVisibility(View.GONE);
        fetchPage(null);
    }

    private void loadMore() {
        if (nextCursor == null) return;
        fetchPage(nextCursor);
    }

    private void fetchPage(@Nullable String cursor) {
        loading = true;
        if (cursor == null) progress.setVisibility(View.VISIBLE);
        repo.list(20, cursor, filterMaterialId, filterCategory, new AssetsRepository.ApiCallback<ListResponse<Asset>>() {
            @Override public void onSuccess(ListResponse<Asset> data) {
                progress.setVisibility(View.GONE);
                if (refresh.isRefreshing()) refresh.setRefreshing(false);
                loading = false;

                if (data != null && data.data != null) {
                    int start = items.size();
                    items.addAll(data.data);
                    adapter.notifyItemRangeInserted(start, data.data.size());
                }
                nextCursor = (data != null && data.meta != null) ? data.meta.next_cursor : null;
                if (items.isEmpty()) tvEmpty.setVisibility(View.VISIBLE);
            }

            @Override public void onError(String msg) {
                progress.setVisibility(View.GONE);
                if (refresh.isRefreshing()) refresh.setRefreshing(false);
                loading = false;
                Toast.makeText(AssetsListActivity.this, msg != null ? msg : "خطأ الشبكة", Toast.LENGTH_SHORT).show();
                if (items.isEmpty()) tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(Asset a) {
        AssetDetailsActivity.open(this, a.id, a.title);
    }
}
