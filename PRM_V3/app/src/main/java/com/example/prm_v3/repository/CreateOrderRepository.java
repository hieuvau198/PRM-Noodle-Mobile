package com.example.prm_v3.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.prm_v3.api.ApiClient;
import com.example.prm_v3.api.ApiService;
import com.example.prm_v3.api.CreateOrderRequest;
import com.example.prm_v3.model.Order;
import com.example.prm_v3.model.Product;
import com.example.prm_v3.model.Combo;
import com.example.prm_v3.model.Topping;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrderRepository {
    private static CreateOrderRepository instance;
    private ApiService apiService;

    // LiveData for caching
    private MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Combo>> combosLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Topping>> toppingsLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();

    private CreateOrderRepository() {
        apiService = ApiClient.getApiService();
    }

    public static synchronized CreateOrderRepository getInstance() {
        if (instance == null) {
            instance = new CreateOrderRepository();
        }
        return instance;
    }

    // Getters
    public LiveData<List<Product>> getProductsLiveData() { return productsLiveData; }
    public LiveData<List<Combo>> getCombosLiveData() { return combosLiveData; }
    public LiveData<List<Topping>> getToppingsLiveData() { return toppingsLiveData; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getLoadingLiveData() { return loadingLiveData; }

    public void fetchProducts() {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        Call<List<Product>> call = apiService.getAvailableProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    productsLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue("Không thể tải danh sách sản phẩm");
                    productsLiveData.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Lỗi kết nối: " + t.getMessage());
                productsLiveData.setValue(new ArrayList<>());
            }
        });
    }

    public void fetchCombos() {
        Call<List<Combo>> call = apiService.getAvailableCombos();
        call.enqueue(new Callback<List<Combo>>() {
            @Override
            public void onResponse(Call<List<Combo>> call, Response<List<Combo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    combosLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue("Không thể tải danh sách combo");
                    combosLiveData.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<Combo>> call, Throwable t) {
                errorLiveData.setValue("Lỗi kết nối combo: " + t.getMessage());
                combosLiveData.setValue(new ArrayList<>());
            }
        });
    }

    public void fetchToppings() {
        Call<List<Topping>> call = apiService.getAvailableToppings();
        call.enqueue(new Callback<List<Topping>>() {
            @Override
            public void onResponse(Call<List<Topping>> call, Response<List<Topping>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    toppingsLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue("Không thể tải danh sách topping");
                    toppingsLiveData.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<Topping>> call, Throwable t) {
                errorLiveData.setValue("Lỗi kết nối topping: " + t.getMessage());
                toppingsLiveData.setValue(new ArrayList<>());
            }
        });
    }

    public void createOrder(CreateOrderRequest request, OnOrderCreatedListener listener) {
        Call<Order> call = apiService.createOrder(request);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (listener != null) {
                        listener.onSuccess(response.body());
                    }
                } else {
                    String errorMsg = "Đặt hàng thất bại";
                    if (response.code() == 400) {
                        errorMsg = "Thông tin đơn hàng không hợp lệ";
                    } else if (response.code() == 500) {
                        errorMsg = "Lỗi server. Vui lòng thử lại sau";
                    }
                    if (listener != null) {
                        listener.onError(errorMsg);
                    }
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                if (listener != null) {
                    listener.onError("Lỗi kết nối: " + t.getMessage());
                }
            }
        });
    }

    public interface OnOrderCreatedListener {
        void onSuccess(Order order);
        void onError(String error);
    }

    public void clearCache() {
        productsLiveData.setValue(new ArrayList<>());
        combosLiveData.setValue(new ArrayList<>());
        toppingsLiveData.setValue(new ArrayList<>());
        errorLiveData.setValue(null);
        loadingLiveData.setValue(false);
    }
}