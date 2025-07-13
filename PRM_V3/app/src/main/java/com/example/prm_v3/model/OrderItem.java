package com.example.prm_v3.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class OrderItem {
    @SerializedName("orderItemId")
    private int orderItemId;

    @SerializedName("productId")
    private int productId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("unitPrice")
    private double unitPrice;

    @SerializedName("subtotal")
    private double subtotal;

    @SerializedName("toppings")
    private List<OrderItemTopping> toppings = new ArrayList<>();

    // Constructors
    public OrderItem() {}

    public OrderItem(int productId, String productName, int quantity, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    // Getters and Setters
    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() {
        Log.d("OrderItem", "getQuantity() called for " + productName + ": " + quantity);
        return quantity;
    }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public List<OrderItemTopping> getToppings() { return toppings; }
    public void setToppings(List<OrderItemTopping> toppings) { this.toppings = toppings; }

    // Helper methods
    public String getFormattedUnitPrice() {
        return String.format("%.0f₫", unitPrice);
    }

    public String getFormattedSubtotal() {
        return String.format("%.0f₫", subtotal);
    }

    public boolean hasToppings() {
        return toppings != null && !toppings.isEmpty();
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}