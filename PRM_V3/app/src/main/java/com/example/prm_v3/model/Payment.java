package com.example.prm_v3.model;

import com.google.gson.annotations.SerializedName;

public class Payment {
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
    @SerializedName("paymentId")
    private int paymentId;
    @SerializedName("isDeleted")
    private boolean isDeleted;
    @SerializedName("deletionReason")
    private String deletionReason;
    @SerializedName("processedAt")
    private String processedAt;
    @SerializedName("completedAt")
    private String completedAt;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updatedAt")
    private String updatedAt;

    public Payment() {}

    // Getters and setters
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
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public String getDeletionReason() { return deletionReason; }
    public void setDeletionReason(String deletionReason) { this.deletionReason = deletionReason; }
    public String getProcessedAt() { return processedAt; }
    public void setProcessedAt(String processedAt) { this.processedAt = processedAt; }
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    @Override
    public String toString() {
        return String.format(
                "Payment{orderId=%d, customerUserId=%d, customerName='%s', " +
                        "paymentAmount=%.2f, paymentMethod='%s', paymentStatus='%s', " +
                        "transactionReference='%s'}",
                orderId, customerUserId, customerName, paymentAmount,
                paymentMethod, paymentStatus, transactionReference
        );
    }

    // Add validation method
    public boolean isValid() {
        return orderId > 0 &&
                customerUserId > 0 &&
                customerName != null && !customerName.trim().isEmpty() &&
                paymentAmount > 0 &&
                paymentMethod != null && !paymentMethod.trim().isEmpty() &&
                paymentStatus != null && !paymentStatus.trim().isEmpty();
    }

    // Add method to get validation errors
    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();

        if (orderId <= 0) errors.append("OrderId must be > 0; ");
        if (customerUserId <= 0) errors.append("CustomerUserId must be > 0; ");
        if (customerName == null || customerName.trim().isEmpty())
            errors.append("CustomerName is required; ");
        if (paymentAmount <= 0) errors.append("PaymentAmount must be > 0; ");
        if (paymentMethod == null || paymentMethod.trim().isEmpty())
            errors.append("PaymentMethod is required; ");
        if (paymentStatus == null || paymentStatus.trim().isEmpty())
            errors.append("PaymentStatus is required; ");

        return errors.toString();
    }
} 