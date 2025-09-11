package com.eyadalalimi.students.core.util;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;

import com.eyadalalimi.students.R;

public final class LinkOpener {

    private static boolean confirmedExternal = false;

    private LinkOpener() {}

    public static void ensureExternalConfirm(Context ctx) {
        if (confirmedExternal) return;
        new androidx.appcompat.app.AlertDialog.Builder(ctx)
                .setTitle(R.string.app_name)
                .setMessage("سيتم فتح الروابط/الملفات بتطبيقات خارجية.")
                .setPositiveButton("موافق", (d, w) -> confirmedExternal = true)
                .setNegativeButton("إلغاء", (DialogInterface d, int w) -> d.dismiss())
                .show();
    }

    public static void openCustomTab(Context ctx, String url) {
        if (url == null || url.isEmpty()) return;
        CustomTabsIntent tabs = new CustomTabsIntent.Builder().build();
        tabs.launchUrl(ctx, Uri.parse(url));
    }

    public static void openYoutube(Context ctx, String url) {
        if (url == null || url.isEmpty()) return;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        i.setPackage("com.google.android.youtube");
        try {
            ctx.startActivity(i);
        } catch (ActivityNotFoundException e) {
            openCustomTab(ctx, url);
        }
    }

    public static boolean isYoutube(String url) {
        if (url == null) return false;
        String u = url.toLowerCase();
        return u.contains("youtube.com") || u.contains("youtu.be");
    }

    public static String resolveUrl(@Nullable String maybeRelative) {
        if (maybeRelative == null || maybeRelative.isEmpty()) return null;
        if (maybeRelative.startsWith("http://") || maybeRelative.startsWith("https://")) {
            return maybeRelative;
        }
        // اضبط الأساس لو كان المسار نسبيًا
        // عدّل FILES_BASE إذا كان المسار الحقيقي مختلفًا (مثلاً /storage/..)
        return Constants.FILES_BASE + (maybeRelative.startsWith("/") ? maybeRelative.substring(1) : maybeRelative);
    }

    public static void openFileExternal(Context ctx, String pathOrUrl) {
        String url = resolveUrl(pathOrUrl);
        if (url == null) return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            ctx.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // استخدام DownloadManager كحل بديل
            try {
                DownloadManager dm = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Uri.parse(url).getLastPathSegment());
                dm.enqueue(req);
            } catch (Exception ignored) {}
        }
    }
}
