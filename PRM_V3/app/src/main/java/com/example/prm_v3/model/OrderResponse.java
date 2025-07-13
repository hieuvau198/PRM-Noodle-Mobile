package com.example.prm_v3.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class OrderResponse {
    @SerializedName("orders")
    private List<Order> orders;

    @SerializedName("totalCount")
    private int totalCount;

    @SerializedName("page")
    private int page;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("hasNext")
    private boolean hasNext;

    @SerializedName("hasPrevious")
    private boolean hasPrevious;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private boolean success;

    // Constructors
    public OrderResponse() {
        this.orders = new ArrayList<>();
    }

    public OrderResponse(List<Order> orders, int totalCount, int page, int pageSize) {
        this.orders = orders != null ? orders : new ArrayList<>();
        this.totalCount = totalCount;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = calculateTotalPages(totalCount, pageSize);
        this.hasNext = page < totalPages;
        this.hasPrevious = page > 1;
        this.success = true;
    }

    // Getters and Setters
    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
    }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        this.totalPages = calculateTotalPages(totalCount, pageSize);
        updatePaginationFlags();
    }

    public int getPage() { return page; }
    public void setPage(int page) {
        this.page = page;
        updatePaginationFlags();
    }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.totalPages = calculateTotalPages(totalCount, pageSize);
        updatePaginationFlags();
    }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }

    public boolean isHasPrevious() { return hasPrevious; }
    public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    // Helper methods
    public boolean isEmpty() {
        return orders == null || orders.isEmpty();
    }

    public int getOrderCount() {
        return orders != null ? orders.size() : 0;
    }

    public boolean isFirstPage() {
        return page <= 1;
    }

    public boolean isLastPage() {
        return page >= totalPages;
    }

    public int getNextPage() {
        return hasNext ? page + 1 : page;
    }

    public int getPreviousPage() {
        return hasPrevious ? page - 1 : page;
    }

    public String getPaginationInfo() {
        if (isEmpty()) {
            return "Không có dữ liệu";
        }
        int startItem = (page - 1) * pageSize + 1;
        int endItem = Math.min(page * pageSize, totalCount);
        return String.format("Hiển thị %d-%d trong %d đơn hàng", startItem, endItem, totalCount);
    }

    private int calculateTotalPages(int totalCount, int pageSize) {
        if (pageSize <= 0) return 0;
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    private void updatePaginationFlags() {
        this.hasNext = page < totalPages;
        this.hasPrevious = page > 1;
    }

    @Override
    public String toString() {
        return String.format("OrderResponse{orders=%d, page=%d/%d, totalCount=%d}",
                getOrderCount(), page, totalPages, totalCount);
    }
}