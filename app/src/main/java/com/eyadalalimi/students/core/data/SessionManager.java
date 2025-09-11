package com.eyadalalimi.students.core.data;

import android.content.Context;
import android.text.TextUtils;

/**
 * يطبّق مصفوفة الوصول حسب ADR:
 * - !isLoggedIn → Login فقط
 * - !isEmailVerified → VerifyEmail فقط
 * - !isActivated → Activation فقط
 * - Activated → Home
 */
public class SessionManager {

    private final PreferencesStore store;

    public SessionManager(Context ctx) {
        this.store = new PreferencesStore(ctx.getApplicationContext());
    }

    public boolean isLoggedIn() { return !TextUtils.isEmpty(store.getToken()); }
    public void loginWithToken(String token) { store.setToken(token); store.setEmailVerified(false); store.setActivated(false); }

    public boolean isEmailVerified() { return store.isEmailVerified(); }
    public void markEmailVerified() { store.setEmailVerified(true); }

    public boolean isActivated() { return store.isActivated(); }
    public void markActivated() { store.setActivated(true); }

    public void logout() { store.clearAll(); }
}
