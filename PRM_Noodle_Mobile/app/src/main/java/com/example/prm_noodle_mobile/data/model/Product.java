package com.example.prm_noodle_mobile.data.model;
// mobile app model will contain data to interact and match with APIs endpoint
// to create it, based on BE DTOs, or API required fields
import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private int productId;
    private String productName;
    private String description;
    private int basePrice;
    private String imageUrl;
    private Boolean isAvailable;
    private String spiceLevel;
    private String createdAt;
    private String updatedAt;

    public Product() {
        // Default constructor
    }

    public Product(int productId, String productName, String description, int basePrice, String imageUrl, Boolean isAvailable, String spiceLevel, String createdAt, String updatedAt) {
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

    public int getBasePrice() {
        return basePrice;
    }
    public void setBasePrice(int basePrice) {
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

    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Parcelable implementation
    protected Product(Parcel in) {
        productId = in.readInt();
        productName = in.readString();
        description = in.readString();
        basePrice = in.readInt();
        imageUrl = in.readString();
        isAvailable = in.readByte() != 0;
        spiceLevel = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(productId);
        dest.writeString(productName);
        dest.writeString(description);
        dest.writeInt(basePrice);
        dest.writeString(imageUrl);
        dest.writeByte((byte) (isAvailable ? 1 : 0));
        dest.writeString(spiceLevel);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
