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
    Call<com.example.prm_noodle_mobile.data.model.OrderCreateResponse> createOrder(@Body Order order); // trả về orderId

    @POST("/api/Order/{orderId}/items")
    Call<Void> addOrderItems(@Path("orderId") int orderId, @Body java.util.List<com.example.prm_noodle_mobile.data.model.OrderItem> items);

    @POST("/api/Order/{orderId}/combos")
    Call<Void> addOrderCombos(@Path("orderId") int orderId, @Body java.util.List<com.example.prm_noodle_mobile.data.model.OrderCombo> combos);

    @GET("/api/Order/user/{userId}")
    Call<Object> getOrdersByUser(
        @Path("userId") int userId,
        @Query("page") int page,
        @Query("pageSize") int pageSize
    );
} 