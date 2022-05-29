package com.showcase.tabra.data.remote;

import android.content.Context;
import com.google.gson.GsonBuilder;
import com.showcase.tabra.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    public static String BASE_URL =BuildConfig.API_URL+"api/";
    private static Retrofit retrofit;

    public static Retrofit getClient(Context context){
        if(retrofit == null){
            final OkHttpClient client = new OkHttpClient
                    .Builder()
//                    .authenticator(new TokenAuthenticator())
                    .addInterceptor(new AuthInterceptor(context))
                    .build();


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
                    .client(client)
                    .build();
        }
        return retrofit;
    }

}

