package com.example.prm_v3.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.prm_v3.api.ApiClient;
import com.example.prm_v3.api.ApiService;
import com.example.prm_v3.model.Order;
import com.example.prm_v3.model.OrderCombo;
import com.example.prm_v3.model.OrderItem;
import com.example.prm_v3.model.OrderResponse;
import com.example.prm_v3.model.OrderStatistics;

import java.util.ArrayList;
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
        if (newStatus == null || newStatus.trim().isEmpty()) {
            if (listener != null) {
                listener.onError("Trạng thái không hợp lệ");
            }
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
                if (listener != null) {
                    listener.onError("Trạng thái không hợp lệ hoặc không hỗ trợ PATCH: " + newStatus);
                }
                return;
        }

        if (call == null) {
            if (listener != null) {
                listener.onError("Không thể tạo request PATCH cho trạng thái: " + newStatus);
            }
            return;
        }

        call.enqueue(new retrofit2.Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, retrofit2.Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order updatedOrder = response.body();
                    initializeOrderLists(updatedOrder);
                    updateLocalOrderStatus(orderId, updatedOrder);
                    // Nếu xác nhận đơn hàng thành công thì tạo payment
                    if (newStatus.equalsIgnoreCase("confirmed")) {
                        createPaymentFromOrder(updatedOrder, listener);
                    }
                    if (listener != null) {
                        listener.onSuccess("Cập nhật trạng thái thành công");
                    }
                } else {
                    String errorMsg = "Lỗi khi cập nhật: " + response.code();
                    if (response.code() == 400) {
                        errorMsg = "Trạng thái không hợp lệ";
                    } else if (response.code() == 404) {
                        errorMsg = "Không tìm thấy đơn hàng";
                    }
                    if (listener != null) {
                        listener.onError(errorMsg);
                    }
                }
            }
            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                if (listener != null) {
                    listener.onError(errorMsg);
                }
            }
        });
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

    // Tạo payment mới từ order đã xác nhận, truyền callback để UI reload
    private void createPaymentFromOrder(Order order, OnUpdateStatusListener listener) {
        if (order == null) return;
        com.example.prm_v3.model.Payment payment = new com.example.prm_v3.model.Payment();
        payment.setOrderId(order.getOrderId());
        payment.setCustomerUserId(order.getUserId());
        payment.setCustomerName(order.getUserName());
        payment.setPaymentAmount(order.getTotalAmount());
        payment.setPaymentMethod(order.getPaymentMethod());
        payment.setPaymentStatus("pending"); // Khi mới tạo payment
        apiService.createPayment(payment).enqueue(new retrofit2.Callback<com.example.prm_v3.model.Payment>() {
            @Override
            public void onResponse(Call<com.example.prm_v3.model.Payment> call, retrofit2.Response<com.example.prm_v3.model.Payment> response) {
                if (listener != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        listener.onPaymentCreated(true, "Tạo payment thành công");
                    } else {
                        listener.onPaymentCreated(false, "Tạo payment thất bại: " + response.code());
                    }
                }
            }
            @Override
            public void onFailure(Call<com.example.prm_v3.model.Payment> call, Throwable t) {
                if (listener != null) {
                    listener.onPaymentCreated(false, "Lỗi tạo payment: " + t.getMessage());
                }
            }
        });
    }

    // Helper method to initialize order lists
    private void initializeOrderLists(Order order) {
        if (order.getOrderItems() == null) {
            order.setOrderItems(new ArrayList<OrderItem>());
        }
        if (order.getOrderCombos() == null) {
            order.setOrderCombos(new ArrayList<OrderCombo>());
        }
    }

    private void updateLocalOrderStatus(int orderId, Order updatedOrder) {
        List<Order> currentOrders = ordersLiveData.getValue();
        if (currentOrders != null) {
            for (int i = 0; i < currentOrders.size(); i++) {
                if (currentOrders.get(i).getOrderId() == orderId) {
                    currentOrders.set(i, updatedOrder);
                    break;
                }
            }
            ordersLiveData.setValue(currentOrders);
        }
    }

    private void handleErrorResponse(int code) {
        String errorMsg = "Lỗi khi tải dữ liệu: " + code;
        if (code == 401) {
            errorMsg = "Phiên đăng nhập đã hết hạn";
        } else if (code == 403) {
            errorMsg = "Không có quyền truy cập dữ liệu";
        } else if (code >= 500) {
            errorMsg = "Lỗi server. Vui lòng thử lại sau.";
        }
        errorLiveData.setValue(errorMsg);
    }

    private void handleNetworkError(Throwable t) {
        String errorMsg = "Lỗi kết nối: " + t.getMessage();
        if (t instanceof java.net.UnknownHostException) {
            errorMsg = "Không thể kết nối tới server. Kiểm tra kết nối mạng.";
        } else if (t instanceof java.net.SocketTimeoutException) {
            errorMsg = "Kết nối quá chậm. Vui lòng thử lại.";
        }
        errorLiveData.setValue(errorMsg);
    }

    public void getOrderStatistics(OnStatisticsLoadedListener listener) {
        List<Order> orders = ordersLiveData.getValue();
        if (orders != null) {
            OrderStatistics stats = OrderStatistics.calculateFromOrders(orders);
            if (listener != null) {
                listener.onStatisticsLoaded(stats);
            }
        } else {
            if (listener != null) {
                listener.onStatisticsLoaded(new OrderStatistics());
            }
        }
    }

    public void clearData() {
        ordersLiveData.setValue(new ArrayList<>());
        errorLiveData.setValue(null);
        loadingLiveData.setValue(false);
    }

    public interface OnStatisticsLoadedListener {
        void onStatisticsLoaded(OrderStatistics statistics);
    }

    // Interface for status update callbacks
    public interface OnUpdateStatusListener {
        void onSuccess(String message);
        void onError(String error);
        default void onPaymentCreated(boolean success, String message) {}
    }
}