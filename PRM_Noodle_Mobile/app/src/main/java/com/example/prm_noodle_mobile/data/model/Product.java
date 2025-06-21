package com.example.prm_noodle_mobile.data.model;
// mobile app model will contain data to interact and match with APIs endpoint
// to create it, based on BE DTOs, or API required fields
import java.util.Date;

public class Product {
    private int productId;
    private String productName;
    private String description;
    private double basePrice;
    private String imageUrl;
    private Boolean isAvailable;
    private String spiceLevel;
    private Date createdAt;
    private Date updatedAt;

    public Product() {
        // Default constructor
    }

    public Product(int productId, String productName, String description, double basePrice, String imageUrl, Boolean isAvailable, String spiceLevel, Date createdAt, Date updatedAt) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.basePrice = basePrice;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.spiceLevel = spiceLevel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters

    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public double getBasePrice() {
        return basePrice;
    }
    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getSpiceLevel() {
        return spiceLevel;
    }
    public void setSpiceLevel(String spiceLevel) {
        this.spiceLevel = spiceLevel;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
