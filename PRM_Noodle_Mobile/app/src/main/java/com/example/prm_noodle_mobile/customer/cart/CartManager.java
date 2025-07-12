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
        // Kiểm tra nếu sản phẩm đã có thì tăng quantity
        for (OrderItem existingItem : orderItems) {
            if (existingItem.getProductId() == item.getProductId()) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        orderItems.add(item);
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
    }

    public void clearCart() {
        orderItems.clear();
    }
}
