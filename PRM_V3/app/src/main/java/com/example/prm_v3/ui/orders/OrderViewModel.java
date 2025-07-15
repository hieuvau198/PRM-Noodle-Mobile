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

    // ========== NEW STATUS-SPECIFIC METHODS ==========

    public void loadOrdersByStatusDirect(String status, int page, int pageSize) {
        orderRepository.fetchOrdersByStatusDirect(status, page, pageSize);
    }

    // ========== ENHANCED STATUS UPDATE ==========

    public void updateOrderStatus(int orderId, String newStatus) {
        orderRepository.patchOrderStatus(orderId, newStatus, new OrderRepository.OnUpdateStatusListener() {
            @Override
            public void onSuccess(String message) {
                statusMessage.postValue(message);
            }

            @Override
            public void onError(String error) {
                statusMessage.postValue(error);
            }
        });
    }

    // Specific status update methods for better code organization
    public void confirmOrder(int orderId) {
        updateOrderStatus(orderId, "confirmed");
    }

    public void prepareOrder(int orderId) {
        updateOrderStatus(orderId, "preparing");
    }

    public void deliverOrder(int orderId) {
        updateOrderStatus(orderId, "delivered");
    }

    public void completeOrder(int orderId) {
        updateOrderStatus(orderId, "completed");
    }

    public void cancelOrder(int orderId) {
        updateOrderStatus(orderId, "cancelled");
    }

    // ========== LEGACY METHODS (keep for backward compatibility) ==========

    public void loadOrders() {
        orderRepository.fetchOrders();
    }

    public void loadOrdersByStatus(String status) {
        if (status == null || status.equals("all")) {
            orderRepository.fetchOrders();
        } else {
            orderRepository.fetchOrdersByStatus(status);
        }
    }

    public void loadOrdersWithPagination(int page, int pageSize) {
        orderRepository.fetchOrdersWithPagination(page, pageSize);
    }

    public void loadOrdersByStatusWithPagination(String status, int page, int pageSize) {
        if (status == null || status.equals("all")) {
            orderRepository.fetchOrdersWithPagination(page, pageSize);
        } else {
            // Use new direct status method for better performance
            orderRepository.fetchOrdersByStatusDirect(status, page, pageSize);
        }
    }

    public void refreshOrders() {
        orderRepository.fetchOrders();
    }
}