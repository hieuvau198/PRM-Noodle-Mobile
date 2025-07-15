package com.example.prm_noodle_mobile.data.model.payment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payment {
    @SerializedName("orderId")
    private int orderId;

    @SerializedName("customerUserId")
    private int customerId;

    @SerializedName("customerName")
    private String customerName;

    // Ẩn các field của staff
    @Expose(serialize = false)
    private Integer staffUserId;

    @Expose(serialize = false)
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

    public Payment(int orderId, int customerId, String customerName, double paymentAmount, String paymentMethod) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
}
