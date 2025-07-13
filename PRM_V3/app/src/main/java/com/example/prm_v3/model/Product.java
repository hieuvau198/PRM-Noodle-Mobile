package com.example.prm_v3.model;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("productId")
    private int productId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("description")
    private String description;

    @SerializedName("basePrice")
    private double basePrice;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("isAvailable")
    private boolean isAvailable;

    @SerializedName("spiceLevel")
    private String spiceLevel;

    @SerializedName("category")
    private String category;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Constructors
    public Product() {}

    public Product(String productName, double basePrice) {
        this.productName = productName;
        this.basePrice = basePrice;
        this.isAvailable = true;
    }

    public Product(int productId, String productName, String description, double basePrice,
                   String imageUrl, boolean isAvailable, String spiceLevel) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.basePrice = basePrice;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.spiceLevel = spiceLevel;
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getSpiceLevel() { return spiceLevel; }
    public void setSpiceLevel(String spiceLevel) { this.spiceLevel = spiceLevel; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public String getFormattedPrice() {
        return String.format("%.0f₫", basePrice);
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

    public String getSpiceLevelDisplayText() {
        if (spiceLevel == null || spiceLevel.trim().isEmpty()) return "Không cay";

        switch (spiceLevel.toLowerCase()) {
            case "none": return "Không cay";
            case "mild": return "Ít cay";
            case "medium": return "Vừa cay";
            case "hot": return "Cay";
            case "very_hot": return "Rất cay";
            default: return spiceLevel;
        }
    }

    public String getAvailabilityText() {
        return isAvailable ? "Có sẵn" : "Hết hàng";
    }

    public String getCategoryDisplayText() {
        if (!hasCategory()) return "Khác";

        switch (category.toLowerCase()) {
            case "main": return "Món chính";
            case "appetizer": return "Khai vị";
            case "dessert": return "Tráng miệng";
            case "drink": return "Đồ uống";
            case "soup": return "Súp";
            case "noodle": return "Bún - Phở";
            case "rice": return "Cơm";
            default: return category;
        }
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', price=%.0f₫, available=%s}",
                productId, productName, basePrice, isAvailable);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Product product = (Product) obj;
        return productId == product.productId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(productId);
    }
}