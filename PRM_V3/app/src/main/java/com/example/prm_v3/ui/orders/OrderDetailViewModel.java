package com.example.prm_v3.ui.orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_v3.api.ApiClient;
import com.example.prm_v3.api.ApiService;
import com.example.prm_v3.api.UpdateOrderStatusRequest;
import com.example.prm_v3.model.Order;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<Order> order = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<String> statusMessage = new MutableLiveData<>();

    public OrderDetailViewModel() {
        apiService = ApiClient.getApiService();
    }

    public LiveData<Order> getOrder() {
        return order;
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

    public void loadOrderDetail(int orderId) {
        loading.setValue(true);
        error.setValue(null);

        Call<Order> call = apiService.getOrderById(orderId);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                loading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    Order orderData = response.body();

                    // Ensure lists are initialized to avoid null pointer exceptions
                    if (orderData.getOrderItems() == null) {
                        orderData.setOrderItems(new java.util.ArrayList<>());
                    }
                    if (orderData.getOrderCombos() == null) {
                        orderData.setOrderCombos(new java.util.ArrayList<>());
                    }

                    order.setValue(orderData);
                    error.setValue(null);
                } else {
                    String errorMsg = "Không thể tải chi tiết đơn hàng";
                    if (response.code() == 404) {
                        errorMsg = "Không tìm thấy đơn hàng";
                    } else if (response.code() == 403) {
                        errorMsg = "Không có quyền truy cập đơn hàng này";
                    }
                    error.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                loading.setValue(false);
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "Không thể kết nối tới server. Vui lòng kiểm tra kết nối mạng.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Kết nối quá chậm. Vui lòng thử lại.";
                }
                error.setValue(errorMsg);
            }
        });
    }

    public void updateOrderStatus(int orderId, String newStatus) {
        if (newStatus == null || newStatus.trim().isEmpty()) {
            statusMessage.setValue("Trạng thái không hợp lệ");
            return;
        }

        // Validate status
        if (!isValidStatus(newStatus)) {
            statusMessage.setValue("Trạng thái không hợp lệ: " + newStatus);
            return;
        }

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
                statusMessage.setValue("Trạng thái không hợp lệ hoặc không hỗ trợ PATCH: " + newStatus);
                return;
        }

        if (call == null) {
            statusMessage.setValue("Không thể tạo request PATCH cho trạng thái: " + newStatus);
            return;
        }

        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order updatedOrder = response.body();

                    // Ensure lists are initialized
                    if (updatedOrder.getOrderItems() == null) {
                        updatedOrder.setOrderItems(new java.util.ArrayList<>());
                    }
                    if (updatedOrder.getOrderCombos() == null) {
                        updatedOrder.setOrderCombos(new java.util.ArrayList<>());
                    }

                    order.setValue(updatedOrder);
                    statusMessage.setValue("Cập nhật trạng thái thành công");
                    // Reload order detail after status update
                    loadOrderDetail(orderId);
                } else {
                    String errorMsg = "Lỗi khi cập nhật trạng thái";
                    if (response.code() == 400) {
                        errorMsg = "Trạng thái không hợp lệ hoặc không thể chuyển đổi";
                    } else if (response.code() == 404) {
                        errorMsg = "Không tìm thấy đơn hàng";
                    } else if (response.code() == 403) {
                        errorMsg = "Không có quyền cập nhật đơn hàng này";
                    }
                    statusMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                String errorMsg = "Lỗi kết nối khi cập nhật: " + t.getMessage();
                if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "Không thể kết nối tới server để cập nhật.";
                }
                statusMessage.setValue(errorMsg);
            }
        });
    }

    private boolean isValidStatus(String status) {
        switch (status.toLowerCase()) {
            case "pending":
            case "confirmed":
            case "preparing":
            case "delivered":
            case "completed":
            case "cancelled":
                return true;
            default:
                return false;
        }
    }

    private UpdateOrderStatusRequest createStatusRequest(String newStatus) {
        switch (newStatus.toLowerCase()) {
            case "pending":
                return UpdateOrderStatusRequest.pending();
            case "confirmed":
                return UpdateOrderStatusRequest.confirmed();
            case "preparing":
                return UpdateOrderStatusRequest.preparing();
            case "delivered":
                return UpdateOrderStatusRequest.delivered();
            case "completed":
                return UpdateOrderStatusRequest.completed();
            case "cancelled":
                return UpdateOrderStatusRequest.cancelled();
            default:
                return null;
        }
    }

    public void refreshOrderDetail() {
        Order currentOrder = order.getValue();
        if (currentOrder != null) {
            loadOrderDetail(currentOrder.getOrderId());
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up any resources if needed
    }
}