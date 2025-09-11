package com.eyadalalimi.students.core.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

class ConnectivityInterceptor implements Interceptor {
    private final Context appCtx;

    ConnectivityInterceptor(Context appCtx) {
        this.appCtx = appCtx.getApplicationContext();
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) appCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        Network n = cm.getActiveNetwork();
        if (n == null) return false;
        NetworkCapabilities nc = cm.getNetworkCapabilities(n);
        return nc != null && (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!isConnected()) {
            throw new IOException("NO_NETWORK");
        }
        return chain.proceed(chain.request());
    }
}
