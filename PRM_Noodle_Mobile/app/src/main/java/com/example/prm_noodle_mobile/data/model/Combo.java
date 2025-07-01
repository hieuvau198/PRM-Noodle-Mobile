package com.example.prm_noodle_mobile.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Combo implements Parcelable {
    private int comboId;
    private String comboName;
    private String description;
    private int price;
    private String imageUrl;
    private Boolean isAvailable;
    private String createdAt;
    private String updatedAt;

    public Combo() {}

    public Combo(int comboId, String comboName, String description, int price, String imageUrl, Boolean isAvailable, String createdAt, String updatedAt) {
        this.comboId = comboId;
        this.comboName = comboName;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getComboId() { return comboId; }
    public void setComboId(int comboId) { this.comboId = comboId; }

    public String getComboName() { return comboName; }
    public void setComboName(String comboName) { this.comboName = comboName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Parcelable implementation
    protected Combo(Parcel in) {
        comboId = in.readInt();
        comboName = in.readString();
        description = in.readString();
        price = in.readInt();
        imageUrl = in.readString();
        isAvailable = in.readByte() != 0;
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(comboId);
        dest.writeString(comboName);
        dest.writeString(description);
        dest.writeInt(price);
        dest.writeString(imageUrl);
        dest.writeByte((byte) (isAvailable ? 1 : 0));
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Combo> CREATOR = new Creator<Combo>() {
        @Override
        public Combo createFromParcel(Parcel in) {
            return new Combo(in);
        }

        @Override
        public Combo[] newArray(int size) {
            return new Combo[size];
        }
    };
} 