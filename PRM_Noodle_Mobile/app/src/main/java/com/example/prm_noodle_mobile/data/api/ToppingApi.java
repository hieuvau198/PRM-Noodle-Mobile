package com.example.prm_noodle_mobile.data.api;

import com.example.prm_noodle_mobile.data.model.Topping;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ToppingApi {
    @GET("/api/Toppings/available")
    Call<List<Topping>> getAvailableToppings();
} 