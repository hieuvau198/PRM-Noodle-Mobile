package com.example.prm_noodle_mobile.data.model;

import java.util.List;

public class OrderItem {
    private int productId;
    private int quantity;
    private List<ToppingOrder> toppings;

    public OrderItem(int productId, int quantity, List<ToppingOrder> toppings) {
        this.productId = productId;
        this.quantity = quantity;
        this.toppings = toppings;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public List<ToppingOrder> getToppings() { return toppings; }
    public void setToppings(List<ToppingOrder> toppings) { this.toppings = toppings; }
} 