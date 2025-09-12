package com.eyadalalimi.students.ui.activity.contents;

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
import com.eyadalalimi.students.model.Content;
import com.eyadalalimi.students.model.Media;
import com.eyadalalimi.students.response.ApiResponse;
import com.eyadalalimi.students.ui.activity.webview.DocumentViewerActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentDetailsActivity extends AppCompatActivity {

    public static void open(Context ctx, long id, String title) {
        Intent i = new Intent(ctx, ContentDetailsActivity.class);
        i.putExtra("id", id);
        i.putExtra("title", title);
        ctx.startActivity(i);
    }

    // يمكنك ضبطه إذا تغيّر الدومين لديك
    private static final String BASE_FILES = "https://obdcodehub.com/";

    private ApiService api;
    private long contentId;

    private ProgressBar progress;
    private TextView tvTitle, tvMeta, tvDesc, tvType;
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

        tvMeta = new TextView(this);
        tvDesc = new TextView(this);
        tvType = new TextView(this);

        btnOpenFile = new Button(this);
        btnOpenFile.setText("فتح الملف");
        btnOpenLink = new Button(this);
        btnOpenLink.setText("فتح الرابط");
        btnOpenVideo = new Button(this);
        btnOpenVideo.setText("فتح الفيديو");

        box.addView(progress);
        box.addView(tvTitle);
        box.addView(tvType);
        box.addView(tvMeta);
        box.addView(tvDesc);
        box.addView(btnOpenFile);
        box.addView(btnOpenLink);
        box.addView(btnOpenVideo);

        root.addView(box);
        setContentView(root);

        // استلام المعطيات
        contentId = getIntent().getLongExtra("id", 0L);
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
        api.content(contentId).enqueue(new Callback<ApiResponse<Content>>() {
            @Override
            public void onResponse(Call<ApiResponse<Content>> call, Response<ApiResponse<Content>> response) {
                showLoading(false);
                if (!response.isSuccessful() || response.body() == null || response.body().data == null) {
                    toast("فشل جلب تفاصيل المحتوى");
                    return;
                }
                bind(response.body().data);
            }

            @Override
            public void onFailure(Call<ApiResponse<Content>> call, Throwable t) {
                showLoading(false);
                toast(t.getMessage());
            }
        });
    }

    private void bind(Content c) {
        if (c.title != null) {
            tvTitle.setText(c.title);
            setTitle(c.title);
        } else {
            tvTitle.setText("محتوى");
        }

        tvType.setText("النوع: " + (c.type != null ? c.type : "غير محدد"));
        String meta = "الحالة: " + (c.status != null ? c.status : "-");
        if (c.published_at != null) meta += " · " + c.published_at;
        tvMeta.setText(meta);

        tvDesc.setText(c.description != null ? c.description : "لا يوجد وصف");

        Media m = c.media;
        final String fileUrl   = absoluteUrl(m != null ? m.file_path   : null);
        final String linkUrl   = absoluteUrl(m != null ? m.source_url  : null); // contents: source_url
        final String videoUrl  = absoluteUrl(m != null ? m.video_url   : null);

        if (fileUrl != null) {
            btnOpenFile.setVisibility(View.VISIBLE);
            btnOpenFile.setOnClickListener(v -> openFile(fileUrl, c.title));
        }
        if (linkUrl != null) {
            btnOpenLink.setVisibility(View.VISIBLE);
            btnOpenLink.setOnClickListener(v -> openExternal(linkUrl));
        }
        if (videoUrl != null) {
            btnOpenVideo.setVisibility(View.VISIBLE);
            btnOpenVideo.setOnClickListener(v -> openExternal(videoUrl));
        }
    }

    private void openFile(String url, String title) {
        // تفضيل عارض المستندات الداخلي إن وُجد، وإلا فتح خارجي
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
