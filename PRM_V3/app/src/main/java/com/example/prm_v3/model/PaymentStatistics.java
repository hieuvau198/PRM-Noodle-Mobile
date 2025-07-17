package com.example.prm_v3.model;

import com.google.gson.annotations.SerializedName;

public class PaymentStatistics {
    @SerializedName("totalPayments")
    private int totalPayments;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("pendingCount")
    private int pendingCount;

    @SerializedName("paidCount")
    private int paidCount;

    @SerializedName("failedCount")
    private int failedCount;

    @SerializedName("pendingAmount")
    private double pendingAmount;

    @SerializedName("paidAmount")
    private double paidAmount;

    // Constructors
    public PaymentStatistics() {}

    // Getters and Setters
    public int getTotalPayments() { return totalPayments; }
    public void setTotalPayments(int totalPayments) { this.totalPayments = totalPayments; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public int getPendingCount() { return pendingCount; }
    public void setPendingCount(int pendingCount) { this.pendingCount = pendingCount; }

    public int getPaidCount() { return paidCount; }
    public void setPaidCount(int paidCount) { this.paidCount = paidCount; }

    public int getFailedCount() { return failedCount; }
    public void setFailedCount(int failedCount) { this.failedCount = failedCount; }

    public double getPendingAmount() { return pendingAmount; }
    public void setPendingAmount(double pendingAmount) { this.pendingAmount = pendingAmount; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    // Helper methods
    public String getFormattedTotalAmount() {
        return String.format("%.0f₫", totalAmount);
    }

    public String getFormattedPendingAmount() {
        return String.format("%.0f₫", pendingAmount);
    }

    public String getFormattedPaidAmount() {
        return String.format("%.0f₫", paidAmount);
    }

    public double getSuccessRate() {
        if (totalPayments == 0) return 0;
        return (double) paidCount / totalPayments * 100;
    }

    public double getFailureRate() {
        if (totalPayments == 0) return 0;
        return (double) failedCount / totalPayments * 100;
    }

    public String getFormattedSuccessRate() {
        return String.format("%.1f%%", getSuccessRate());
    }

    public String getFormattedFailureRate() {
        return String.format("%.1f%%", getFailureRate());
    }
}