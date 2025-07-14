package com.example.prm_v3.api;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    @SerializedName("username")
    private String username;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("address")
    private String address;

    // Constructors
    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String username, String fullName, String phone, String address) {
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // Validation methods
    public boolean isValid() {
        return (username != null && !username.trim().isEmpty()) ||
                (fullName != null && !fullName.trim().isEmpty()) ||
                (phone != null && !phone.trim().isEmpty()) ||
                (address != null && !address.trim().isEmpty());
    }

    public boolean hasUsername() {
        return username != null && !username.trim().isEmpty();
    }

    public boolean hasFullName() {
        return fullName != null && !fullName.trim().isEmpty();
    }

    public boolean hasPhone() {
        return phone != null && !phone.trim().isEmpty();
    }

    public boolean hasAddress() {
        return address != null && !address.trim().isEmpty();
    }
}