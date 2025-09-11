package com.eyadalalimi.students.ui.activity.contents;

import android.content.Intent;
import android.os.Bundle;
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
import com.eyadalalimi.students.core.util.LinkOpener;
import com.eyadalalimi.students.model.Content;
import com.eyadalalimi.students.model.VisibilityInfo;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.ContentsRepository;
import com.eyadalalimi.students.repo.VisibilityRepository;
import com.eyadalalimi.students.ui.activity.profile.ProfileActivity;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class ContentsListActivity extends BaseActivity {

    private SwipeRefreshLayout swipe;
    private RecyclerView rv;
    private ProgressBar progress;
    private TextView emptyView;
    private View gateView;
    private TextView gateMsg;

    private ContentsAdapter adapter;
    private ContentsRepository repo;
    private VisibilityRepository visRepo;

    private static final int PAGE_LIMIT = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, getString(R.string.contents_title), true);
        setupBottomBar(findViewById(R.id.bottomBar), R.id.nav_contents);

        swipe = findViewById(R.id.swipe);
        rv = findViewById(R.id.recycler);
        progress = findViewById(R.id.progress);
        emptyView = findViewById(R.id.emptyView);
        gateView = findViewById(R.id.gateView);
        gateMsg = findViewById(R.id.gateMsg);

        repo = new ContentsRepository(this);
        visRepo = new VisibilityRepository(this);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContentsAdapter(new ArrayList<>(), this::openContent);
        rv.setAdapter(adapter);

        swipe.setOnRefreshListener(this::reload);

        // تأكد من الأذونات (u-scope)
        checkVisibilityThenLoad();
    }

    private void checkVisibilityThenLoad() {
        progress.setVisibility(View.VISIBLE);
        gateView.setVisibility(View.GONE);
        visRepo.getVisibility(new com.eyadalalimi.students.repo.ApiCallback<VisibilityInfo>() {
            @Override public void onSuccess(VisibilityInfo v) {
                boolean allowed = v != null && v.canSeeContents();
                if (!allowed) {
                    progress.setVisibility(View.GONE);
                    showGate();
                } else {
                    reload();
                }
            }
            @Override public void onError(String msg) {
                progress.setVisibility(View.GONE);
                Toast.makeText(ContentsListActivity.this, msg, Toast.LENGTH_LONG).show();
                showGate(); // فشل الجلب: أظهر بوابة احترازياً
            }
        });
    }

    private void showGate() {
        gateView.setVisibility(View.VISIBLE);
        gateMsg.setText("لا يمكنك عرض المحتوى الخاص. اربط حسابك بالمؤسسة (جامعة/كلية/تخصص).");
        findViewById(R.id.btnGoProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class))
        );
    }

    private void reload() {
        emptyView.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        adapter.setItems(new ArrayList<>());

        repo.list(PAGE_LIMIT, null, new ApiCallback<List<Content>>() {
            @Override public void onSuccess(List<Content> data) {
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
                Toast.makeText(ContentsListActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openContent(Content c) {
        if (c == null) return;
        LinkOpener.ensureExternalConfirm(this);

        // video/link عبر source_url
        String src = c.sourceUrl();
        if (src != null && !src.isEmpty()) {
            if (c.isVideo() && LinkOpener.isYoutube(src)) {
                LinkOpener.openYoutube(this, src);
                return;
            }
            // أي رابط آخر (video أو link) نفتح Custom Tab
            LinkOpener.openCustomTab(this, src);
            return;
        }

        // file عبر file_path (قد يكون نسبيًا)
        String file = c.filePath();
        if (c.isFile() && file != null && !file.isEmpty()) {
            LinkOpener.openFileExternal(this, file);
            return;
        }

        Toast.makeText(this, "لا يوجد رابط صالح لهذا العنصر", Toast.LENGTH_SHORT).show();
    }

}
