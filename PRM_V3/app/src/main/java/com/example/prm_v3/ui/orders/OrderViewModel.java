package com.example.prm_v3.ui.orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_v3.model.Order;
import com.example.prm_v3.repository.OrderRepository;

import java.util.List;

public class OrderViewModel extends ViewModel {

    private OrderRepository orderRepository;
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    public OrderViewModel() {
        orderRepository = OrderRepository.getInstance();
    }

    // ========== LIVEDATA GETTERS ==========

    public LiveData<List<Order>> getOrders() {
        return orderRepository.getOrdersLiveData();
    }

    public LiveData<Boolean> getLoading() {
        return orderRepository.getLoadingLiveData();
    }

    public LiveData<String> getError() {
        return orderRepository.getErrorLiveData();
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    // ========== AUTO REFRESH CONTROL ==========

    /**
     * Enable/disable auto refresh after status updates
     */
    public void enableAutoRefresh(boolean enabled) {
        orderRepository.enableAutoRefresh(enabled);
    }

    /**
     * Clear error state through repository
     */
    public void clearError() {
        orderRepository.clearError();
    }

    /**
     * Clear all states (error, loading)
     */
    public void clearAllStates() {
        orderRepository.clearAllStates();
    }

    // ========== DIRECT STATUS ENDPOINT METHODS ==========

    /**
     * Load orders using direct status endpoints - PREFERRED METHOD
     */
    public void loadOrdersByDirectEndpoint(String status, int page, int pageSize) {
        orderRepository.fetchOrdersByDirectEndpoint(status, page, pageSize);
    }

    public void loadOrdersByDirectEndpoint(String status) {
        orderRepository.fetchOrdersByDirectEndpoint(status);
    }

    // ========== SPECIFIC STATUS METHODS ==========

    public void loadPendingOrders(int page, int pageSize) {
        orderRepository.fetchPendingOrders(page, pageSize);
    }

    public void loadConfirmedOrders(int page, int pageSize) {
        orderRepository.fetchConfirmedOrders(page, pageSize);
    }

    public void loadPreparingOrders(int page, int pageSize) {
        orderRepository.fetchPreparingOrders(page, pageSize);
    }

    public void loadDeliveredOrders(int page, int pageSize) {
        orderRepository.fetchDeliveredOrders(page, pageSize);
    }

    public void loadCompletedOrders(int page, int pageSize) {
        orderRepository.fetchCompletedOrders(page, pageSize);
    }

    public void loadCancelledOrders(int page, int pageSize) {
        orderRepository.fetchCancelledOrders(page, pageSize);
    }

    // ========== ENHANCED STATUS UPDATE WITH AUTO REFRESH ==========

    public void updateOrderStatus(int orderId, String newStatus) {
        orderRepository.patchOrderStatus(orderId, newStatus, new OrderRepository.OnUpdateStatusListener() {
            @Override
            public void onSuccess(String message) {
                statusMessage.setValue(message); // Use setValue instead of postValue for UI thread
            }

            @Override
            public void onError(String error) {
                statusMessage.setValue(error);
            }
        });
    }

    // Specific status update methods
    public void confirmOrder(int orderId) {
        updateOrderStatus(orderId, "confirmed");
    }

    public void prepareOrder(int orderId) {
        updateOrderStatus(orderId, "preparing");
    }

    public void deliverOrder(int orderId) {
        updateOrderStatus(orderId, "ready");  // preparing -> ready via /deliver
    }

    public void completeOrder(int orderId) {
        updateOrderStatus(orderId, "delivered");  // ready -> delivered via /complete
    }

    public void cancelOrder(int orderId) {
        updateOrderStatus(orderId, "cancelled");
    }

    // ========== MANUAL REFRESH METHODS ==========

    /**
     * Manual refresh current data (reset to page 1)
     */
    public void refreshCurrentData() {
        orderRepository.refreshCurrentData();
    }

    /**
     * Manual refresh keeping current pagination
     */
    public void refreshCurrentDataKeepPagination() {
        orderRepository.refreshCurrentDataKeepPagination();
    }

    // ========== CONVENIENCE METHODS ==========

    /**
     * Smart method that chooses the best endpoint based on status
     */
    public void loadOrdersOptimal(String status, int page, int pageSize) {
        if (isDirectEndpointAvailable(status)) {
            loadOrdersByDirectEndpoint(status, page, pageSize);
        } else {
            loadOrdersByStatusWithPagination(status, page, pageSize);
        }
    }

    private boolean isDirectEndpointAvailable(String status) {
        if (status == null) return true;

        switch (status.toLowerCase()) {
            case "all":
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

    // ========== STATE MANAGEMENT ==========

    /**
     * Get current filter state from repository
     */
    public String getCurrentStatus() {
        return orderRepository.getCurrentStatus();
    }

    public int getCurrentPage() {
        return orderRepository.getCurrentPage();
    }

    public int getCurrentPageSize() {
        return orderRepository.getCurrentPageSize();
    }

    // ========== BULK OPERATIONS ==========

    /**
     * Load multiple status types for dashboard
     */
    public void loadDashboardData() {
        loadPendingOrders(1, 5);
        loadConfirmedOrders(1, 5);
        loadPreparingOrders(1, 5);
    }

    // ========== LEGACY METHODS (keep for backward compatibility) ==========

    public void loadOrders() {
        orderRepository.fetchOrders();
    }

    public void loadOrdersByStatus(String status) {
        if (isDirectEndpointAvailable(status)) {
            loadOrdersByDirectEndpoint(status);
        } else {
            orderRepository.fetchOrdersByStatus(status);
        }
    }

    public void loadOrdersWithPagination(int page, int pageSize) {
        orderRepository.fetchOrdersWithPagination(page, pageSize);
    }

    public void loadOrdersByStatusWithPagination(String status, int page, int pageSize) {
        if (isDirectEndpointAvailable(status)) {
            loadOrdersByDirectEndpoint(status, page, pageSize);
        } else {
            orderRepository.fetchOrdersByStatus(status, page, pageSize);
        }
    }

    public void loadOrdersByStatusDirect(String status, int page, int pageSize) {
        orderRepository.fetchOrdersByStatusDirect(status, page, pageSize);
    }

    public void refreshOrders() {
        orderRepository.fetchOrders();
    }

    // ========== FILTER AND SEARCH ==========

    public void loadOrdersWithFilters(OrderFilter filter) {
        if (filter.hasOnlyStatusFilter()) {
            loadOrdersByDirectEndpoint(filter.getStatus(), filter.getPage(), filter.getPageSize());
        } else {
            loadOrdersByStatusWithPagination(filter.getStatus(), filter.getPage(), filter.getPageSize());
        }
    }

    // ========== PERFORMANCE TESTING ==========

    public void testEndpointPerformance(String status, int page, int pageSize) {
        orderRepository.compareEndpointPerformance(status, page, pageSize);
    }

    // ========== ERROR HANDLING ==========

    public void retryFailedRequest(String lastStatus, int lastPage, int lastPageSize) {
        if (isDirectEndpointAvailable(lastStatus)) {
            loadOrdersByDirectEndpoint(lastStatus, lastPage, lastPageSize);
        } else {
            loadOrdersByStatusWithPagination(lastStatus, lastPage, lastPageSize);
        }
    }

    // ========== HELPER CLASSES ==========

    public static class OrderFilter {
        private String status;
        private int page = 1;
        private int pageSize = 20;
        private String customerName;
        private String dateRange;

        public OrderFilter(String status) {
            this.status = status;
        }

        public OrderFilter(String status, int page, int pageSize) {
            this.status = status;
            this.page = page;
            this.pageSize = pageSize;
        }

        public boolean hasOnlyStatusFilter() {
            return customerName == null && dateRange == null;
        }

        // Getters
        public String getStatus() { return status; }
        public int getPage() { return page; }
        public int getPageSize() { return pageSize; }
        public String getCustomerName() { return customerName; }
        public String getDateRange() { return dateRange; }

        // Setters with fluent interface
        public OrderFilter setCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public OrderFilter setDateRange(String dateRange) {
            this.dateRange = dateRange;
            return this;
        }

        public OrderFilter setPage(int page) {
            this.page = page;
            return this;
        }

        public OrderFilter setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }
    }

    // ========== ANALYTICS AND MONITORING ==========

    public void loadOrderCounts() {
        loadPendingOrders(1, 1);
        loadConfirmedOrders(1, 1);
        loadPreparingOrders(1, 1);
        loadDeliveredOrders(1, 1);
        loadCompletedOrders(1, 1);
        loadCancelledOrders(1, 1);
    }

    // ========== CLEANUP ==========

    @Override
    protected void onCleared() {
        super.onCleared();
        // Disable auto refresh when ViewModel is cleared
        enableAutoRefresh(false);

        // Clear status message
        statusMessage.setValue(null);
    }
}