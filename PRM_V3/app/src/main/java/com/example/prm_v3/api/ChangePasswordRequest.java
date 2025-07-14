package com.example.prm_v3.api;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {
    @SerializedName("currentPassword")
    private String currentPassword;

    @SerializedName("newPassword")
    private String newPassword;

    // Constructors
    public ChangePasswordRequest() {}

    public ChangePasswordRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    // Getters and Setters
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    // Validation methods
    public boolean isValid() {
        return currentPassword != null && !currentPassword.trim().isEmpty() &&
                newPassword != null && !newPassword.trim().isEmpty() &&
                newPassword.length() >= 6;
    }

    public String getValidationError() {
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            return "Mật khẩu hiện tại không được để trống";
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return "Mật khẩu mới không được để trống";
        }
        if (newPassword.length() < 6) {
            return "Mật khẩu mới phải có ít nhất 6 ký tự";
        }
        if (currentPassword.equals(newPassword)) {
            return "Mật khẩu mới phải khác mật khẩu hiện tại";
        }
        return null;
    }
}