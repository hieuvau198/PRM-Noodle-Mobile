package com.example.prm_v3.api;

import com.example.prm_v3.model.Order;
import com.example.prm_v3.model.OrderResponse;
import com.example.prm_v3.model.Product;
import com.example.prm_v3.model.Combo;
import com.example.prm_v3.model.Topping;
import com.example.prm_v3.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Order APIs (đã hoạt động)
    @GET("api/order")
    Call<OrderResponse> getOrders();

    @GET("api/order")
    Call<OrderResponse> getOrders(@Query("page") int page,
                                  @Query("pageSize") int pageSize);

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

    // Product APIs - Thử các endpoint khác nhau
    @GET("api/product")  // lowercase
    Call<List<Product>> getProducts();

    @GET("api/products") // lowercase + s
    Call<List<Product>> getProductsList();

    @GET("api/Product")  // uppercase (original)
    Call<List<Product>> getProductsOriginal();

    @GET("api/product/{productId}")
    Call<Product> getProductById(@Path("productId") int productId);

    @GET("api/product/available")
    Call<List<Product>> getAvailableProducts();

    // Combo APIs - Thử các endpoint khác nhau
    @GET("api/combo")   // lowercase
    Call<List<Combo>> getCombos();

    @GET("api/combos")  // lowercase + s
    Call<List<Combo>> getCombosList();

    @GET("api/Combo")   // uppercase (original)
    Call<List<Combo>> getCombosOriginal();

    @GET("api/combo/{comboId}")
    Call<Combo> getComboById(@Path("comboId") int comboId);

    @GET("api/combo/available")
    Call<List<Combo>> getAvailableCombos();

    // Topping APIs - Thử các endpoint khác nhau
    @GET("api/topping") // lowercase
    Call<List<Topping>> getToppings();

    @GET("api/toppings") // lowercase + s
    Call<List<Topping>> getToppingsList();

    @GET("api/Topping")  // uppercase (original)
    Call<List<Topping>> getToppingsOriginal();

    @GET("api/topping/{toppingId}")
    Call<Topping> getToppingById(@Path("toppingId") int toppingId);

    @GET("api/topping/available")

    Call<List<Topping>> getAvailableToppings();
    @GET("api/Auth/profile")
    Call<User> getUserProfile();

    @PUT("api/Auth/profile")
    Call<User> updateProfile(@Body UpdateProfileRequest request);

    @POST("api/Auth/change-password")
    Call<ApiResponse> changePassword(@Body ChangePasswordRequest request);
}