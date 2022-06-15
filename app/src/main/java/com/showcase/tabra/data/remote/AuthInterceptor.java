package com.showcase.tabra.data.remote;

import android.content.Context;
import com.showcase.tabra.utils.Util;
import okhttp3.*;

import java.io.IOException;

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
