package com.example.prm_noodle_mobile.data.model;

public class ToppingOrder {
    private int toppingId;
    private int quantity;

    public ToppingOrder(int toppingId, int quantity) {
        this.toppingId = toppingId;
        this.quantity = quantity;
    }

    public int getToppingId() { return toppingId; }
    public void setToppingId(int toppingId) { this.toppingId = toppingId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
} 