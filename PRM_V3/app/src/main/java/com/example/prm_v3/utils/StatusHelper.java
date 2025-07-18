package com.example.prm_v3.utils;

import com.example.prm_v3.R;

/**
 * Helper class for order status management
 */
public class StatusHelper {

    // Order Status Constants
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_PREPARING = "preparing";
    public static final String STATUS_READY = "ready";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_CANCELLED = "cancelled";

    // ========== STATUS DISPLAY METHODS ==========

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
            case STATUS_READY:
                return "Sẵn sàng giao";
            case STATUS_DELIVERED:
                return "Đang vận chuyển";
            case STATUS_COMPLETED:
                return "Hoàn thành";
            case STATUS_CANCELLED:
                return "Đã hủy";
            default:
                return status;
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
            case STATUS_READY:
                return R.color.purple_600;
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
            case STATUS_READY:
                return R.drawable.bg_status_ready;
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

    // ========== STATUS VALIDATION METHODS ==========

    /**
     * Check if order can be updated to next status
     */
    public static boolean canUpdateToNextStatus(String currentStatus) {
        if (currentStatus == null) return false;

        switch (currentStatus.toLowerCase()) {
            case STATUS_PENDING:
            case STATUS_CONFIRMED:
            case STATUS_PREPARING:
            case STATUS_READY:
                return true;
            case STATUS_DELIVERED:
            case STATUS_COMPLETED:
            case STATUS_CANCELLED:
                return false;
            default:
                return false;
        }
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
            case STATUS_READY:
                return true;
            case STATUS_DELIVERED:
            case STATUS_COMPLETED:
            case STATUS_CANCELLED:
                return false;
            default:
                return false;
        }
    }

    /**
     * Check if status is final (cannot be changed)
     */
    public static boolean isFinalStatus(String status) {
        if (status == null) return false;

        switch (status.toLowerCase()) {
            case STATUS_COMPLETED:
            case STATUS_CANCELLED:
                return true;
            default:
                return false;
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
            case STATUS_READY:
            case STATUS_DELIVERED:
            case STATUS_COMPLETED:
            case STATUS_CANCELLED:
                return true;
            default:
                return false;
        }
    }

    // ========== STATUS WORKFLOW METHODS ==========

    /**
     * Get next status in workflow
     */
    public static String getNextStatus(String currentStatus) {
        if (currentStatus == null) return null;

        switch (currentStatus.toLowerCase()) {
            case STATUS_PENDING:
                return STATUS_CONFIRMED;
            case STATUS_CONFIRMED:
                return STATUS_PREPARING;
            case STATUS_PREPARING:
                return STATUS_READY;
            case STATUS_READY:
                return STATUS_DELIVERED;
            case STATUS_DELIVERED:
                return STATUS_COMPLETED;
            default:
                return null;
        }
    }

    /**
     * Get action button text for current status
     */
    public static String getActionButtonText(String currentStatus) {
        if (currentStatus == null) return "Cập nhật";

        switch (currentStatus.toLowerCase()) {
            case STATUS_PENDING:
                return "Xác nhận đơn hàng";
            case STATUS_CONFIRMED:
                return "Bắt đầu chuẩn bị";
            case STATUS_PREPARING:
                return "Sẵn sàng giao";
            case STATUS_READY:
                return "Hoàn thành";
            case STATUS_DELIVERED:
                return "Hoàn thành";
            default:
                return "Cập nhật";
        }
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
            case STATUS_READY:
                return 4;
            case STATUS_DELIVERED:
                return 5;
            case STATUS_COMPLETED:
                return 6;
            case STATUS_CANCELLED:
                return 7;
            default:
                return 999;
        }
    }

    // ========== UTILITY METHODS ==========

    /**
     * Get status update message for notifications
     */
    public static String getStatusUpdateMessage(String newStatus) {
        if (newStatus == null) return "Cập nhật trạng thái thành công";

        switch (newStatus.toLowerCase()) {
            case STATUS_CONFIRMED:
                return "Đơn hàng đã được xác nhận";
            case STATUS_PREPARING:
                return "Đã bắt đầu chuẩn bị đơn hàng";
            case STATUS_READY:
                return "Đơn hàng sẵn sàng giao";
            case STATUS_DELIVERED:
                return "Đơn hàng đang được vận chuyển";
            case STATUS_COMPLETED:
                return "Đơn hàng đã hoàn thành";
            case STATUS_CANCELLED:
                return "Đơn hàng đã bị hủy";
            default:
                return "Cập nhật trạng thái thành công";
        }
    }

    /**
     * Get all available order statuses
     */
    public static String[] getAllStatuses() {
        return new String[]{
                STATUS_PENDING,
                STATUS_CONFIRMED,
                STATUS_PREPARING,
                STATUS_READY,
                STATUS_DELIVERED,
                STATUS_COMPLETED,
                STATUS_CANCELLED
        };
    }

    /**
     * Get all status display names
     */
    public static String[] getAllStatusDisplayNames() {
        return new String[]{
                "Chờ xác nhận",
                "Đã xác nhận",
                "Đang chuẩn bị",
                "Sẵn sàng giao",
                "Đang vận chuyển",
                "Hoàn thành",
                "Đã hủy"
        };
    }

    /**
     * Check if status allows payment creation
     */
    public static boolean canCreatePaymentForStatus(String orderStatus) {
        if (orderStatus == null) return false;

        switch (orderStatus.toLowerCase()) {
            case STATUS_CONFIRMED:
            case STATUS_PREPARING:
            case STATUS_READY:
            case STATUS_DELIVERED:
                return true;
            case STATUS_PENDING:
            case STATUS_CANCELLED:
            case STATUS_COMPLETED:
            default:
                return false;
        }
    }

    /**
     * Get order status description for payment creation
     */
    public static String getOrderStatusForPayment(String orderStatus) {
        if (orderStatus == null) return "Không xác định";

        switch (orderStatus.toLowerCase()) {
            case STATUS_CONFIRMED:
                return "Đã xác nhận - Có thể tạo thanh toán";
            case STATUS_PREPARING:
                return "Đang chuẩn bị - Có thể tạo thanh toán";
            case STATUS_READY:
                return "Sẵn sàng - Có thể tạo thanh toán";
            case STATUS_DELIVERED:
                return "Đã giao - Có thể tạo thanh toán";
            case STATUS_PENDING:
                return "Chờ xác nhận - Chưa thể tạo thanh toán";
            case STATUS_CANCELLED:
                return "Đã hủy - Không thể tạo thanh toán";
            case STATUS_COMPLETED:
                return "Đã hoàn thành - Không cần thanh toán";
            default:
                return orderStatus;
        }
    }

    // ========== SEARCH AND FILTER HELPERS ==========

    /**
     * Filter statuses by category
     */
    public static String[] getActiveStatuses() {
        return new String[]{
                STATUS_PENDING,
                STATUS_CONFIRMED,
                STATUS_PREPARING,
                STATUS_READY,
                STATUS_DELIVERED
        };
    }

    public static String[] getFinalStatuses() {
        return new String[]{
                STATUS_COMPLETED,
                STATUS_CANCELLED
        };
    }

    public static String[] getProcessingStatuses() {
        return new String[]{
                STATUS_CONFIRMED,
                STATUS_PREPARING,
                STATUS_READY
        };
    }

    /**
     * Check if status is in active processing
     */
    public static boolean isActiveStatus(String status) {
        if (status == null) return false;

        for (String activeStatus : getActiveStatuses()) {
            if (activeStatus.equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if status is in processing (kitchen work)
     */
    public static boolean isProcessingStatus(String status) {
        if (status == null) return false;

        for (String processingStatus : getProcessingStatuses()) {
            if (processingStatus.equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }
}