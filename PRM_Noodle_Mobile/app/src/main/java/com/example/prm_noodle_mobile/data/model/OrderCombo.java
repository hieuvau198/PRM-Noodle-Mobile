package com.example.prm_noodle_mobile.data.model;

public class OrderCombo {
    private int comboId;
    private int quantity;

    public OrderCombo(int comboId, int quantity) {
        this.comboId = comboId;
        this.quantity = quantity;
    }

    public int getComboId() { return comboId; }
    public void setComboId(int comboId) { this.comboId = comboId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
} 