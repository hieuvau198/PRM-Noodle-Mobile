package com.example.prm_noodle_mobile.data.model;

import java.util.List;

public class Order {
    private int userId;
    private String deliveryAddress;
    private String notes;
    private String paymentMethod;
    private List<OrderItem> orderItems;
    private List<OrderCombo> orderCombos;

    public Order(int userId, String deliveryAddress, String notes, String paymentMethod, List<OrderItem> orderItems, List<OrderCombo> orderCombos) {
        this.userId = userId;
        this.deliveryAddress = deliveryAddress;
        this.notes = notes;
        this.paymentMethod = paymentMethod;
        this.orderItems = orderItems;
        this.orderCombos = orderCombos;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
    public List<OrderCombo> getOrderCombos() { return orderCombos; }
    public void setOrderCombos(List<OrderCombo> orderCombos) { this.orderCombos = orderCombos; }
} 