package com.example.prm_noodle_mobile.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;
import android.content.Context;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String token = new com.example.prm_noodle_mobile.utils.UserSessionManager(context).getToken();
                    okhttp3.Request original = chain.request();
                    okhttp3.Request.Builder builder = original.newBuilder();
                    if (token != null && !token.isEmpty()) {
                        builder.header("Authorization", "Bearer " + token);
                    }
                    okhttp3.Request request = builder.build();
                    return chain.proceed(request);
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
            retrofit = new Retrofit.Builder()
                .baseUrl("https://prmnoodle.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        }
        return retrofit;
    }
} 