package com.example.prm_v3.model;

import com.google.gson.annotations.SerializedName;

public class Topping {
    @SerializedName("toppingId")
    private int toppingId;

    @SerializedName("toppingName")
    private String toppingName;

    @SerializedName("price")
    private double price;

    @SerializedName("description")
    private String description;

    @SerializedName("isAvailable")
    private boolean isAvailable;

    @SerializedName("category")
    private String category;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Constructors
    public Topping() {}

    public Topping(String toppingName, double price) {
        this.toppingName = toppingName;
        this.price = price;
        this.isAvailable = true;
    }

    public Topping(int toppingId, String toppingName, double price, String description, boolean isAvailable) {
        this.toppingId = toppingId;
        this.toppingName = toppingName;
        this.price = price;
        this.description = description;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public int getToppingId() { return toppingId; }
    public void setToppingId(int toppingId) { this.toppingId = toppingId; }

    public String getToppingName() { return toppingName; }
    public void setToppingName(String toppingName) { this.toppingName = toppingName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public String getFormattedPrice() {
        return String.format("%.0f₫", price);
    }

    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty() && !description.equals("null");
    }

    public boolean hasCategory() {
        return category != null && !category.trim().isEmpty() && !category.equals("null");
    }

    public String getCategoryDisplayText() {
        if (!hasCategory()) return "Khác";

        switch (category.toLowerCase()) {
            case "sauce": return "Nước chấm";
            case "extra": return "Thêm";
            case "size": return "Kích thước";
            case "spice": return "Gia vị";
            case "topping": return "Topping";
            default: return category;
        }
    }

    public String getAvailabilityText() {
        return isAvailable ? "Có sẵn" : "Hết hàng";
    }

    @Override
    public String toString() {
        return String.format("Topping{id=%d, name='%s', price=%.0f₫, available=%s}",
                toppingId, toppingName, price, isAvailable);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Topping topping = (Topping) obj;
        return toppingId == topping.toppingId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(toppingId);
    }
}