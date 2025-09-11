package com.eyadalalimi.students.core.data;

import android.content.Context;
import android.text.TextUtils;

/**
 * يطبّق مصفوفة الوصول حسب ADR:
 * - Registered (token null) → Login فقط
 * - EmailVerified=false → VerifyEmail فقط
 * - Activated=false → Activation فقط
 * - Activated=true → Home
 */
public class SessionManager {

    private final PreferencesStore store;

    public SessionManager(Context ctx) {
        this.store = new PreferencesStore(ctx.getApplicationContext());
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(store.getToken());
    }

    public void loginWithToken(String token) {
        store.setToken(token);
        // بعد تسجيل الدخول، البريد غير موثّق افتراضيًا حتى التحقق الفعلي
        store.setEmailVerified(false);
        store.setActivated(false);
    }

    public void markEmailVerified() {
        store.setEmailVerified(true);
    }

    public boolean isEmailVerified() {
        return store.isEmailVerified();
    }

    public void markActivated() {
        store.setActivated(true);
    }

    public boolean isActivated() {
        return store.isActivated();
    }

    public void logout() {
        store.clearAll();
    }
}
