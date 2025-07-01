package com.example.prm_noodle_mobile.customer.cart;

import com.example.prm_noodle_mobile.data.model.OrderItem;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<OrderItem> orderItems;

    private CartManager() {
        orderItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void clearCart() {
        orderItems.clear();
    }
} 