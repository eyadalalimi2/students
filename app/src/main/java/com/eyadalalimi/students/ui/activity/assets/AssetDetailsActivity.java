package com.eyadalalimi.students.ui.activity.assets;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.eyadalalimi.students.core.network.ApiClient;
import com.eyadalalimi.students.core.network.ApiService;
import com.eyadalalimi.students.model.Asset;
import com.eyadalalimi.students.model.Media;
import com.eyadalalimi.students.response.ApiResponse;
import com.eyadalalimi.students.ui.activity.webview.DocumentViewerActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetDetailsActivity extends AppCompatActivity {

    public static void open(Context ctx, long id, String title) {
        Intent i = new Intent(ctx, AssetDetailsActivity.class);
        i.putExtra("id", id);
        i.putExtra("title", title);
        ctx.startActivity(i);
    }

    private static final String BASE_FILES = "https://obdcodehub.com/";

    private ApiService api;
    private long assetId;

    private ProgressBar progress;
    private TextView tvTitle, tvMeta, tvDesc, tvCat;
    private Button btnOpenFile, btnOpenLink, btnOpenVideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = ApiClient.get(this);

        // واجهة برمجية بسيطة
        ScrollView root = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        box.setPadding(pad, pad, pad, pad);

        progress = new ProgressBar(this);
        progress.setIndeterminate(true);

        tvTitle = new TextView(this);
        tvTitle.setTextSize(20f);
        tvTitle.setPadding(0, pad, 0, pad / 2);

        tvCat = new TextView(this);
        tvMeta = new TextView(this);
        tvDesc = new TextView(this);

        btnOpenFile = new Button(this);
        btnOpenFile.setText("فتح الملف");
        btnOpenLink = new Button(this);
        btnOpenLink.setText("فتح الرابط");
        btnOpenVideo = new Button(this);
        btnOpenVideo.setText("فتح الفيديو");

        box.addView(progress);
        box.addView(tvTitle);
        box.addView(tvCat);
        box.addView(tvMeta);
        box.addView(tvDesc);
        box.addView(btnOpenFile);
        box.addView(btnOpenLink);
        box.addView(btnOpenVideo);

        root.addView(box);
        setContentView(root);

        // استلام المعطيات
        assetId = getIntent().getLongExtra("id", 0L);
        String givenTitle = getIntent().getStringExtra("title");
        if (givenTitle != null) setTitle(givenTitle);

        // إخفاء الأزرار مبدئيًا
        btnOpenFile.setVisibility(View.GONE);
        btnOpenLink.setVisibility(View.GONE);
        btnOpenVideo.setVisibility(View.GONE);

        loadDetails();
    }

    private void loadDetails() {
        showLoading(true);
        api.asset(assetId).enqueue(new Callback<ApiResponse<Asset>>() {
            @Override
            public void onResponse(Call<ApiResponse<Asset>> call, Response<ApiResponse<Asset>> response) {
                showLoading(false);
                if (!response.isSuccessful() || response.body() == null || response.body().data == null) {
                    toast("فشل جلب تفاصيل الملف");
                    return;
                }
                bind(response.body().data);
            }

            @Override
            public void onFailure(Call<ApiResponse<Asset>> call, Throwable t) {
                showLoading(false);
                toast(t.getMessage());
            }
        });
    }

    private void bind(Asset a) {
        if (a.title != null) {
            tvTitle.setText(a.title);
            setTitle(a.title);
        } else {
            tvTitle.setText("ملف");
        }

        tvCat.setText("التصنيف: " + (a.category != null ? a.category : "غير محدد"));

        String meta = "الحالة: " + (a.status != null ? a.status : "-");
        if (a.published_at != null) meta += " · " + a.published_at;
        tvMeta.setText(meta);

        tvDesc.setText(a.description != null ? a.description : "لا يوجد وصف");

        Media m = a.media;
        final String fileUrl   = absoluteUrl(m != null ? m.file_path     : null);
        final String extUrl    = absoluteUrl(m != null ? m.external_url  : null);
        final String videoUrl  = absoluteUrl(m != null ? m.video_url     : null);

        if (fileUrl != null) {
            btnOpenFile.setVisibility(View.VISIBLE);
            btnOpenFile.setOnClickListener(v -> openFile(fileUrl, a.title));
        }
        if (extUrl != null) {
            btnOpenLink.setVisibility(View.VISIBLE);
            btnOpenLink.setOnClickListener(v -> openExternal(extUrl));
        }
        if (videoUrl != null) {
            btnOpenVideo.setVisibility(View.VISIBLE);
            btnOpenVideo.setOnClickListener(v -> openExternal(videoUrl));
        }
    }

    private void openFile(String url, String title) {
        try {
            Intent i = new Intent(this, DocumentViewerActivity.class);
            i.putExtra("title", title != null ? title : "مستند");
            i.putExtra("url", url);
            startActivity(i);
        } catch (Throwable ignore) {
            openExternal(url);
        }
    }

    private void openExternal(String url) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            toast("لا يمكن فتح الرابط");
        }
    }

    private String absoluteUrl(String maybeRelative) {
        if (maybeRelative == null || maybeRelative.isEmpty()) return null;
        String u = maybeRelative.trim();
        if (u.startsWith("http://") || u.startsWith("https://")) return u;
        if (u.startsWith("/")) return BASE_FILES + u.substring(1);
        return BASE_FILES + u;
    }

    private void showLoading(boolean show) {
        if (progress != null) progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void toast(String m) {
        Toast.makeText(this, m != null ? m : "حدث خطأ غير متوقع", Toast.LENGTH_SHORT).show();
    }
}
