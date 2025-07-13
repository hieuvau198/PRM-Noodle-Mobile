package com.example.prm_v3.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateOrderRequest {
    @SerializedName("userId")
    private int userId;

    @SerializedName("deliveryAddress")
    private String deliveryAddress;

    @SerializedName("notes")
    private String notes;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("orderItems")
    private List<CreateOrderItem> orderItems;

    @SerializedName("orderCombos")
    private List<CreateOrderCombo> orderCombos;

    // Constructors
    public CreateOrderRequest() {}

    public CreateOrderRequest(int userId, String deliveryAddress, String notes, String paymentMethod) {
        this.userId = userId;
        this.deliveryAddress = deliveryAddress;
        this.notes = notes;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public List<CreateOrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<CreateOrderItem> orderItems) { this.orderItems = orderItems; }

    public List<CreateOrderCombo> getOrderCombos() { return orderCombos; }
    public void setOrderCombos(List<CreateOrderCombo> orderCombos) { this.orderCombos = orderCombos; }

    // Inner classes for order items and combos
    public static class CreateOrderItem {
        @SerializedName("productId")
        private int productId;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName("toppings")
        private List<CreateOrderItemTopping> toppings;

        public CreateOrderItem() {}

        public CreateOrderItem(int productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        // Getters and Setters
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public List<CreateOrderItemTopping> getToppings() { return toppings; }
        public void setToppings(List<CreateOrderItemTopping> toppings) { this.toppings = toppings; }
    }

    public static class CreateOrderCombo {
        @SerializedName("comboId")
        private int comboId;

        @SerializedName("quantity")
        private int quantity;

        public CreateOrderCombo() {}

        public CreateOrderCombo(int comboId, int quantity) {
            this.comboId = comboId;
            this.quantity = quantity;
        }

        // Getters and Setters
        public int getComboId() { return comboId; }
        public void setComboId(int comboId) { this.comboId = comboId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class CreateOrderItemTopping {
        @SerializedName("toppingId")
        private int toppingId;

        @SerializedName("quantity")
        private int quantity;

        public CreateOrderItemTopping() {}

        public CreateOrderItemTopping(int toppingId, int quantity) {
            this.toppingId = toppingId;
            this.quantity = quantity;
        }

        // Getters and Setters
        public int getToppingId() { return toppingId; }
        public void setToppingId(int toppingId) { this.toppingId = toppingId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}