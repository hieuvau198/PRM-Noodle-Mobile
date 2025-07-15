package com.example.prm_v3.utils;

import com.example.prm_v3.R;

/**
 * Helper class for order status management
 */
public class StatusHelper {

    // Status constants
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_PREPARING = "preparing";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_ALL = "all";

    /**
     * Get the next status in the order workflow
     */
    public static String getNextStatus(String currentStatus) {
        if (currentStatus == null) return null;

        switch (currentStatus.toLowerCase()) {
            case STATUS_PENDING:
                return STATUS_CONFIRMED;
            case STATUS_CONFIRMED:
                return STATUS_PREPARING;
            case STATUS_PREPARING:
                return STATUS_DELIVERED;
            case STATUS_DELIVERED:
                return STATUS_COMPLETED;
            default:
                return null;
        }
    }

    /**
     * Get status display text in Vietnamese
     */
    public static String getStatusDisplayText(String status) {
        if (status == null) return "Không xác định";

        switch (status.toLowerCase()) {
            case STATUS_PENDING:
                return "Chờ xác nhận";
            case STATUS_CONFIRMED:
                return "Đã xác nhận";
            case STATUS_PREPARING:
                return "Đang chuẩn bị";
            case STATUS_DELIVERED:
                return "Đang vận chuyển";
            case STATUS_COMPLETED:
                return "Hoàn thành";
            case STATUS_CANCELLED:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }

    /**
     * Get status color resource
     */
    public static int getStatusColor(String status) {
        if (status == null) return R.color.gray_600;

        switch (status.toLowerCase()) {
            case STATUS_PENDING:
                return R.color.orange_600;
            case STATUS_CONFIRMED:
                return R.color.blue_600;
            case STATUS_PREPARING:
                return R.color.yellow_600;
            case STATUS_DELIVERED:
                return R.color.purple_600;
            case STATUS_COMPLETED:
                return R.color.green_600;
            case STATUS_CANCELLED:
                return R.color.red_600;
            default:
                return R.color.gray_600;
        }
    }

    /**
     * Get status background drawable resource
     */
    public static int getStatusBackground(String status) {
        if (status == null) return R.drawable.bg_status_badge;

        switch (status.toLowerCase()) {
            case STATUS_PENDING:
                return R.drawable.bg_status_pending;
            case STATUS_CONFIRMED:
                return R.drawable.bg_status_confirmed;
            case STATUS_PREPARING:
                return R.drawable.bg_status_preparing;
            case STATUS_DELIVERED:
                return R.drawable.bg_status_delivered;
            case STATUS_COMPLETED:
                return R.drawable.bg_status_completed;
            case STATUS_CANCELLED:
                return R.drawable.bg_status_cancelled;
            default:
                return R.drawable.bg_status_badge;
        }
    }

    /**
     * Check if status can be updated to next status
     */
    public static boolean canUpdateToNextStatus(String currentStatus) {
        return getNextStatus(currentStatus) != null;
    }

    /**
     * Check if order can be cancelled
     */
    public static boolean canCancelOrder(String currentStatus) {
        if (currentStatus == null) return false;

        switch (currentStatus.toLowerCase()) {
            case STATUS_PENDING:
            case STATUS_CONFIRMED:
            case STATUS_PREPARING:
            case STATUS_DELIVERED:
                return true;
            case STATUS_COMPLETED:
            case STATUS_CANCELLED:
                return false;
            default:
                return false;
        }
    }

    /**
     * Get action button text for next status
     */
    public static String getActionButtonText(String currentStatus) {
        String nextStatus = getNextStatus(currentStatus);
        if (nextStatus == null) return "";

        switch (nextStatus) {
            case STATUS_CONFIRMED:
                return "Xác nhận đơn hàng";
            case STATUS_PREPARING:
                return "Bắt đầu chuẩn bị";
            case STATUS_DELIVERED:
                return "Giao hàng";
            case STATUS_COMPLETED:
                return "Hoàn thành";
            default:
                return "Cập nhật";
        }
    }

    /**
     * Check if status is valid
     */
    public static boolean isValidStatus(String status) {
        if (status == null) return false;

        switch (status.toLowerCase()) {
            case STATUS_PENDING:
            case STATUS_CONFIRMED:
            case STATUS_PREPARING:
            case STATUS_DELIVERED:
            case STATUS_COMPLETED:
            case STATUS_CANCELLED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Get all available statuses
     */
    public static String[] getAllStatuses() {
        return new String[]{
                STATUS_ALL,
                STATUS_PENDING,
                STATUS_CONFIRMED,
                STATUS_PREPARING,
                STATUS_DELIVERED,
                STATUS_COMPLETED,
                STATUS_CANCELLED
        };
    }

    /**
     * Get tab display names
     */
    public static String getTabDisplayName(String status) {
        if (status == null) return "Tất cả";

        switch (status.toLowerCase()) {
            case STATUS_ALL:
                return "Tất cả";
            case STATUS_PENDING:
                return "Chờ xác nhận";
            case STATUS_CONFIRMED:
                return "Đã xác nhận";
            case STATUS_PREPARING:
                return "Đang chuẩn bị";
            case STATUS_DELIVERED:
                return "Đang vận chuyển";
            case STATUS_COMPLETED:
                return "Hoàn thành";
            case STATUS_CANCELLED:
                return "Đã hủy";
            default:
                return "Tất cả";
        }
    }

    /**
     * Get notification message for status update
     */
    public static String getStatusUpdateMessage(String newStatus) {
        if (newStatus == null) return "Cập nhật trạng thái thành công";

        switch (newStatus.toLowerCase()) {
            case STATUS_CONFIRMED:
                return "Đã xác nhận đơn hàng";
            case STATUS_PREPARING:
                return "Đã bắt đầu chuẩn bị đơn hàng";
            case STATUS_DELIVERED:
                return "Đơn hàng đã được giao";
            case STATUS_COMPLETED:
                return "Đơn hàng đã hoàn thành";
            case STATUS_CANCELLED:
                return "Đơn hàng đã được hủy";
            default:
                return "Cập nhật trạng thái thành công";
        }
    }

    /**
     * Check if status is final (cannot be changed)
     */
    public static boolean isFinalStatus(String status) {
        if (status == null) return false;

        return status.toLowerCase().equals(STATUS_COMPLETED) ||
                status.toLowerCase().equals(STATUS_CANCELLED);
    }

    /**
     * Get status priority for sorting (lower number = higher priority)
     */
    public static int getStatusPriority(String status) {
        if (status == null) return 999;

        switch (status.toLowerCase()) {
            case STATUS_PENDING:
                return 1;
            case STATUS_CONFIRMED:
                return 2;
            case STATUS_PREPARING:
                return 3;
            case STATUS_DELIVERED:
                return 4;
            case STATUS_COMPLETED:
                return 5;
            case STATUS_CANCELLED:
                return 6;
            default:
                return 999;
        }
    }
}