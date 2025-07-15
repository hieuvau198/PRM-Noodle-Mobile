package com.example.prm_noodle_mobile.data.model.payment;

public class CreatePaymentRequest {
    private int orderId;
    private double amount;
    private String description;
    private int customerId;
    private String customerName;

    public CreatePaymentRequest(int orderId, double amount, String description, int customerId, String customerName) {
        this.orderId = orderId;
        this.amount = amount;
        this.description = description;
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
