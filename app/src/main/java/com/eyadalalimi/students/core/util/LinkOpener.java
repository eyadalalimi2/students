package com.eyadalalimi.students.core.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

public final class LinkOpener {

    private LinkOpener() {}

    public static void openYoutube(Context ctx, String url) {
        if (url == null || url.trim().isEmpty()) return;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        i.setPackage("com.google.android.youtube");
        try {
            ctx.startActivity(i);
        } catch (ActivityNotFoundException e) {
            openCustomTab(ctx, url);
        }
    }

    public static void openCustomTab(Context ctx, String url) {
        if (url == null || url.trim().isEmpty()) return;
        CustomTabsIntent tabs = new CustomTabsIntent.Builder().build();
        tabs.launchUrl(ctx, Uri.parse(url));
    }

    public static void openFileExternal(Context ctx, String url) {
        if (url == null || url.trim().isEmpty()) return;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent chooser = Intent.createChooser(i, "فتح الملف عبر...");
        try {
            ctx.startActivity(chooser);
        } catch (ActivityNotFoundException e) {
            // fallback: افتح كرابط عام
            openCustomTab(ctx, url);
        }
    }

    // حوار تأكيد أول مرة للخروج (اختياري مبسّط – بدون UI هنا، مجرد “opt-in” بالحفظ)
    public static boolean ensureExternalConfirm(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean confirmed = sp.getBoolean("ext_confirmed", false);
        if (!confirmed) {
            sp.edit().putBoolean("ext_confirmed", true).apply();
            // في نسخة لاحقة: اعرض Dialog ثم خزّن الموافقة.
        }
        return true;
    }
}
