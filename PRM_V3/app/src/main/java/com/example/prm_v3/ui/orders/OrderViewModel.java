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

    // ORIGINAL METHODS (keep for backward compatibility)
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

    // NEW PAGINATION METHODS
    public void loadOrdersWithPagination(int page, int pageSize) {
        orderRepository.fetchOrdersWithPagination(page, pageSize);
    }

    public void loadOrdersByStatusWithPagination(String status, int page, int pageSize) {
        if (status == null || status.equals("all")) {
            orderRepository.fetchOrdersWithPagination(page, pageSize);
        } else {
            orderRepository.fetchOrdersByStatusWithPagination(status, page, pageSize);
        }
    }

    public void updateOrderStatus(int orderId, String newStatus) {
        orderRepository.updateOrderStatus(orderId, newStatus, new OrderRepository.OnUpdateStatusListener() {
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

    public void refreshOrders() {
        orderRepository.fetchOrders();
    }
}