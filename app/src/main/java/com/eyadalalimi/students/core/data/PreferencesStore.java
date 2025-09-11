package com.eyadalalimi.students.core.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesStore {

    private static final String PREFS = "students_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_EMAIL_VERIFIED = "email_verified";
    private static final String KEY_ACTIVATED = "activated";

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

    public void clearAll() { sp.edit().clear().apply(); }
}
