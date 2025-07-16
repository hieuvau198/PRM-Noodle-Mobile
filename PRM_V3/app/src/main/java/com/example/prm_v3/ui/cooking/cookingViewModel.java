package com.example.prm_v3.ui.cooking;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.prm_v3.api.ApiClient;
import com.example.prm_v3.api.ApiService;
import com.example.prm_v3.model.Order;
import com.example.prm_v3.model.OrderResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class cookingViewModel extends ViewModel {
    private static final String TAG = "CookingViewModel";

    private ApiService apiService;
    private MutableLiveData<List<Order>> preparingOrders = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<String> statusMessage = new MutableLiveData<>();

    public cookingViewModel() {
        apiService = ApiClient.getApiService();
        loading.setValue(false);
    }

    // Getters for LiveData
    public LiveData<List<Order>> getPreparingOrders() {
        return preparingOrders;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    /**
     * Load preparing orders from API
     */
    public void loadPreparingOrders() {
        loading.setValue(true);
        error.setValue(null);

        Log.d(TAG, "Loading preparing orders...");

        Call<OrderResponse> call = apiService.getPreparingOrders();
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                loading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    OrderResponse orderResponse = response.body();
                    List<Order> orders = orderResponse.getOrders();

                    Log.d(TAG, "Successfully loaded " + (orders != null ? orders.size() : 0) + " preparing orders");
                    preparingOrders.setValue(orders);
                    error.setValue(null);
                } else {
                    String errorMsg = "Không thể tải danh sách đơn hàng";
                    if (response.code() == 404) {
                        errorMsg = "Không có đơn hàng nào đang chuẩn bị";
                    } else if (response.code() == 403) {
                        errorMsg = "Không có quyền truy cập";
                    }
                    Log.e(TAG, "Error loading preparing orders: " + response.code());
                    error.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                loading.setValue(false);
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "Không thể kết nối tới server";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Kết nối quá chậm";
                }
                Log.e(TAG, "Network error: " + errorMsg, t);
                error.setValue(errorMsg);
            }
        });
    }

    /**
     * Update order status to "ready" (preparing -> ready)
     */
    public void markOrderAsReady(int orderId) {
        Log.d(TAG, "Marking order " + orderId + " as ready");

        Call<Order> call = apiService.deliverOrder(orderId);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order updatedOrder = response.body();
                    Log.d(TAG, "Order " + orderId + " marked as ready successfully");

                    statusMessage.setValue("Đơn hàng #" + orderId + " đã sẵn sàng giao!");

                    // Remove order from current list since it's no longer "preparing"
                    removeOrderFromList(orderId);

                    // Refresh the list after a short delay
                    refreshOrdersDelayed();
                } else {
                    String errorMsg = "Không thể cập nhật trạng thái";
                    if (response.code() == 400) {
                        errorMsg = "Đơn hàng không thể chuyển sang trạng thái sẵn sàng";
                    } else if (response.code() == 404) {
                        errorMsg = "Không tìm thấy đơn hàng";
                    }
                    Log.e(TAG, "Error marking order as ready: " + response.code());
                    statusMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                String errorMsg = "Lỗi kết nối khi cập nhật: " + t.getMessage();
                Log.e(TAG, "Network error while marking as ready: " + errorMsg, t);
                statusMessage.setValue(errorMsg);
            }
        });
    }

    /**
     * Cancel order
     */
    public void cancelOrder(int orderId) {
        Log.d(TAG, "Cancelling order " + orderId);

        Call<Order> call = apiService.cancelOrder(orderId);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Order " + orderId + " cancelled successfully");

                    statusMessage.setValue("Đơn hàng #" + orderId + " đã được hủy!");

                    // Remove order from current list
                    removeOrderFromList(orderId);

                    // Refresh the list after a short delay
                    refreshOrdersDelayed();
                } else {
                    String errorMsg = "Không thể hủy đơn hàng";
                    if (response.code() == 400) {
                        errorMsg = "Đơn hàng không thể hủy ở trạng thái này";
                    } else if (response.code() == 404) {
                        errorMsg = "Không tìm thấy đơn hàng";
                    }
                    Log.e(TAG, "Error cancelling order: " + response.code());
                    statusMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                String errorMsg = "Lỗi kết nối khi hủy đơn: " + t.getMessage();
                Log.e(TAG, "Network error while cancelling: " + errorMsg, t);
                statusMessage.setValue(errorMsg);
            }
        });
    }

    /**
     * Remove order from current list (optimistic update)
     */
    private void removeOrderFromList(int orderId) {
        List<Order> currentOrders = preparingOrders.getValue();
        if (currentOrders != null) {
            List<Order> updatedOrders = new java.util.ArrayList<>(currentOrders);
            updatedOrders.removeIf(order -> order.getOrderId() == orderId);
            preparingOrders.setValue(updatedOrders);
        }
    }

    /**
     * Refresh orders after a delay
     */
    private void refreshOrdersDelayed() {
        // Refresh after 1 second to allow server to process
        android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
        handler.postDelayed(this::loadPreparingOrders, 1000);
    }

    /**
     * Manual refresh
     */
    public void refreshOrders() {
        loadPreparingOrders();
    }

    /**
     * Clear error state
     */
    public void clearError() {
        error.setValue(null);
    }

    /**
     * Clear status message
     */
    public void clearStatusMessage() {
        statusMessage.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "CookingViewModel cleared");
    }
}