package com.example.prm_noodle_mobile.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
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