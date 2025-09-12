package com.eyadalalimi.students.ui.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.model.FeedItem;
import com.eyadalalimi.students.model.PagedResponse;
import com.eyadalalimi.students.model.User;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.AuthRepository;
import com.eyadalalimi.students.repo.FeedRepository;
import com.eyadalalimi.students.repo.ProfileRepository;
import com.eyadalalimi.students.ui.activity.auth.ActivationActivity;
import com.eyadalalimi.students.ui.activity.auth.VerifyEmailActivity;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity {

    private SwipeRefreshLayout swipe;
    private RecyclerView rv;
    private ProgressBar progress;
    private TextView emptyView;

    // بنرات الحالة
    private View bannerVerify;
    private Button btnResendLink, btnOpenVerify;
    private View bannerActivate;
    private Button btnGoActivate;

    private HomeFeedAdapter adapter;
    private FeedRepository feedRepo;
    private ProfileRepository profileRepo;

    private static final int PAGE_LIMIT = 20;
    private String nextCursor = null;
    private boolean isLoading = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, getString(R.string.app_name), false);
        setupBottomBar(findViewById(R.id.bottomBar), R.id.nav_home);

        swipe = findViewById(R.id.swipe);
        rv = findViewById(R.id.recycler);
        progress = findViewById(R.id.progress);
        emptyView = findViewById(R.id.emptyView);

        bannerVerify = findViewById(R.id.bannerVerify);
        btnResendLink = findViewById(R.id.btnResendVerify);
        btnOpenVerify = findViewById(R.id.btnOpenVerify);
        bannerActivate = findViewById(R.id.bannerActivate);
        btnGoActivate = findViewById(R.id.btnGoActivate);

        feedRepo = new FeedRepository(this);
        profileRepo = new ProfileRepository(this);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HomeFeedAdapter(new ArrayList<>(), this::openItem);
        rv.setAdapter(adapter);

        // سحب للتحديث
        swipe.setOnRefreshListener(this::fullReload);

        // تحميل أولي
        fullReload();

        // تحميل المزيد (تمرير لا نهائي بسيط)
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && !isLoading && nextCursor != null) {
                    LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                    if (lm != null && lm.findLastVisibleItemPosition() >= adapter.getItemCount() - 4) {
                        loadMore();
                    }
                }
            }
        });

        // أزرار البنرات
        btnOpenVerify.setOnClickListener(v -> startActivity(new Intent(this, VerifyEmailActivity.class)));
        btnResendLink.setOnClickListener(v -> resendVerifyLink());
        btnGoActivate.setOnClickListener(v -> startActivity(new Intent(this, ActivationActivity.class)));
    }

    private void fullReload() {
        // تحديث حالة المستخدم لإظهار البنرات الدقيقة
        profileRepo.getProfile(new ApiCallback<User>() {
            @Override public void onSuccess(User u) {
                updateBanners(u);
                // ثم الخلاصة
                reloadFeed();
            }
            @Override public void onError(String message) {
                // حتى لو فشل /me، نكمل تحميل الخلاصة
                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
                updateBanners(null);
                reloadFeed();
            }
        });
    }

    private void updateBanners(User u) {
        boolean needVerify = u != null && (u.email_verified_at == null || u.email_verified_at.trim().isEmpty());
        boolean needActivate = u != null && (u.has_active_subscription == null || !u.has_active_subscription);

        bannerVerify.setVisibility(needVerify ? View.VISIBLE : View.GONE);
        bannerActivate.setVisibility((!needVerify && needActivate) ? View.VISIBLE : View.GONE);
        // ملاحظة: حسب ADR، لا وصول لأي محتوى قبل التفعيل، لكننا نظهر الخلاصة فارغة مع رسائل.
    }

    private void reloadFeed() {
        emptyView.setVisibility(View.GONE);
        adapter.clear();
        nextCursor = null;
        progress.setVisibility(View.VISIBLE);
        isLoading = true;

        feedRepo.list(PAGE_LIMIT, null, new ApiCallback<PagedResponse<FeedItem>>() {
            @Override public void onSuccess(PagedResponse<FeedItem> page) {
                isLoading = false;
                progress.setVisibility(View.GONE);
                swipe.setRefreshing(false);

                if (page == null || page.data == null || page.data.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    adapter.addAll(page.data);
                    nextCursor = page.meta != null ? page.meta.next_cursor : null;
                }
            }
            @Override public void onError(String message) {
                isLoading = false;
                progress.setVisibility(View.GONE);
                swipe.setRefreshing(false);
                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadMore() {
        isLoading = true;
        feedRepo.list(PAGE_LIMIT, nextCursor, new ApiCallback<PagedResponse<FeedItem>>() {
            @Override public void onSuccess(PagedResponse<FeedItem> page) {
                isLoading = false;
                if (page != null && page.data != null && !page.data.isEmpty()) {
                    adapter.addAll(page.data);
                }
                nextCursor = (page != null && page.meta != null) ? page.meta.next_cursor : null;
            }
            @Override public void onError(String message) {
                isLoading = false;
                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openItem(FeedItem item) {
        if (item == null) return;

        // أولوية الروابط
        String directUrl = null;
        String filePath  = null;
        if (item.media != null) {
            if (item.media.file_path != null) filePath = item.media.file_path;
            if (item.media.video_url != null) directUrl = item.media.video_url;
            else if (item.media.external_url != null) directUrl = item.media.external_url;
            else if (item.media.source_url != null) directUrl = item.media.source_url;
        }

        // إن وُجد ملف ⇒ نستخدم DocumentViewerActivity
        if (filePath != null) {
            Intent i = new Intent(this, com.eyadalalimi.students.ui.activity.webview.DocumentViewerActivity.class);
            i.putExtra(com.eyadalalimi.students.ui.activity.webview.DocumentViewerActivity.EXTRA_TITLE,
                    item.title != null ? item.title : "ملف");
            i.putExtra(com.eyadalalimi.students.ui.activity.webview.DocumentViewerActivity.EXTRA_URL, filePath);
            startActivity(i);
            return;
        }

        // خلاف ذلك نفتح الرابط (Custom Tabs → ACTION_VIEW)
        String url = directUrl;
        if (url == null || url.trim().isEmpty()) {
            Toast.makeText(this, "لا يوجد رابط صالح للفتح", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            androidx.browser.customtabs.CustomTabsIntent intent = new androidx.browser.customtabs.CustomTabsIntent.Builder().build();
            intent.launchUrl(this, android.net.Uri.parse(url));
        } catch (Exception e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url)));
            } catch (Exception ex) {
                Toast.makeText(this, "تعذّر فتح الرابط", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resendVerifyLink() {
        // إعادة إرسال رابط التفعيل عبر البريد
        AuthRepository auth = new AuthRepository(this);
        String email = auth.getLastLoginEmail(); // إن لم تكن لديك هذه الدالة، استخدم البريد من SessionManager/Preferences
        if (email == null || email.trim().isEmpty()) {
            startActivity(new Intent(this, VerifyEmailActivity.class));
            return;
        }
        auth.resendEmail(email, new AuthRepository.ApiCallback<com.eyadalalimi.students.response.MessageResponse>() {
            @Override public void onSuccess(com.eyadalalimi.students.response.MessageResponse data) {
                Toast.makeText(HomeActivity.this, "تم إرسال رابط التفعيل إلى بريدك", Toast.LENGTH_LONG).show();
            }
            @Override public void onError(String message) {
                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
