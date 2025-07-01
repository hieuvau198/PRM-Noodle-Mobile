package com.example.prm_noodle_mobile.data.api;

import com.example.prm_noodle_mobile.data.model.Combo;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ComboApi {
    @GET("/api/Combos/available")
    Call<List<Combo>> getAvailableCombos();

    @GET("/api/Combos/{id}")
    Call<Combo> getComboById(@Path("id") int id);
} 