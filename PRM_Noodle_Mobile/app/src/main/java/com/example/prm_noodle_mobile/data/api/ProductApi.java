package com.example.prm_noodle_mobile.data.api;

import com.example.prm_noodle_mobile.data.model.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ProductApi {
    @GET("/api/Products")
    Call<List<Product>> getProducts();
} 