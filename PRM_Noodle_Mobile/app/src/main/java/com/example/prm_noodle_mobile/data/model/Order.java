package com.example.prm_noodle_mobile.data.model;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("orderId")
    private int orderId;

    @SerializedName("userId")
    private int userId;

    @SerializedName("userName")
    private String userName;

    @SerializedName("orderStatus")
    private String orderStatus;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("paymentStatus")
    private String paymentStatus;

    @SerializedName("orderDate")
    private String orderDate;

    @SerializedName("totalItems")
    private int totalItems;

    // Các trường cho tạo đơn hàng mới
    @SerializedName("deliveryAddress")
    private String deliveryAddress;
    @SerializedName("notes")
    private String notes;
    @SerializedName("orderItems")
    private java.util.List<OrderItem> orderItems;
    @SerializedName("orderCombos")
    private java.util.List<OrderCombo> orderCombos;

    // Getters
    public int getOrderId() { return orderId; }
    public String getOrderDate() { return orderDate; }
    public String getOrderStatus() { return orderStatus; }
    public int getTotalItems() { return totalItems; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    
    // Optional getters if needed
    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getPaymentStatus() { return paymentStatus; }

    public String getFormattedStatus() {
        if (orderStatus == null) return "";
        switch (orderStatus.toLowerCase()) {
            case "pending": return "Đang xử lý";
            case "confirmed": return "Đã xác nhận";
            case "delivered": return "Đã giao";
            case "cancelled": return "Đã hủy";
            default: return orderStatus;
        }
    }

    public String getFormattedPaymentMethod() {
        if (paymentMethod == null) return "";
        switch (paymentMethod.toLowerCase()) {
            case "cash": return "Tiền mặt";
            case "card": return "Thẻ";
            case "digital_wallet": return "Ví điện tử";
            default: return paymentMethod;
        }
    }

    // Constructor dùng cho tạo đơn hàng mới
    public Order(int userId, String deliveryAddress, String notes, String paymentMethod, java.util.List<OrderItem> orderItems, java.util.List<OrderCombo> orderCombos) {
        this.userId = userId;
        this.deliveryAddress = deliveryAddress;
        this.notes = notes;
        this.paymentMethod = paymentMethod;
        this.orderItems = orderItems;
        this.orderCombos = orderCombos;
    }
} 