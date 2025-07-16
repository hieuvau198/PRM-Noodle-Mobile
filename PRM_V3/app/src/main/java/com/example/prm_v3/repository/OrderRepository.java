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

    // Track current filter state for auto-refresh
    private String currentStatus = "all";
    private int currentPage = 1;
    private int currentPageSize = 20;
    private boolean isRefreshEnabled = true;

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

    // ========== STATE MANAGEMENT ==========

    public void setCurrentFilter(String status, int page, int pageSize) {
        this.currentStatus = status;
        this.currentPage = page;
        this.currentPageSize = pageSize;
        Log.d(TAG, "Current filter set: " + status + ", page: " + page);
    }

    public void enableAutoRefresh(boolean enabled) {
        this.isRefreshEnabled = enabled;
        Log.d(TAG, "Auto refresh: " + (enabled ? "enabled" : "disabled"));
    }

    // ========== DIRECT STATUS ENDPOINT METHODS ==========

    public void fetchOrdersByDirectEndpoint(String status, int page, int pageSize) {
        // Update current state for auto-refresh
        setCurrentFilter(status, page, pageSize);

        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        Call<OrderResponse> call = getDirectStatusCall(status, page, pageSize);

        if (call != null) {
            executeCall(call, "fetchOrdersByDirectEndpoint: " + status);
        } else {
            Log.w(TAG, "Direct endpoint not available for status: " + status + ", using legacy API");
            fetchOrdersByStatus(status, page, pageSize);
        }
    }

    private Call<OrderResponse> getDirectStatusCall(String status, int page, int pageSize) {
        if (status == null || status.equals("all")) {
            return apiService.getOrders(page, pageSize);
        }

        switch (status.toLowerCase()) {
            case "pending":
                return apiService.getPendingOrders(page, pageSize);
            case "confirmed":
                return apiService.getConfirmedOrders(page, pageSize);
            case "preparing":
                return apiService.getPreparingOrders(page, pageSize);
            case "delivered":
                return apiService.getDeliveredOrders(page, pageSize);
            case "completed":
                return apiService.getCompletedOrders(page, pageSize);
            case "cancelled":
                return apiService.getCancelledOrders(page, pageSize);
            default:
                Log.w(TAG, "Unknown status for direct endpoint: " + status);
                return null;
        }
    }

    public void fetchOrdersByDirectEndpoint(String status) {
        fetchOrdersByDirectEndpoint(status, 1, 20);
    }

    // ========== ENHANCED STATUS-SPECIFIC METHODS ==========

    public void fetchPendingOrders(int page, int pageSize) {
        setCurrentFilter("pending", page, pageSize);
        loadingLiveData.setValue(true);
        Call<OrderResponse> call = apiService.getPendingOrders(page, pageSize);
        executeCall(call, "fetchPendingOrders");
    }

    public void fetchConfirmedOrders(int page, int pageSize) {
        setCurrentFilter("confirmed", page, pageSize);
        loadingLiveData.setValue(true);
        Call<OrderResponse> call = apiService.getConfirmedOrders(page, pageSize);
        executeCall(call, "fetchConfirmedOrders");
    }

    public void fetchPreparingOrders(int page, int pageSize) {
        setCurrentFilter("preparing", page, pageSize);
        loadingLiveData.setValue(true);
        Call<OrderResponse> call = apiService.getPreparingOrders(page, pageSize);
        executeCall(call, "fetchPreparingOrders");
    }

    public void fetchDeliveredOrders(int page, int pageSize) {
        setCurrentFilter("delivered", page, pageSize);
        loadingLiveData.setValue(true);
        Call<OrderResponse> call = apiService.getDeliveredOrders(page, pageSize);
        executeCall(call, "fetchDeliveredOrders");
    }

    public void fetchCompletedOrders(int page, int pageSize) {
        setCurrentFilter("completed", page, pageSize);
        loadingLiveData.setValue(true);
        Call<OrderResponse> call = apiService.getCompletedOrders(page, pageSize);
        executeCall(call, "fetchCompletedOrders");
    }

    public void fetchCancelledOrders(int page, int pageSize) {
        setCurrentFilter("cancelled", page, pageSize);
        loadingLiveData.setValue(true);
        Call<OrderResponse> call = apiService.getCancelledOrders(page, pageSize);
        executeCall(call, "fetchCancelledOrders");
    }

    // ========== ENHANCED STATUS UPDATE WITH AUTO-REFRESH ==========

    public void patchOrderStatus(int orderId, String newStatus, OnUpdateStatusListener listener) {
        Call<Order> call = null;

        Log.d(TAG, "patchOrderStatus: orderId=" + orderId + ", newStatus=" + newStatus);

        switch (newStatus.toLowerCase()) {
            case "confirmed":
                call = apiService.confirmOrder(orderId);
                break;
            case "preparing":
                call = apiService.prepareOrder(orderId);
                break;
            case "ready":
                call = apiService.deliverOrder(orderId);
                break;
            case "delivered":
                call = apiService.completeOrder(orderId);
                break;
            case "cancelled":
                call = apiService.cancelOrder(orderId);
                break;
            default:
                String errorMsg = "Trạng thái không hợp lệ: " + newStatus;
                Log.e(TAG, errorMsg);
                if (listener != null) {
                    listener.onError(errorMsg);
                }
                return;
        }

        if (call != null) {
            call.enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Call<Order> call, Response<Order> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Order updatedOrder = response.body();
                        String actualStatus = updatedOrder.getOrderStatus();

                        Log.d(TAG, "Status update successful - Expected: " + newStatus + ", Actual: " + actualStatus);

                        if (listener != null) {
                            listener.onSuccess("Cập nhật trạng thái thành công");
                        }

                        // ENHANCED: Smart refresh based on current filter
                        refreshAfterStatusUpdate(orderId, actualStatus);

                    } else {
                        String errorMsg = handleErrorResponse(response.code());
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

    // ========== SMART REFRESH LOGIC ==========

    private void refreshAfterStatusUpdate(int orderId, String newStatus) {
        if (!isRefreshEnabled) {
            Log.d(TAG, "Auto refresh disabled, skipping refresh");
            return;
        }

        Log.d(TAG, "Smart refresh - Order " + orderId + " updated to " + newStatus +
                ", current filter: " + currentStatus);

        // Strategy 1: If order no longer matches current filter, remove it locally
        if (shouldRemoveFromCurrentList(newStatus)) {
            removeOrderFromCurrentList(orderId);
            Log.d(TAG, "Order removed from current list as it no longer matches filter");
        }

        // Strategy 2: Always refresh current view after a short delay
        // This ensures data consistency
        refreshCurrentDataDelayed();
    }

    private boolean shouldRemoveFromCurrentList(String newOrderStatus) {
        if (currentStatus.equals("all")) {
            return false; // "all" tab shows all orders regardless of status
        }

        return !currentStatus.equalsIgnoreCase(newOrderStatus);
    }

    private void removeOrderFromCurrentList(int orderId) {
        List<Order> currentOrders = ordersLiveData.getValue();
        if (currentOrders != null) {
            List<Order> updatedOrders = new java.util.ArrayList<>(currentOrders);
            updatedOrders.removeIf(order -> order.getOrderId() == orderId);

            // Update LiveData on main thread
            android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
            mainHandler.post(() -> {
                ordersLiveData.setValue(updatedOrders);
                Log.d(TAG, "Order " + orderId + " removed from local list");
            });
        }
    }

    private void refreshCurrentDataDelayed() {
        // Refresh after 1 second to allow server to process the update
        android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
        handler.postDelayed(() -> {
            Log.d(TAG, "Performing delayed refresh with current filter: " + currentStatus);
            fetchOrdersByDirectEndpoint(currentStatus, 1, currentPageSize); // Reset to page 1
        }, 1000);
    }

    // ========== ERROR HANDLING ==========

    private String handleErrorResponse(int code) {
        switch (code) {
            case 400:
                return "Không thể chuyển trạng thái từ trạng thái hiện tại";
            case 401:
                return "Phiên đăng nhập đã hết hạn";
            case 403:
                return "Không có quyền cập nhật đơn hàng này";
            case 404:
                return "Không tìm thấy đơn hàng";
            case 500:
                return "Lỗi máy chủ, vui lòng thử lại";
            default:
                return "Lỗi cập nhật trạng thái: " + code;
        }
    }

    // ========== LEGACY METHODS (updated) ==========

    public void fetchOrders() {
        fetchOrdersWithPagination(1, 20);
    }

    public void fetchOrdersWithPagination(int page, int pageSize) {
        setCurrentFilter("all", page, pageSize);
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        Call<OrderResponse> call = apiService.getOrders(page, pageSize);
        executeCall(call, "fetchOrdersWithPagination");
    }

    public void fetchOrdersByStatus(String status) {
        fetchOrdersByStatus(status, 1, 20);
    }

    public void fetchOrdersByStatus(String status, int page, int pageSize) {
        Call<OrderResponse> directCall = getDirectStatusCall(status, page, pageSize);

        if (directCall != null) {
            Log.d(TAG, "Using direct endpoint for status: " + status);
            setCurrentFilter(status, page, pageSize);
            loadingLiveData.setValue(true);
            executeCall(directCall, "fetchOrdersByStatus (direct): " + status);
        } else {
            Log.d(TAG, "Using legacy endpoint for status: " + status);
            setCurrentFilter(status, page, pageSize);
            loadingLiveData.setValue(true);
            Call<OrderResponse> legacyCall = apiService.getOrdersByStatus(status, page, pageSize);
            executeCall(legacyCall, "fetchOrdersByStatus (legacy): " + status);
        }
    }

    public void fetchOrdersByStatusDirect(String status, int page, int pageSize) {
        fetchOrdersByDirectEndpoint(status, page, pageSize);
    }

    // ========== MANUAL REFRESH METHODS ==========

    public void refreshCurrentData() {
        Log.d(TAG, "Manual refresh requested for: " + currentStatus);
        fetchOrdersByDirectEndpoint(currentStatus, 1, currentPageSize); // Reset to page 1
    }

    public void refreshCurrentDataKeepPagination() {
        Log.d(TAG, "Manual refresh (keep pagination) for: " + currentStatus + ", page: " + currentPage);
        fetchOrdersByDirectEndpoint(currentStatus, currentPage, currentPageSize);
    }

    // ========== UTILITY METHODS ==========

    public void updateToNextStatus(int orderId, String currentStatus, OnUpdateStatusListener listener) {
        String nextStatus = getNextStatus(currentStatus);

        if (nextStatus != null) {
            Log.d(TAG, "Updating order " + orderId + " from " + currentStatus + " to " + nextStatus);
            patchOrderStatus(orderId, nextStatus, listener);
        } else {
            String errorMsg = "Không thể cập nhật từ trạng thái: " + currentStatus;
            Log.w(TAG, errorMsg);
            if (listener != null) {
                listener.onError(errorMsg);
            }
        }
    }

    private String getNextStatus(String currentStatus) {
        if (currentStatus == null) return null;

        switch (currentStatus.toLowerCase()) {
            case "pending":
                return "confirmed";
            case "confirmed":
                return "preparing";
            case "preparing":
                return "ready";
            case "ready":
                return "delivered";
            default:
                return null;
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
                    String errorMsg = handleErrorResponse(response.code());
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

    // ========== PERFORMANCE MONITORING ==========

    public void compareEndpointPerformance(String status, int page, int pageSize) {
        Log.d(TAG, "Performance test for status: " + status);

        long startTime = System.currentTimeMillis();

        Call<OrderResponse> directCall = getDirectStatusCall(status, page, pageSize);
        if (directCall != null) {
            directCall.enqueue(new Callback<OrderResponse>() {
                @Override
                public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                    long directTime = System.currentTimeMillis() - startTime;
                    Log.d(TAG, "Direct endpoint time: " + directTime + "ms");
                }

                @Override
                public void onFailure(Call<OrderResponse> call, Throwable t) {
                    Log.d(TAG, "Direct endpoint failed: " + t.getMessage());
                }
            });
        }
    }

    // ========== ERROR MANAGEMENT ==========

    /**
     * Clear error state
     */
    public void clearError() {
        errorLiveData.setValue(null);
        Log.d(TAG, "Error state cleared");
    }

    /**
     * Clear all states
     */
    public void clearAllStates() {
        errorLiveData.setValue(null);
        loadingLiveData.setValue(false);
        Log.d(TAG, "All states cleared");
    }

    // ========== GETTERS FOR CURRENT STATE ==========

    public String getCurrentStatus() {
        return currentStatus;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getCurrentPageSize() {
        return currentPageSize;
    }

    // Interface for status update callbacks
    public interface OnUpdateStatusListener {
        void onSuccess(String message);
        void onError(String error);
    }
}