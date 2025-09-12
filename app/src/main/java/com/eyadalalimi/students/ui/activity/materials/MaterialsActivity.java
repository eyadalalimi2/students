package com.eyadalalimi.students.ui.activity.materials;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.model.Material;
import com.eyadalalimi.students.repo.CatalogRepository;
import com.eyadalalimi.students.response.ListResponse;

import java.util.ArrayList;
import java.util.List;

public class MaterialsActivity extends BaseActivity implements MaterialsAdapter.OnItemClick {

    private SwipeRefreshLayout refresh;
    private RecyclerView rv;
    private ProgressBar progress;
    private MaterialsAdapter adapter;

    private final List<Material> items = new ArrayList<>();
    private String nextCursor = null;
    private boolean loading = false;

    private CatalogRepository repo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materials);
        setupToolbar(findViewById(R.id.toolbar), getString(R.string.title_materials), true);

        refresh = findViewById(R.id.swipe);
        rv = findViewById(R.id.recycler);
        progress = findViewById(R.id.progress);

        adapter = new MaterialsAdapter(items, this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        repo = new CatalogRepository(this);

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

    private void reload() {
        items.clear();
        adapter.notifyDataSetChanged();
        nextCursor = null;
        fetchPage(null);
    }

    private void loadMore() {
        if (nextCursor == null) return;
        fetchPage(nextCursor);
    }

    private void fetchPage(@Nullable String cursor) {
        loading = true;
        showProgress(cursor == null);
        repo.listMaterials(20, cursor, new CatalogRepository.ApiCallback<ListResponse<Material>>() {
            @Override public void onSuccess(ListResponse<Material> data) {
                hideProgress();
                if (refresh.isRefreshing()) refresh.setRefreshing(false);
                loading = false;

                if (data != null && data.data != null) {
                    int start = items.size();
                    items.addAll(data.data);
                    adapter.notifyItemRangeInserted(start, data.data.size());
                }
                nextCursor = (data != null && data.meta != null) ? data.meta.next_cursor : null;
            }

            @Override public void onError(String msg) {
                hideProgress();
                if (refresh.isRefreshing()) refresh.setRefreshing(false);
                loading = false;
                Toast.makeText(MaterialsActivity.this, msg != null ? msg : "خطأ الشبكة", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress(boolean firstPage) {
        if (firstPage) progress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onClick(Material m) {
        MaterialDetailsActivity.open(this, m.id, m.name);
    }
}
