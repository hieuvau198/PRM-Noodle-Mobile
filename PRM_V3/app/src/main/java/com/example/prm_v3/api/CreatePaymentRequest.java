package com.example.prm_v3.api;

import com.google.gson.annotations.SerializedName;
import com.example.prm_v3.model.Order;

public class CreatePaymentRequest {
    @SerializedName("orderId")
    private int orderId;

    @SerializedName("customerUserId")
    private int customerUserId;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("staffUserId")
    private int staffUserId;

    @SerializedName("staffName")
    private String staffName;

    @SerializedName("paymentAmount")
    private double paymentAmount;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("paymentStatus")
    private String paymentStatus;

    @SerializedName("transactionReference")
    private String transactionReference;

    @SerializedName("paymentDate")
    private String paymentDate;

    // Constructors
    public CreatePaymentRequest() {}

    public CreatePaymentRequest(int orderId, int customerUserId, String customerName, 
                               int staffUserId, String staffName, double paymentAmount, 
                               String paymentMethod, String paymentStatus, 
                               String transactionReference, String paymentDate) {
        this.orderId = orderId;
        this.customerUserId = customerUserId;
        this.customerName = customerName;
        this.staffUserId = staffUserId;
        this.staffName = staffName;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionReference = transactionReference;
        this.paymentDate = paymentDate;
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCustomerUserId() { return customerUserId; }
    public void setCustomerUserId(int customerUserId) { this.customerUserId = customerUserId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getStaffUserId() { return staffUserId; }
    public void setStaffUserId(int staffUserId) { this.staffUserId = staffUserId; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public double getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(double paymentAmount) { this.paymentAmount = paymentAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    /**
     * Factory method to create payment request from Order
     */
    public static CreatePaymentRequest fromOrder(Order order, int staffUserId, String staffName) {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setOrderId(order.getOrderId());
        request.setCustomerUserId(order.getUserId());
        request.setCustomerName(order.getUserName());
        request.setStaffUserId(staffUserId);
        request.setStaffName(staffName);
        request.setPaymentAmount(order.getTotalAmount());
        request.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod() : "cash"); // Default to cash
        request.setPaymentStatus("pending"); // Default status
        request.setTransactionReference("TXN_" + order.getOrderId() + "_" + System.currentTimeMillis());
        request.setPaymentDate(getCurrentDateTime());
        
        return request;
    }

    private static String getCurrentDateTime() {
        // Format: "2025-07-18T07:35:28.175Z"
        return java.time.Instant.now().toString();
    }
}
