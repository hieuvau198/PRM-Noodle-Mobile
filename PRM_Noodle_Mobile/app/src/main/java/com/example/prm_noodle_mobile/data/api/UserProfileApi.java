package com.example.prm_noodle_mobile.data.api;
import com.example.prm_noodle_mobile.data.model.UserProfile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface UserProfileApi {
    @Headers({
        "Accept: application/json"
    })
    @GET("api/Auth/profile")
    Call<UserProfile> getProfile();
    
    // Full URL sẽ là: https://prmnoodle.azurewebsites.net/api/Auth/profile
}