package com.example.prm_v3.model;

import com.google.gson.annotations.SerializedName;

public class Combo {
    @SerializedName("comboId")
    private int comboId;

    @SerializedName("comboName")
    private String comboName;

    @SerializedName("description")
    private String description;

    @SerializedName("comboPrice")
    private double comboPrice;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("isAvailable")
    private boolean isAvailable;

    @SerializedName("category")
    private String category;

    @SerializedName("validFrom")
    private String validFrom;

    @SerializedName("validTo")
    private String validTo;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Constructors
    public Combo() {}

    public Combo(String comboName, double comboPrice) {
        this.comboName = comboName;
        this.comboPrice = comboPrice;
        this.isAvailable = true;
    }

    public Combo(int comboId, String comboName, String description, double comboPrice,
                 String imageUrl, boolean isAvailable) {
        this.comboId = comboId;
        this.comboName = comboName;
        this.description = description;
        this.comboPrice = comboPrice;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public int getComboId() { return comboId; }
    public void setComboId(int comboId) { this.comboId = comboId; }

    public String getComboName() { return comboName; }
    public void setComboName(String comboName) { this.comboName = comboName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getComboPrice() { return comboPrice; }
    public void setComboPrice(double comboPrice) { this.comboPrice = comboPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getValidFrom() { return validFrom; }
    public void setValidFrom(String validFrom) { this.validFrom = validFrom; }

    public String getValidTo() { return validTo; }
    public void setValidTo(String validTo) { this.validTo = validTo; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public String getFormattedPrice() {
        return String.format("%.0f₫", comboPrice);
    }

    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty() && !description.equals("null");
    }

    public boolean hasImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty() && !imageUrl.equals("null");
    }

    public boolean hasCategory() {
        return category != null && !category.trim().isEmpty() && !category.equals("null");
    }

    public String getAvailabilityText() {
        return isAvailable ? "Có sẵn" : "Hết hàng";
    }

    public String getCategoryDisplayText() {
        if (!hasCategory()) return "Combo thường";

        switch (category.toLowerCase()) {
            case "lunch": return "Combo trưa";
            case "dinner": return "Combo tối";
            case "breakfast": return "Combo sáng";
            case "family": return "Combo gia đình";
            case "couple": return "Combo đôi";
            case "student": return "Combo sinh viên";
            case "premium": return "Combo cao cấp";
            default: return category;
        }
    }

    public boolean isValidToday() {
        // Simple implementation - you might want to use proper date parsing
        return isAvailable;
    }

    @Override
    public String toString() {
        return String.format("Combo{id=%d, name='%s', price=%.0f₫, available=%s}",
                comboId, comboName, comboPrice, isAvailable);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Combo combo = (Combo) obj;
        return comboId == combo.comboId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(comboId);
    }
}