package com.example.prm_v3.api;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Object data;

    @SerializedName("error")
    private String error;

    // Constructors
    public ApiResponse() {}

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    // Helper methods
    public String getDisplayMessage() {
        if (message != null && !message.isEmpty()) {
            return message;
        }
        if (error != null && !error.isEmpty()) {
            return error;
        }
        return success ? "Thành công" : "Có lỗi xảy ra";
    }

    @Override
    public String toString() {
        return String.format("ApiResponse{success=%b, message='%s', error='%s'}",
                success, message, error);
    }
}