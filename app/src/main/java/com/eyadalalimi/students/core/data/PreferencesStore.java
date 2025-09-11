package com.eyadalalimi.students.core.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesStore {
    private static final String PREFS = "students_prefs";

    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_EMAIL_VERIFIED = "email_verified";
    private static final String KEY_ACTIVATED = "activated";
    private static final String KEY_EMAIL = "user_email";

    private static final String KEY_ALLOWED_SOURCES = "allowed_sources"; // "assets,contents"
    private static final String KEY_SCOPE_U = "scope_university_id";
    private static final String KEY_SCOPE_C = "scope_college_id";
    private static final String KEY_SCOPE_M = "scope_major_id";

    private final SharedPreferences sp;

    public PreferencesStore(Context context) {
        this.sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void setToken(String token) { sp.edit().putString(KEY_TOKEN, token).apply(); }
    public String getToken() { return sp.getString(KEY_TOKEN, null); }
    public void clearToken() { sp.edit().remove(KEY_TOKEN).apply(); }

    public void setEmailVerified(boolean value) { sp.edit().putBoolean(KEY_EMAIL_VERIFIED, value).apply(); }
    public boolean isEmailVerified() { return sp.getBoolean(KEY_EMAIL_VERIFIED, false); }

    public void setActivated(boolean value) { sp.edit().putBoolean(KEY_ACTIVATED, value).apply(); }
    public boolean isActivated() { return sp.getBoolean(KEY_ACTIVATED, false); }

    public void setEmail(String email) { sp.edit().putString(KEY_EMAIL, email).apply(); }
    public String getEmail() { return sp.getString(KEY_EMAIL, null); }

    public void setAllowedSources(String value) { sp.edit().putString(KEY_ALLOWED_SOURCES, value).apply(); }
    public String getAllowedSources() { return sp.getString(KEY_ALLOWED_SOURCES, null); }

    public void setScope(Long u, Long c, Long m) {
        SharedPreferences.Editor e = sp.edit();
        if (u == null) e.remove(KEY_SCOPE_U); else e.putLong(KEY_SCOPE_U, u);
        if (c == null) e.remove(KEY_SCOPE_C); else e.putLong(KEY_SCOPE_C, c);
        if (m == null) e.remove(KEY_SCOPE_M); else e.putLong(KEY_SCOPE_M, m);
        e.apply();
    }

    public void clearAll() { sp.edit().clear().apply(); }
}
