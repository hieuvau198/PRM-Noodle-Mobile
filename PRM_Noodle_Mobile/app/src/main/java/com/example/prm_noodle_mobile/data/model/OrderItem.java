package com.example.prm_noodle_mobile.data.model;

import java.util.List;

public class OrderItem {
    private int productId;
    private int quantity;
    private List<ToppingOrder> toppings;



    // Thêm cho hiển thị
    private String productName;
    private double unitPrice;
    private String imageUrl;

    public OrderItem(int productId, int quantity, List<ToppingOrder> toppings) {
        this.productId = productId;
        this.quantity = quantity;
        this.toppings = toppings;
    }

    // Constructor đầy đủ khi thêm sản phẩm vào Cart
    public OrderItem(int productId, String productName, double unitPrice, String imageUrl, int quantity, List<ToppingOrder> toppings) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.toppings = toppings;
    }

    // Getters & setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public List<ToppingOrder> getToppings() { return toppings; }
    public void setToppings(List<ToppingOrder> toppings) { this.toppings = toppings; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
