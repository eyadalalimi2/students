package com.eyadalalimi.students.core.network;

import androidx.annotation.Nullable;

import com.eyadalalimi.students.core.data.PreferencesStore;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

class TokenAuthenticator implements Authenticator {
    private final PreferencesStore store;

    TokenAuthenticator(PreferencesStore store) {
        this.store = store;
    }

    @Nullable
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        // لا إعادة محاولة تلقائية. يمكن إضافة refresh لاحقًا.
        return null;
    }
}
