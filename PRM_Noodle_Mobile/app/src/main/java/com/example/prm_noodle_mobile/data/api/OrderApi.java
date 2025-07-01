package com.example.prm_noodle_mobile.data.api;

import com.example.prm_noodle_mobile.data.model.Order;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderApi {
    @POST("/api/Order")
    Call<Void> createOrder(@Body Order order);

    @GET("/api/Order/user/{userId}")
    Call<Object> getOrdersByUser(
        @Path("userId") int userId,
        @Query("page") int page,
        @Query("pageSize") int pageSize
    );
} 