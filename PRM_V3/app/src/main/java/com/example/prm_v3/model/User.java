package com.example.prm_v3.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("userId")
    private int userId;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("address")
    private String address;

    @SerializedName("role")
    private String role;

    @SerializedName("isActive")
    private Boolean isActive;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Constructors
    public User() {}

    public User(String username, String email, String fullName) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public String getDisplayRole() {
        if (role == null) return "Người dùng";
        switch (role.toLowerCase()) {
            case "admin": return "Quản trị viên";
            case "staff": return "Nhân viên";
            case "manager": return "Quản lý";
            case "customer": return "Khách hàng";
            default: return role;
        }
    }

    public String getFormattedCreatedDate() {
        return formatDate(createdAt);
    }

    public String getFormattedUpdatedDate() {
        return formatDate(updatedAt);
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "Chưa có";
        }
        try {
            return dateString.replace("T", " ").substring(0,
                    Math.min(16, dateString.replace("T", " ").length()));
        } catch (Exception e) {
            return dateString;
        }
    }

    public boolean hasPhone() {
        return phone != null && !phone.trim().isEmpty() && !phone.equals("null");
    }

    public boolean hasAddress() {
        return address != null && !address.trim().isEmpty() && !address.equals("null");
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', fullName='%s', role='%s'}",
                userId, username, fullName, role);
    }
}
