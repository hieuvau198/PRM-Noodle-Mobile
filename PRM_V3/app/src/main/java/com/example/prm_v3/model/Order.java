package com.example.prm_v3.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

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

    @SerializedName("deliveryAddress")
    private String deliveryAddress;

    @SerializedName("notes")
    private String notes;

    @SerializedName("orderDate")
    private String orderDate;

    @SerializedName("confirmedAt")
    private String confirmedAt;

    @SerializedName("completedAt")
    private String completedAt;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // ADD THIS FIELD - từ API response
    @SerializedName("totalItems")
    private int totalItems;

    @SerializedName("orderItems")
    private List<OrderItem> orderItems = new ArrayList<>();

    @SerializedName("orderCombos")
    private List<OrderCombo> orderCombos = new ArrayList<>();

    // Constructors
    public Order() {}

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(String confirmedAt) { this.confirmedAt = confirmedAt; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // ADD GETTERS/SETTERS for totalItems
    public int getTotalItemsFromAPI() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems != null ? orderItems : new ArrayList<>();
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
    }

    public List<OrderCombo> getOrderCombos() {
        return orderCombos != null ? orderCombos : new ArrayList<>();
    }

    public void setOrderCombos(List<OrderCombo> orderCombos) {
        this.orderCombos = orderCombos != null ? orderCombos : new ArrayList<>();
    }

    // ========== STATUS HELPER METHODS ==========

    public String getStatusDisplayText() {
        if (orderStatus == null) return "Không xác định";

        switch (orderStatus.toLowerCase()) {
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "preparing": return "Đang chuẩn bị";
            case "ready": return "Sẵn sàng giao";
            case "delivered": return "Đang vận chuyển";
            case "completed": return "Hoàn thành";
            case "cancelled": return "Đã hủy";
            default: return "Không xác định";
        }
    }

    // ========== PAYMENT HELPER METHODS ==========

    public String getPaymentMethodText() {
        if (paymentMethod == null) return "Không xác định";

        switch (paymentMethod.toLowerCase()) {
            case "cash": return "Tiền mặt";
            case "digital_wallet": return "Ví điện tử";
            case "credit_card": return "Thẻ tín dụng";
            case "debit_card": return "Thẻ ghi nợ";
            case "bank_transfer": return "Chuyển khoản";
            default: return paymentMethod;
        }
    }

    public String getPaymentStatusText() {
        if (paymentStatus == null) return "Không xác định";

        switch (paymentStatus.toLowerCase()) {
            case "pending": return "Chờ thanh toán";
            case "processing": return "Đang xử lý";
            case "complete": return "Đã thanh toán";
            case "paid": return "Đã thanh toán";
            case "failed": return "Thất bại";
            case "refunded": return "Đã hoàn tiền";
            case "cancelled": return "Đã hủy";
            default: return paymentStatus;
        }
    }

    // ========== FORMATTING METHODS ==========

    public String getFormattedAmount() {
        return String.format("%.0f₫", totalAmount);
    }

    public String getFormattedDate() {
        return formatDate(orderDate);
    }

    public String getFormattedConfirmedDate() {
        return formatDate(confirmedAt);
    }

    public String getFormattedCompletedDate() {
        return formatDate(completedAt);
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty() || dateString.equals("null")) {
            return "Chưa có";
        }
        try {
            // Handle both ISO format and simple format
            return dateString.replace("T", " ").substring(0,
                    Math.min(16, dateString.replace("T", " ").length()));
        } catch (Exception e) {
            return dateString;
        }
    }

    // ========== ITEM COUNT METHODS ==========

    /**
     * Get total items - tries API field first, then calculates
     */
    public int getTotalItems() {
        // Ưu tiên totalItems từ API response trước
        if (totalItems > 0) {
            android.util.Log.d("Order", "Using totalItems from API: " + totalItems);
            return totalItems;
        }

        // Fallback to manual calculation
        int itemCount = 0;

        android.util.Log.d("Order", "Calculating total items for order #" + orderId);

        if (orderItems != null) {
            android.util.Log.d("Order", "OrderItems size: " + orderItems.size());
            for (OrderItem item : orderItems) {
                if (item != null) {
                    int quantity = item.getQuantity();
                    android.util.Log.d("Order", "Item: " + item.getProductName() + ", Quantity: " + quantity);
                    itemCount += quantity;
                }
            }
        } else {
            android.util.Log.d("Order", "OrderItems is null");
        }

        if (orderCombos != null) {
            android.util.Log.d("Order", "OrderCombos size: " + orderCombos.size());
            for (OrderCombo combo : orderCombos) {
                if (combo != null) {
                    int quantity = combo.getQuantity();
                    android.util.Log.d("Order", "Combo: " + combo.getComboName() + ", Quantity: " + quantity);
                    itemCount += quantity;
                }
            }
        } else {
            android.util.Log.d("Order", "OrderCombos is null");
        }

        android.util.Log.d("Order", "Total items calculated: " + itemCount);
        return itemCount;
    }

    public String getItemSummary() {
        int itemCount = orderItems != null ? orderItems.size() : 0;
        int comboCount = orderCombos != null ? orderCombos.size() : 0;

        if (itemCount > 0 && comboCount > 0) {
            return String.format("Tổng cộng: %d món, %d combo", itemCount, comboCount);
        } else if (itemCount > 0) {
            return String.format("Tổng cộng: %d món", itemCount);
        } else if (comboCount > 0) {
            return String.format("Tổng cộng: %d combo", comboCount);
        } else {
            return "Không có món nào";
        }
    }

    // ========== VALIDATION METHODS ==========

    public boolean hasNotes() {
        return notes != null && !notes.trim().isEmpty() && !notes.equals("null");
    }

    public boolean hasDeliveryAddress() {
        return deliveryAddress != null && !deliveryAddress.trim().isEmpty() && !deliveryAddress.equals("null");
    }

    public boolean isConfirmed() {
        return confirmedAt != null && !confirmedAt.isEmpty() && !confirmedAt.equals("null");
    }

    public boolean isCompleted() {
        return completedAt != null && !completedAt.isEmpty() && !completedAt.equals("null");
    }

    // ========== STATUS CHECK METHODS ==========

    public boolean isPending() {
        return "pending".equalsIgnoreCase(orderStatus);
    }

    public boolean isConfirmedStatus() {
        return "confirmed".equalsIgnoreCase(orderStatus);
    }

    public boolean isPreparing() {
        return "preparing".equalsIgnoreCase(orderStatus);
    }

    public boolean isReady() {
        return "ready".equalsIgnoreCase(orderStatus);
    }

    public boolean isDelivered() {
        return "delivered".equalsIgnoreCase(orderStatus);
    }

    public boolean isCompletedStatus() {
        return "completed".equalsIgnoreCase(orderStatus);
    }

    public boolean isCancelled() {
        return "cancelled".equalsIgnoreCase(orderStatus);
    }

    public boolean isFinalStatus() {
        return isCompletedStatus() || isCancelled();
    }

    public boolean canBeUpdated() {
        return !isFinalStatus();
    }

    public boolean canBeCancelled() {
        return isPending() || isConfirmedStatus() || isPreparing();
    }

    // ========== PAYMENT STATUS CHECKS ==========

    public boolean isPaymentPending() {
        return "pending".equalsIgnoreCase(paymentStatus);
    }

    public boolean isPaymentComplete() {
        return "complete".equalsIgnoreCase(paymentStatus) || "paid".equalsIgnoreCase(paymentStatus);
    }

    public boolean isPaymentFailed() {
        return "failed".equalsIgnoreCase(paymentStatus);
    }

    // ========== UTILITY METHODS ==========

    public String getOrderSummary() {
        return String.format("Đơn hàng #%d - %s - %s",
                orderId,
                getStatusDisplayText(),
                getFormattedAmount());
    }

    public String getCustomerInfo() {
        return String.format("Khách hàng: %s (ID: %d)",
                userName != null ? userName : "N/A",
                userId);
    }

    public boolean hasItems() {
        return (orderItems != null && !orderItems.isEmpty()) ||
                (orderCombos != null && !orderCombos.isEmpty());
    }

    public int getUniqueItemsCount() {
        int count = 0;
        if (orderItems != null) count += orderItems.size();
        if (orderCombos != null) count += orderCombos.size();
        return count;
    }

    public double calculateSubtotal() {
        double subtotal = 0.0;

        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                subtotal += item.getSubtotal();
            }
        }

        if (orderCombos != null) {
            for (OrderCombo combo : orderCombos) {
                subtotal += combo.getSubtotal();
            }
        }

        return subtotal;
    }

    // ========== TOSTRING & EQUALS ==========

    @Override
    public String toString() {
        return String.format("Order{id=%d, status='%s', customer='%s', amount=%.0f₫}",
                orderId, orderStatus, userName, totalAmount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Order order = (Order) obj;
        return orderId == order.orderId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(orderId);
    }
}