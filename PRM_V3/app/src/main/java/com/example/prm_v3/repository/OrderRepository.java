package com.example.prm_v3.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.prm_v3.api.ApiClient;
import com.example.prm_v3.api.ApiService;
import com.example.prm_v3.model.Order;
import com.example.prm_v3.model.OrderResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private static final String TAG = "OrderRepository";
    private static OrderRepository instance;
    private ApiService apiService;

    // LiveData for UI observation
    private MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private OrderRepository() {
        apiService = ApiClient.getApiService();
    }

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    // Getters for LiveData
    public LiveData<List<Order>> getOrdersLiveData() {
        return ordersLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // ========== NEW STATUS-SPECIFIC METHODS ==========

    public void fetchOrdersByStatusDirect(String status, int page, int pageSize) {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        Call<OrderResponse> call = null;

        switch (status.toLowerCase()) {
            case "pending":
                call = apiService.getPendingOrders(page, pageSize);
                break;
            case "confirmed":
                call = apiService.getConfirmedOrders(page, pageSize);
                break;
            case "preparing":
                call = apiService.getPreparingOrders(page, pageSize);
                break;
            case "delivered":
                call = apiService.getDeliveredOrders(page, pageSize);
                break;
            case "completed":
                call = apiService.getCompletedOrders(page, pageSize);
                break;
            case "cancelled":
                call = apiService.getCancelledOrders(page, pageSize);
                break;
            case "all":
            default:
                call = apiService.getOrders(page, pageSize);
                break;
        }

        if (call != null) {
            executeCall(call, "fetchOrdersByStatusDirect: " + status);
        }
    }

    // ========== PATCH STATUS UPDATE METHODS ==========

    public void patchOrderStatus(int orderId, String newStatus, OnUpdateStatusListener listener) {
        Call<Order> call = null;

        switch (newStatus.toLowerCase()) {
            case "confirmed":
                call = apiService.confirmOrder(orderId);
                break;
            case "preparing":
                call = apiService.prepareOrder(orderId);
                break;
            case "delivered":
                call = apiService.deliverOrder(orderId);
                break;
            case "completed":
                call = apiService.completeOrder(orderId);
                break;
            case "cancelled":
                call = apiService.cancelOrder(orderId);
                break;
            default:
                if (listener != null) {
                    listener.onError("Trạng thái không hợp lệ: " + newStatus);
                }
                return;
        }

        if (call != null) {
            call.enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Call<Order> call, Response<Order> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Status updated successfully for order " + orderId + " to " + newStatus);
                        if (listener != null) {
                            listener.onSuccess("Cập nhật trạng thái thành công");
                        }
                        // Refresh current data
                        refreshCurrentData();
                    } else {
                        String errorMsg = "Lỗi cập nhật trạng thái: " + response.code();
                        Log.e(TAG, errorMsg);
                        if (listener != null) {
                            listener.onError(errorMsg);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Order> call, Throwable t) {
                    String errorMsg = "Lỗi kết nối: " + t.getMessage();
                    Log.e(TAG, errorMsg, t);
                    if (listener != null) {
                        listener.onError(errorMsg);
                    }
                }
            });
        }
    }

    // ========== LEGACY METHODS (kept for backward compatibility) ==========

    public void fetchOrders() {
        fetchOrdersWithPagination(1, 20);
    }

    public void fetchOrdersWithPagination(int page, int pageSize) {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        Call<OrderResponse> call = apiService.getOrders(page, pageSize);
        executeCall(call, "fetchOrdersWithPagination");
    }

    public void fetchOrdersByStatus(String status) {
        fetchOrdersByStatusWithPagination(status, 1, 20);
    }

    public void fetchOrdersByStatusWithPagination(String status, int page, int pageSize) {
        if (status == null || status.equals("all")) {
            fetchOrdersWithPagination(page, pageSize);
        } else {
            // Use new direct status endpoints
            fetchOrdersByStatusDirect(status, page, pageSize);
        }
    }

    // ========== HELPER METHODS ==========

    private void executeCall(Call<OrderResponse> call, String operation) {
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                loadingLiveData.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    OrderResponse orderResponse = response.body();
                    List<Order> orders = orderResponse.getOrders();

                    Log.d(TAG, operation + " success: " + (orders != null ? orders.size() : 0) + " orders");
                    ordersLiveData.setValue(orders);
                    errorLiveData.setValue(null);
                } else {
                    String errorMsg = "Lỗi tải dữ liệu: " + response.code();
                    Log.e(TAG, operation + " error: " + errorMsg);
                    errorLiveData.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                loadingLiveData.setValue(false);
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, operation + " failure: " + errorMsg, t);
                errorLiveData.setValue(errorMsg);
            }
        });
    }

    private void refreshCurrentData() {
        // This method can be enhanced to remember current filter and page
        // For now, just refresh all orders
        fetchOrders();
    }

    // Interface for status update callbacks
    public interface OnUpdateStatusListener {
        void onSuccess(String message);
        void onError(String error);
    }
}