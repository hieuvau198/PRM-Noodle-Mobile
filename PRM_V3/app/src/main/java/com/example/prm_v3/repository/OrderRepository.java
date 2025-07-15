package com.example.prm_v3.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.prm_v3.api.ApiClient;
import com.example.prm_v3.api.ApiService;
import com.example.prm_v3.api.UpdateOrderStatusRequest;
import com.example.prm_v3.model.Order;
import com.example.prm_v3.model.OrderItem;
import com.example.prm_v3.model.OrderCombo;
import com.example.prm_v3.model.OrderResponse;
import com.example.prm_v3.model.OrderStatistics;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private static OrderRepository instance;
    private ApiService apiService;
    private MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();

    private OrderRepository() {
        apiService = ApiClient.getApiService();
        ordersLiveData.setValue(new ArrayList<>());
    }

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    public LiveData<List<Order>> getOrdersLiveData() {
        return ordersLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    // ORIGINAL METHOD: Fetch all orders (no pagination)
    public void fetchOrders() {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        Call<OrderResponse> call = apiService.getOrders();
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                loadingLiveData.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body().getOrders();
                    if (orders != null) {
                        // Ensure all orders have properly initialized lists
                        for (Order order : orders) {
                            initializeOrderLists(order);
                        }
                        ordersLiveData.setValue(orders);
                    } else {
                        ordersLiveData.setValue(new ArrayList<>());
                    }
                    errorLiveData.setValue(null);
                } else {
                    handleErrorResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                loadingLiveData.setValue(false);
                handleNetworkError(t);
            }
        });
    }

    // NEW: Fetch orders with pagination
    public void fetchOrdersWithPagination(int page, int pageSize) {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        Call<OrderResponse> call = apiService.getOrders(page, pageSize);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                loadingLiveData.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body().getOrders();
                    if (orders != null) {
                        // Ensure all orders have properly initialized lists
                        for (Order order : orders) {
                            initializeOrderLists(order);
                        }
                        ordersLiveData.setValue(orders);
                    } else {
                        ordersLiveData.setValue(new ArrayList<>());
                    }
                    errorLiveData.setValue(null);
                } else {
                    handleErrorResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                loadingLiveData.setValue(false);
                handleNetworkError(t);
            }
        });
    }

    // ORIGINAL METHOD: Fetch orders by status (no pagination)
    public void fetchOrdersByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            fetchOrders();
            return;
        }

        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        Call<OrderResponse> call = apiService.getOrdersByStatus(status, 1, 100);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                loadingLiveData.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body().getOrders();
                    if (orders != null) {
                        // Ensure all orders have properly initialized lists
                        for (Order order : orders) {
                            initializeOrderLists(order);
                        }
                        ordersLiveData.setValue(orders);
                    } else {
                        ordersLiveData.setValue(new ArrayList<>());
                    }
                    errorLiveData.setValue(null);
                } else {
                    String errorMsg = "Lỗi khi tải dữ liệu theo trạng thái: " + response.code();
                    errorLiveData.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                loadingLiveData.setValue(false);
                String errorMsg = "Lỗi kết nối khi lọc theo trạng thái: " + t.getMessage();
                errorLiveData.setValue(errorMsg);
            }
        });
    }

    // NEW: Fetch orders by status with pagination
    public void fetchOrdersByStatusWithPagination(String status, int page, int pageSize) {
        if (status == null || status.trim().isEmpty()) {
            fetchOrdersWithPagination(page, pageSize);
            return;
        }

        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        Call<OrderResponse> call = apiService.getOrdersByStatus(status, page, pageSize);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                loadingLiveData.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body().getOrders();
                    if (orders != null) {
                        // Ensure all orders have properly initialized lists
                        for (Order order : orders) {
                            initializeOrderLists(order);
                        }
                        ordersLiveData.setValue(orders);
                    } else {
                        ordersLiveData.setValue(new ArrayList<>());
                    }
                    errorLiveData.setValue(null);
                } else {
                    String errorMsg = "Lỗi khi tải dữ liệu theo trạng thái: " + response.code();
                    errorLiveData.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                loadingLiveData.setValue(false);
                String errorMsg = "Lỗi kết nối khi lọc theo trạng thái: " + t.getMessage();
                errorLiveData.setValue(errorMsg);
            }
        });
    }

    public void updateOrderStatus(int orderId, String newStatus, OnUpdateStatusListener listener) {
        if (newStatus == null || newStatus.trim().isEmpty()) {
            if (listener != null) {
                listener.onError("Trạng thái không hợp lệ");
            }
            return;
        }

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(newStatus);

        Call<Order> call = apiService.updateOrderStatus(orderId, request);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order updatedOrder = response.body();

                    // Ensure lists are initialized
                    initializeOrderLists(updatedOrder);

                    // Update local data
                    updateLocalOrderStatus(orderId, updatedOrder);

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

    // PATCH: Update order status using PATCH endpoints
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

    public interface OnUpdateStatusListener {
        void onSuccess(String message);
        void onError(String error);
        default void onPaymentCreated(boolean success, String message) {}
    }
}