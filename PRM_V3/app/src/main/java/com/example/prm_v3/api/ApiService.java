package com.example.prm_v3.api;

import com.example.prm_v3.model.Order;
import com.example.prm_v3.model.OrderResponse;
import com.example.prm_v3.model.Product;
import com.example.prm_v3.model.Combo;
import com.example.prm_v3.model.Topping;
import com.example.prm_v3.model.Login;
import com.example.prm_v3.api.AuthResponse;
import com.example.prm_v3.model.User;
import com.example.prm_v3.model.Payment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.PATCH;

public interface ApiService {

    // ========== ORDER APIs ==========

    // Get all orders with pagination
    @GET("api/order")
    Call<OrderResponse> getOrders();

    @GET("api/order")
    Call<OrderResponse> getOrders(@Query("page") int page,
                                  @Query("pageSize") int pageSize);

    // Get orders by status (NEW - theo ảnh 1)
    @GET("api/order/pending")
    Call<OrderResponse> getPendingOrders(@Query("page") int page,
                                         @Query("pageSize") int pageSize);

    @GET("api/order/confirmed")
    Call<OrderResponse> getConfirmedOrders(@Query("page") int page,
                                           @Query("pageSize") int pageSize);

    @GET("api/order/preparing")
    Call<OrderResponse> getPreparingOrders(@Query("page") int page,
                                           @Query("pageSize") int pageSize);

    @GET("api/order/delivered")
    Call<OrderResponse> getDeliveredOrders(@Query("page") int page,
                                           @Query("pageSize") int pageSize);

    @GET("api/order/completed")
    Call<OrderResponse> getCompletedOrders(@Query("page") int page,
                                           @Query("pageSize") int pageSize);

    @GET("api/order/cancelled")
    Call<OrderResponse> getCancelledOrders(@Query("page") int page,
                                           @Query("pageSize") int pageSize);

    // Status update endpoints (NEW - theo ảnh 2)
    @PATCH("api/order/{orderId}/confirm")
    Call<Order> confirmOrder(@Path("orderId") int orderId);

    @PATCH("api/order/{orderId}/complete")
    Call<Order> completeOrder(@Path("orderId") int orderId);

    @PATCH("api/order/{orderId}/prepare")
    Call<Order> prepareOrder(@Path("orderId") int orderId);

    @PATCH("api/order/{orderId}/deliver")
    Call<Order> deliverOrder(@Path("orderId") int orderId);

    @PATCH("api/order/{orderId}/cancel")
    Call<Order> cancelOrder(@Path("orderId") int orderId);

    // Legacy endpoints (giữ lại cho backward compatibility)
    @GET("api/order")
    Call<OrderResponse> getOrdersByStatus(@Query("status") String status,
                                          @Query("page") int page,
                                          @Query("pageSize") int pageSize);

    @PUT("api/order/{orderId}/status")
    Call<Order> updateOrderStatus(@Path("orderId") int orderId,
                                  @Body UpdateOrderStatusRequest request);

    @GET("api/order/{orderId}")
    Call<Order> getOrderById(@Path("orderId") int orderId);

    @POST("api/Order")
    Call<Order> createOrder(@Body CreateOrderRequest request);

    // ========== AUTH APIs ==========
    @POST("api/Auth/login")
    Call<AuthResponse> login(@Body Login request);

    @GET("api/Auth/profile")
    Call<User> getUserProfile();

    @PUT("api/Auth/profile")
    Call<User> updateProfile(@Body UpdateProfileRequest request);

    @POST("api/Auth/change-password")
    Call<ApiResponse> changePassword(@Body ChangePasswordRequest request);

    // ========== PRODUCT APIs ==========
    @GET("api/product")
    Call<List<Product>> getProducts();

    @GET("api/products")
    Call<List<Product>> getProductsList();

    @GET("api/Product")
    Call<List<Product>> getProductsOriginal();

    @GET("api/product/{productId}")
    Call<Product> getProductById(@Path("productId") int productId);

    @GET("api/product/available")
    Call<List<Product>> getAvailableProducts();

    // ========== COMBO APIs ==========
    @GET("api/combo")
    Call<List<Combo>> getCombos();

    @GET("api/combos")
    Call<List<Combo>> getCombosList();

    @GET("api/Combo")
    Call<List<Combo>> getCombosOriginal();

    @GET("api/combo/{comboId}")
    Call<Combo> getComboById(@Path("comboId") int comboId);

    @GET("api/combo/available")
    Call<List<Combo>> getAvailableCombos();

    // ========== TOPPING APIs ==========
    @GET("api/topping")
    Call<List<Topping>> getToppings();

    @GET("api/toppings")
    Call<List<Topping>> getToppingsList();

    @GET("api/Topping")
    Call<List<Topping>> getToppingsOriginal();

    @GET("api/topping/{toppingId}")
    Call<Topping> getToppingById(@Path("toppingId") int toppingId);

    @GET("api/topping/available")
    Call<List<Topping>> getAvailableToppings();

    // ========== PAYMENT APIs ==========
    @POST("api/Payments")
    Call<Payment> createPayment(@Body Payment payment);

    @GET("api/Payments")
    Call<List<Payment>> getPayments();
}