package com.example.prm_v3.api;

import com.google.gson.annotations.SerializedName;

public class UpdateOrderStatusRequest {
    @SerializedName("status")
    private String status;

    public UpdateOrderStatusRequest() {}

    public UpdateOrderStatusRequest(String status) {
        this.status = status;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Static factory methods for convenience
    public static UpdateOrderStatusRequest pending() {
        return new UpdateOrderStatusRequest("pending");
    }

    public static UpdateOrderStatusRequest confirmed() {
        return new UpdateOrderStatusRequest("confirmed");
    }

    public static UpdateOrderStatusRequest preparing() {
        return new UpdateOrderStatusRequest("preparing");
    }

    public static UpdateOrderStatusRequest delivered() {
        return new UpdateOrderStatusRequest("delivered");
    }

    public static UpdateOrderStatusRequest completed() {
        return new UpdateOrderStatusRequest("completed");
    }

    public static UpdateOrderStatusRequest cancelled() {
        return new UpdateOrderStatusRequest("cancelled");
    }
}