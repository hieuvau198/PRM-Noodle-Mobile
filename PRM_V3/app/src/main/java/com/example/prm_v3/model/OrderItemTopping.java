package com.example.prm_v3.model;

import com.google.gson.annotations.SerializedName;

public class OrderItemTopping {
    @SerializedName("orderItemToppingId")
    private int orderItemToppingId;

    @SerializedName("toppingId")
    private int toppingId;

    @SerializedName("toppingName")
    private String toppingName;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("unitPrice")
    private double unitPrice;

    @SerializedName("subtotal")
    private double subtotal;

    @SerializedName("category")
    private String category;

    @SerializedName("description")
    private String description;

    // Constructors
    public OrderItemTopping() {}

    public OrderItemTopping(int toppingId, String toppingName, int quantity, double unitPrice) {
        this.toppingId = toppingId;
        this.toppingName = toppingName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    // Getters and Setters
    public int getOrderItemToppingId() { return orderItemToppingId; }
    public void setOrderItemToppingId(int orderItemToppingId) { this.orderItemToppingId = orderItemToppingId; }

    public int getToppingId() { return toppingId; }
    public void setToppingId(int toppingId) { this.toppingId = toppingId; }

    public String getToppingName() { return toppingName; }
    public void setToppingName(String toppingName) { this.toppingName = toppingName; }

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

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

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

    public boolean hasCategory() {
        return category != null && !category.trim().isEmpty() && !category.equals("null");
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

    public String getDisplayName() {
        if (quantity > 1) {
            return String.format("%s (x%d)", toppingName, quantity);
        }
        return toppingName;
    }

    public String getCategoryDisplayText() {
        if (!hasCategory()) return "Khác";

        switch (category.toLowerCase()) {
            case "sauce": return "Nước chấm";
            case "extra": return "Thêm";
            case "size": return "Kích thước";
            case "spice": return "Gia vị";
            case "topping": return "Topping";
            default: return category;
        }
    }

    @Override
    public String toString() {
        return String.format("OrderItemTopping{id=%d, name='%s', quantity=%d, price=%.0f₫}",
                orderItemToppingId, toppingName, quantity, subtotal);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        OrderItemTopping that = (OrderItemTopping) obj;
        return toppingId == that.toppingId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(toppingId);
    }
}