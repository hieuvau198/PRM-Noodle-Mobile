package com.example.prm_v3.model;

import java.util.List;

public class OrderStatistics {
    private int totalOrders;
    private int pendingOrders;
    private int confirmedOrders;
    private int preparingOrders;
    private int deliveredOrders;
    private int completedOrders;
    private int cancelledOrders;
    private double totalRevenue;
    private double pendingRevenue;

    // Constructors
    public OrderStatistics() {}

    public OrderStatistics(int totalOrders, double totalRevenue) {
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
    }

    public OrderStatistics(int totalOrders, int pendingOrders, int confirmedOrders,
                           int preparingOrders, int deliveredOrders, int completedOrders,
                           int cancelledOrders, double totalRevenue, double pendingRevenue) {
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.confirmedOrders = confirmedOrders;
        this.preparingOrders = preparingOrders;
        this.deliveredOrders = deliveredOrders;
        this.completedOrders = completedOrders;
        this.cancelledOrders = cancelledOrders;
        this.totalRevenue = totalRevenue;
        this.pendingRevenue = pendingRevenue;
    }

    // Getters and Setters
    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public int getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(int pendingOrders) { this.pendingOrders = pendingOrders; }

    public int getConfirmedOrders() { return confirmedOrders; }
    public void setConfirmedOrders(int confirmedOrders) { this.confirmedOrders = confirmedOrders; }

    public int getPreparingOrders() { return preparingOrders; }
    public void setPreparingOrders(int preparingOrders) { this.preparingOrders = preparingOrders; }

    public int getDeliveredOrders() { return deliveredOrders; }
    public void setDeliveredOrders(int deliveredOrders) { this.deliveredOrders = deliveredOrders; }

    public int getCompletedOrders() { return completedOrders; }
    public void setCompletedOrders(int completedOrders) { this.completedOrders = completedOrders; }

    public int getCancelledOrders() { return cancelledOrders; }
    public void setCancelledOrders(int cancelledOrders) { this.cancelledOrders = cancelledOrders; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public double getPendingRevenue() { return pendingRevenue; }
    public void setPendingRevenue(double pendingRevenue) { this.pendingRevenue = pendingRevenue; }

    // Helper methods
    public String getFormattedTotalRevenue() {
        return String.format("%.0f₫", totalRevenue);
    }

    public String getFormattedPendingRevenue() {
        return String.format("%.0f₫", pendingRevenue);
    }

    public double getCompletionRate() {
        if (totalOrders == 0) return 0;
        return (double) completedOrders / totalOrders * 100;
    }

    public double getCancellationRate() {
        if (totalOrders == 0) return 0;
        return (double) cancelledOrders / totalOrders * 100;
    }

    public String getFormattedCompletionRate() {
        return String.format("%.1f%%", getCompletionRate());
    }

    public String getFormattedCancellationRate() {
        return String.format("%.1f%%", getCancellationRate());
    }

    public int getActiveOrders() {
        return pendingOrders + confirmedOrders + preparingOrders + deliveredOrders;
    }

    public double getAverageOrderValue() {
        if (completedOrders == 0) return 0;
        return totalRevenue / completedOrders;
    }

    public String getFormattedAverageOrderValue() {
        return String.format("%.0f₫", getAverageOrderValue());
    }

    // Reset all statistics
    public void reset() {
        totalOrders = 0;
        pendingOrders = 0;
        confirmedOrders = 0;
        preparingOrders = 0;
        deliveredOrders = 0;
        completedOrders = 0;
        cancelledOrders = 0;
        totalRevenue = 0;
        pendingRevenue = 0;
    }

    // Utility method to calculate statistics from order list
    public static OrderStatistics calculateFromOrders(List<Order> orders) {
        OrderStatistics stats = new OrderStatistics();
        if (orders == null || orders.isEmpty()) {
            return stats;
        }

        stats.setTotalOrders(orders.size());
        double totalRevenue = 0;
        double pendingRevenue = 0;

        for (Order order : orders) {
            String status = order.getOrderStatus();
            if (status == null) continue;

            switch (status.toLowerCase()) {
                case "pending":
                    stats.setPendingOrders(stats.getPendingOrders() + 1);
                    pendingRevenue += order.getTotalAmount();
                    break;
                case "confirmed":
                    stats.setConfirmedOrders(stats.getConfirmedOrders() + 1);
                    break;
                case "preparing":
                    stats.setPreparingOrders(stats.getPreparingOrders() + 1);
                    break;
                case "delivered":
                    stats.setDeliveredOrders(stats.getDeliveredOrders() + 1);
                    break;
                case "completed":
                    stats.setCompletedOrders(stats.getCompletedOrders() + 1);
                    totalRevenue += order.getTotalAmount();
                    break;
                case "cancelled":
                    stats.setCancelledOrders(stats.getCancelledOrders() + 1);
                    break;
            }
        }

        stats.setTotalRevenue(totalRevenue);
        stats.setPendingRevenue(pendingRevenue);
        return stats;
    }

    @Override
    public String toString() {
        return String.format("OrderStatistics{total=%d, completed=%d, revenue=%.0f₫}",
                totalOrders, completedOrders, totalRevenue);
    }
}