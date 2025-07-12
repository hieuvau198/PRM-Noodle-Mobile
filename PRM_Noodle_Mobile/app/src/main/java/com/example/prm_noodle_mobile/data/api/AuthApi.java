package com.example.prm_noodle_mobile.data.api;

import com.example.prm_noodle_mobile.data.model.UserLoginRequest;
import com.example.prm_noodle_mobile.data.model.UserRegisterRequest;
import com.example.prm_noodle_mobile.data.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

// AuthApi.java
public interface AuthApi {
    @POST("/api/Auth/login")
    Call<UserResponse> login(@Body UserLoginRequest request);

    @POST("/api/Auth/register")
    Call<Void> register(@Body UserRegisterRequest request);
}