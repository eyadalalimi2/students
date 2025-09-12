package com.eyadalalimi.students.ui.activity.webview;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class DocumentViewerActivity extends BaseActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_URL   = "extra_url"; // قد تكون نسبية مثل contents/file.pdf

    private TextView tvName, tvUrl, tvHint;
    private Button btnDownload;
    private long currentDownloadId = -1L;
    private DownloadManager dm;

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id != currentDownloadId) return;
            openDownloadedFile(id);
        }
    };

    private final ActivityResultLauncher<String> permLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) startDownload();
                else Toast.makeText(this, "صلاحية التخزين مطلوبة للتنزيل على إصدارات أقدم", Toast.LENGTH_LONG).show();
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_viewer);

        setupToolbar(findViewById(R.id.toolbar), getString(R.string.doc_viewer_title), true);

        tvName = findViewById(R.id.tvName);
        tvUrl  = findViewById(R.id.tvUrl);
        tvHint = findViewById(R.id.tvHint);
        btnDownload = findViewById(R.id.btnDownload);

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String url   = normalizeUrl(getIntent().getStringExtra(EXTRA_URL));

        tvName.setText(!TextUtils.isEmpty(title) ? title : getString(R.string.doc_unknown));
        tvUrl.setText(url != null ? url : "-");

        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        btnDownload.setOnClickListener(v -> {
            if (requiresLegacyStoragePermission()) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    permLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    return;
                }
            }
            startDownload();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try { unregisterReceiver(onDownloadComplete); } catch (Throwable ignore) {}
    }

    private boolean requiresLegacyStoragePermission() {
        // WRITE_EXTERNAL_STORAGE مطلوب حتى Android 9 (API 28) عند استخدام setDestinationInExternalPublicDir
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.P;
    }

    private String normalizeUrl(String raw) {
        if (raw == null) return null;
        String r = raw.trim();
        if (r.startsWith("http://") || r.startsWith("https://")) return r;
        // مسار نسبي من الـ API → نُضيف الجذر
        return "https://obdcodehub.com/" + (r.startsWith("/") ? r.substring(1) : r);
    }

    private void startDownload() {
        String url = tvUrl.getText().toString();
        if (TextUtils.isEmpty(url) || url.equals("-")) {
            Toast.makeText(this, "رابط غير صالح", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Uri uri = Uri.parse(url);
            String filename = fileNameFromUrl(url);
            String mime = guessMimeByExtension(filename);

            DownloadManager.Request req = new DownloadManager.Request(uri);
            req.setAllowedOverMetered(true);
            req.setAllowedOverRoaming(true);
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            req.setTitle(filename);
            if (mime != null) req.setMimeType(mime);

            // مجلد التنزيلات العام
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

            currentDownloadId = dm.enqueue(req);
            Toast.makeText(this, "بدأ التنزيل…", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "فشل بدء التنزيل", Toast.LENGTH_LONG).show();
        }
    }

    private void openDownloadedFile(long downloadId) {
        try {
            DownloadManager.Query q = new DownloadManager.Query().setFilterById(downloadId);
            Cursor c = dm.query(q);
            if (c == null) { Toast.makeText(this, "تعذّر فتح الملف", Toast.LENGTH_SHORT).show(); return; }
            try {
                if (!c.moveToFirst()) { Toast.makeText(this, "تعذّر فتح الملف", Toast.LENGTH_SHORT).show(); return; }
                int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                if (status != DownloadManager.STATUS_SUCCESSFUL) {
                    Toast.makeText(this, "فشل التنزيل", Toast.LENGTH_SHORT).show(); return;
                }
                String localUriStr = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                String mime = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE));
                Uri localUri = localUriStr != null ? Uri.parse(localUriStr) : null;
                if (localUri == null) { Toast.makeText(this, "ملف غير متاح", Toast.LENGTH_SHORT).show(); return; }

                Intent open = new Intent(Intent.ACTION_VIEW);
                open.setDataAndType(localUri, !TextUtils.isEmpty(mime) ? mime : guessMimeByExtension(localUri.getLastPathSegment()));
                open.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

                try {
                    startActivity(open);
                } catch (ActivityNotFoundException e) {
                    // محاولة عامة دون type
                    Intent generic = new Intent(Intent.ACTION_VIEW);
                    generic.setData(localUri);
                    generic.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(generic);
                }
            } finally {
                c.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "لا يوجد تطبيق مناسب لفتح الملف", Toast.LENGTH_LONG).show();
        }
    }

    private String fileNameFromUrl(String url) {
        try {
            String path = Uri.parse(url).getPath();
            if (path == null) return "download";
            String name = new File(path).getName();
            name = URLDecoder.decode(name, StandardCharsets.UTF_8.name());
            if (TextUtils.isEmpty(name)) return "download";
            return name;
        } catch (Exception e) {
            return "download";
        }
    }

    private String guessMimeByExtension(String name) {
        if (name == null) return null;
        String lower = name.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".mp4")) return "video/mp4";
        if (lower.endsWith(".doc") || lower.endsWith(".docx")) return "application/msword";
        if (lower.endsWith(".ppt") || lower.endsWith(".pptx")) return "application/vnd.ms-powerpoint";
        if (lower.endsWith(".xls") || lower.endsWith(".xlsx")) return "application/vnd.ms-excel";
        if (lower.endsWith(".txt")) return "text/plain";
        if (lower.endsWith(".zip")) return "application/zip";
        return null;
    }
}
