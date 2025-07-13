package com.example.prm_v3.model;

import com.google.gson.annotations.SerializedName;

public class OrderCombo {
    @SerializedName("orderComboId")
    private int orderComboId;

    @SerializedName("comboId")
    private int comboId;

    @SerializedName("comboName")
    private String comboName;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("unitPrice")
    private double unitPrice;

    @SerializedName("subtotal")
    private double subtotal;

    @SerializedName("description")
    private String description;

    @SerializedName("imageUrl")
    private String imageUrl;

    // Constructors
    public OrderCombo() {}

    public OrderCombo(int comboId, String comboName, int quantity, double unitPrice) {
        this.comboId = comboId;
        this.comboName = comboName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    // Getters and Setters
    public int getOrderComboId() { return orderComboId; }
    public void setOrderComboId(int orderComboId) { this.orderComboId = orderComboId; }

    public int getComboId() { return comboId; }
    public void setComboId(int comboId) { this.comboId = comboId; }

    public String getComboName() { return comboName; }
    public void setComboName(String comboName) { this.comboName = comboName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Helper methods
    public String getFormattedUnitPrice() {
        return String.format("%.0f₫", unitPrice);
    }

    public String getFormattedSubtotal() {
        return String.format("%.0f₫", subtotal);
    }

    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty() && !description.equals("null");
    }

    public boolean hasImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty() && !imageUrl.equals("null");
    }

    private void calculateSubtotal() {
        this.subtotal = quantity * unitPrice;
    }

    public void increaseQuantity() {
        this.quantity++;
        calculateSubtotal();
    }

    public void decreaseQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
            calculateSubtotal();
        }
    }

    public String getQuantityText() {
        return quantity > 1 ? String.format("x%d", quantity) : "";
    }

    @Override
    public String toString() {
        return String.format("OrderCombo{id=%d, name='%s', quantity=%d, price=%.0f₫}",
                orderComboId, comboName, quantity, subtotal);
    }
}