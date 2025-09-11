package com.eyadalalimi.students.core.network;

import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {

    public interface TokenRefresher {
        @Nullable String refresh() throws IOException;
    }

    private final TokenRefresher refresher;

    public TokenAuthenticator(TokenRefresher refresher) {
        this.refresher = refresher;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (response.request().header("Authorization") == null) return null;
        if (refresher == null) return null;
        String newToken = refresher.refresh();
        if (newToken == null || newToken.isEmpty()) return null;
        return response.request().newBuilder()
                .header("Authorization", "Bearer " + newToken)
                .build();
    }
}
