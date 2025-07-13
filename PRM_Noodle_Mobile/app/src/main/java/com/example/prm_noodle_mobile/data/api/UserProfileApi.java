package com.example.prm_noodle_mobile.data.api;
import com.example.prm_noodle_mobile.data.model.UserProfile;

import retrofit2.Call;
import retrofit2.http.GET;
public interface UserProfileApi {
    @GET("/api/Auth/profile")
    Call<UserProfile> getProfile();
}