package com.example.prm_noodle_mobile.data.model;

import com.google.gson.annotations.SerializedName;
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

    public List<Order> getOrders() { return orders; }
    public int getTotalCount() { return totalCount; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public int getTotalPages() { return totalPages; }
}
