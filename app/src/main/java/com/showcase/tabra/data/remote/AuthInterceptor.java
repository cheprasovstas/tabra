package com.showcase.tabra.data.remote;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.showcase.tabra.ui.login.LoginActivity;
import com.showcase.tabra.ui.product.ProductListActivity;
import com.showcase.tabra.utils.Util;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class AuthInterceptor implements Interceptor {
    private Context mContext;

    public AuthInterceptor(Context context) {

        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();

        if (Util.getToken(mContext) != null) {
            requestBuilder.addHeader("Authorization", String.format("Token %s", Util.getToken(mContext)));
        }
        return chain.proceed(requestBuilder.build());
    }
}
