package com.eyadalalimi.students.ui.activity.contents;

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
import com.eyadalalimi.students.model.Content;
import com.eyadalalimi.students.repo.ContentsRepository;
import com.eyadalalimi.students.response.ListResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ContentsListActivity extends BaseActivity implements ContentsAdapter.OnItemClick {

    private SwipeRefreshLayout refresh;
    private RecyclerView rv;
    private ProgressBar progress;
    private ContentsAdapter adapter;
    private ChipGroup chips;
    private Chip chipMaterial, chipType;
    private TextView tvEmpty;

    private final List<Content> items = new ArrayList<>();
    private String nextCursor = null;
    private boolean loading = false;

    private ContentsRepository repo;
    private FilterPrefs prefs;

    // فلاتر
    private Long filterMaterialId = null;
    private String filterType = null; // "file"|"video"|"link"

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents_list);
        setupToolbar(findViewById(R.id.toolbar), getString(R.string.title_contents), true);

        refresh  = findViewById(R.id.swipe);
        rv       = findViewById(R.id.recycler);
        progress = findViewById(R.id.progress);
        chips    = findViewById(R.id.chips);
        chipMaterial = findViewById(R.id.chipMaterial);
        chipType     = findViewById(R.id.chipType);
        tvEmpty  = findViewById(R.id.tvEmpty);

        adapter = new ContentsAdapter(items, this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        repo = new ContentsRepository(this);
        prefs = new FilterPrefs(this);

        long extraMid = getIntent().getLongExtra("extra_material_id", -1);
        if (extraMid > 0) {
            filterMaterialId = extraMid;
        } else {
            filterMaterialId = prefs.getContentsMaterialId();
        }
        filterType = prefs.getContentsType();

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
        getMenuInflater().inflate(R.menu.menu_contents, menu);
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
        View v = getLayoutInflater().inflate(R.layout.bottomsheet_contents_filter, null, false);
        d.setContentView(v);

        TextInputEditText etMaterial = v.findViewById(R.id.etMaterialId);
        if (filterMaterialId != null && filterMaterialId > 0) {
            etMaterial.setText(String.valueOf(filterMaterialId));
        }

        if (filterType == null) v.findViewById(R.id.rbTypeNone).performClick();
        else {
            if ("file".equals(filterType)) v.findViewById(R.id.rbTypeFile).performClick();
            else if ("video".equals(filterType)) v.findViewById(R.id.rbTypeVideo).performClick();
            else if ("link".equals(filterType)) v.findViewById(R.id.rbTypeLink).performClick();
            else v.findViewById(R.id.rbTypeNone).performClick();
        }

        v.findViewById(R.id.btnApply).setOnClickListener(btn -> {
            Long newMaterialId = null;
            String txt = etMaterial.getText()!=null ? etMaterial.getText().toString().trim() : "";
            if (!TextUtils.isEmpty(txt)) {
                try { newMaterialId = Long.parseLong(txt); } catch (NumberFormatException ignored) {}
            }

            String newType = null;
            if (((android.widget.RadioButton)v.findViewById(R.id.rbTypeFile)).isChecked()) newType = "file";
            else if (((android.widget.RadioButton)v.findViewById(R.id.rbTypeVideo)).isChecked()) newType = "video";
            else if (((android.widget.RadioButton)v.findViewById(R.id.rbTypeLink)).isChecked()) newType = "link";

            filterMaterialId = newMaterialId;
            filterType       = newType;

            prefs.saveContents(filterMaterialId, filterType);

            bindChips();
            reload();
            d.dismiss();
        });

        v.findViewById(R.id.btnClear).setOnClickListener(btn -> {
            filterMaterialId = null;
            filterType       = null;
            prefs.clearContents();
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
                prefs.saveContents(null, filterType);
                reload();
                bindChips();
            });
        } else {
            chipMaterial.setVisibility(View.GONE);
        }

        if (filterType != null && !filterType.isEmpty()) {
            chipType.setVisibility(View.VISIBLE);
            chipType.setText(getString(R.string.filter_by_type, filterType));
            chipType.setOnCloseIconClickListener(v -> {
                filterType = null;
                prefs.saveContents(filterMaterialId, null);
                reload();
                bindChips();
            });
        } else {
            chipType.setVisibility(View.GONE);
        }

        chips.setVisibility(
                (chipMaterial.getVisibility()==View.VISIBLE || chipType.getVisibility()==View.VISIBLE)
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
        repo.list(20, cursor, filterMaterialId, filterType, new ContentsRepository.ApiCallback<ListResponse<Content>>() {
            @Override public void onSuccess(ListResponse<Content> data) {
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
                Toast.makeText(ContentsListActivity.this, msg != null ? msg : "خطأ الشبكة", Toast.LENGTH_SHORT).show();
                if (items.isEmpty()) tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(Content c) {
        ContentDetailsActivity.open(this, c.id, c.title);
    }
}
