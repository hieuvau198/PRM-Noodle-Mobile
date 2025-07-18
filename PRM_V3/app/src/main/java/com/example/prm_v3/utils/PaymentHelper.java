package com.example.prm_v3.utils;

import com.example.prm_v3.R;
import com.example.prm_v3.model.Payment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Helper class for payment management operations
 * Updated for new payment statuses: pending, processing, complete
 */
public class PaymentHelper {

    // Payment Status Constants - UPDATED
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_COMPLETE = "complete";  // Changed from "paid"
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_REFUNDED = "refunded";
    public static final String STATUS_CANCELLED = "cancelled";

    // Payment Method Constants
    public static final String METHOD_CASH = "cash";
    public static final String METHOD_DIGITAL_WALLET = "digital_wallet";
    public static final String METHOD_CREDIT_CARD = "credit_card";
    public static final String METHOD_DEBIT_CARD = "debit_card";
    public static final String METHOD_BANK_TRANSFER = "bank_transfer";

    // ========== STATUS MANAGEMENT ==========

    /**
     * Get next status in payment workflow
     */
    public static String getNextStatus(String currentStatus) {
        if (currentStatus == null) return STATUS_PENDING;

        switch (currentStatus.toLowerCase()) {
            case STATUS_PENDING:
                return STATUS_PROCESSING;
            case STATUS_PROCESSING:
                return STATUS_COMPLETE;
            default:
                return currentStatus;
        }
    }

    /**
     * Get payment status display text in Vietnamese
     */
    public static String getStatusDisplayText(String status) {
        if (status == null) return "Không xác định";

        switch (status.toLowerCase()) {
            case STATUS_PENDING:
                return "Chờ thanh toán";
            case STATUS_PROCESSING:
                return "Đang xử lý";
            case STATUS_COMPLETE:
                return "Hoàn thành";
            case STATUS_FAILED:
                return "Thất bại";
            case STATUS_REFUNDED:
                return "Đã hoàn tiền";
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
            case STATUS_PROCESSING:
                return R.color.blue_600;
            case STATUS_COMPLETE:
                return R.color.green_600;
            case STATUS_FAILED:
                return R.color.red_600;
            case STATUS_REFUNDED:
                return R.color.purple_600;
            case STATUS_CANCELLED:
                return R.color.gray_600;
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
            case STATUS_PROCESSING:
                return R.drawable.bg_payment_processing;
            case STATUS_COMPLETE:
                return R.drawable.bg_payment_complete;
            case STATUS_FAILED:
                return R.drawable.bg_payment_failed;
            case STATUS_REFUNDED:
                return R.drawable.bg_status_cancelled;
            case STATUS_CANCELLED:
                return R.drawable.bg_status_cancelled;
            default:
                return R.drawable.bg_status_badge;
        }
    }

    // ========== PAYMENT METHOD MANAGEMENT ==========

    /**
     * Get payment method display text in Vietnamese
     */
    public static String getMethodDisplayText(String method) {
        if (method == null) return "Không xác định";

        switch (method.toLowerCase()) {
            case METHOD_CASH:
                return "Tiền mặt";
            case METHOD_DIGITAL_WALLET:
                return "Ví điện tử";
            case METHOD_CREDIT_CARD:
                return "Thẻ tín dụng";
            case METHOD_DEBIT_CARD:
                return "Thẻ ghi nợ";
            case METHOD_BANK_TRANSFER:
                return "Chuyển khoản";
            default:
                return method;
        }
    }

    public static String getMethodCodeFromPosition(int position) {
        String[] methods = {METHOD_CASH, METHOD_DIGITAL_WALLET, METHOD_CREDIT_CARD, METHOD_DEBIT_CARD, METHOD_BANK_TRANSFER};
        if (position >= 0 && position < methods.length) {
            return methods[position];
        }
        return METHOD_CASH; // Default
    }

    public static int getMethodPositionFromCode(String methodCode) {
        if (methodCode == null) return 0;

        String[] methods = {METHOD_CASH, METHOD_DIGITAL_WALLET, METHOD_CREDIT_CARD, METHOD_DEBIT_CARD, METHOD_BANK_TRANSFER};
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].equalsIgnoreCase(methodCode)) {
                return i;
            }
        }
        return 0; // Default to cash
    }

    /**
     * Get payment method icon resource
     */
    public static int getMethodIcon(String method) {
        if (method == null) return R.drawable.ic_payment;

        switch (method.toLowerCase()) {
            case METHOD_CASH:
                return R.drawable.ic_cash;
            case METHOD_DIGITAL_WALLET:
                return R.drawable.ic_wallet;
            case METHOD_CREDIT_CARD:
                return R.drawable.ic_credit_card;
            case METHOD_BANK_TRANSFER:
                return R.drawable.ic_bank_transfer;
            default:
                return R.drawable.ic_payment;
        }
    }

    // ========== VALIDATION METHODS ==========

    /**
     * Check if payment can be processed
     */
    public static boolean canProcessPayment(String status) {
        return STATUS_PENDING.equalsIgnoreCase(status);
    }

    /**
     * Check if payment can be completed
     */
    public static boolean canCompletePayment(String status) {
        return STATUS_PENDING.equalsIgnoreCase(status) ||
                STATUS_PROCESSING.equalsIgnoreCase(status);
    }

    /**
     * Check if payment can be marked as failed
     */
    public static boolean canFailPayment(String status) {
        return STATUS_PENDING.equalsIgnoreCase(status) ||
                STATUS_PROCESSING.equalsIgnoreCase(status);
    }

    /**
     * Check if payment can be refunded
     */
    public static boolean canRefundPayment(String status) {
        return STATUS_COMPLETE.equalsIgnoreCase(status);
    }

    /**
     * Check if payment can be cancelled
     */
    public static boolean canCancelPayment(String status) {
        return STATUS_PENDING.equalsIgnoreCase(status) ||
                STATUS_PROCESSING.equalsIgnoreCase(status);
    }

    /**
     * Check if status is valid
     */
    public static boolean isValidStatus(String status) {
        if (status == null) return false;

        switch (status.toLowerCase()) {
            case STATUS_PENDING:
            case STATUS_PROCESSING:
            case STATUS_COMPLETE:
            case STATUS_FAILED:
            case STATUS_REFUNDED:
            case STATUS_CANCELLED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if payment method is valid
     */
    public static boolean isValidMethod(String method) {
        if (method == null) return false;

        switch (method.toLowerCase()) {
            case METHOD_CASH:
            case METHOD_DIGITAL_WALLET:
            case METHOD_CREDIT_CARD:
            case METHOD_DEBIT_CARD:
            case METHOD_BANK_TRANSFER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if payment is final (cannot be changed)
     */
    public static boolean isFinalStatus(String status) {
        if (status == null) return false;

        return status.toLowerCase().equals(STATUS_COMPLETE) ||
                status.toLowerCase().equals(STATUS_FAILED) ||
                status.toLowerCase().equals(STATUS_REFUNDED) ||
                status.toLowerCase().equals(STATUS_CANCELLED);
    }

    // ========== FORMATTING METHODS ==========

    /**
     * Format payment amount with currency
     */
    public static String formatAmount(double amount) {
        return String.format("%.0f₫", amount);
    }

    /**
     * Format payment amount with thousands separator
     */
    public static String formatAmountWithSeparator(double amount) {
        return String.format("%,.0f₫", amount);
    }

    /**
     * Format date string for display
     */
    public static String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty() || dateString.equals("null")) {
            return "Chưa có";
        }

        try {
            // Parse ISO format
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            Date date = isoFormat.parse(dateString);
            return displayFormat.format(date);
        } catch (ParseException e) {
            // Fallback to substring
            try {
                return dateString.replace("T", " ").substring(0,
                        Math.min(16, dateString.replace("T", " ").length()));
            } catch (Exception ex) {
                return dateString;
            }
        }
    }

    /**
     * Parse amount from formatted string
     */
    public static double parseAmount(String formattedAmount) {
        if (formattedAmount == null || formattedAmount.isEmpty()) {
            return 0.0;
        }

        try {
            // Remove currency symbol and separators
            String cleanAmount = formattedAmount.replaceAll("[₫,.]", "").trim();
            return Double.parseDouble(cleanAmount);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Format currency input (remove non-numeric characters)
     */
    public static String formatCurrencyInput(String input) {
        if (input == null) return "";
        return input.replaceAll("[^0-9]", "");
    }

    /**
     * Format date for display (short format)
     */
    public static String formatDateShort(String dateString) {
        if (dateString == null || dateString.isEmpty() || dateString.equals("null")) {
            return "Chưa có";
        }

        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Date date = isoFormat.parse(dateString);
            return displayFormat.format(date);
        } catch (ParseException e) {
            // Fallback to substring
            try {
                return dateString.substring(0, Math.min(10, dateString.length()));
            } catch (Exception ex) {
                return dateString;
            }
        }
    }

    // ========== UTILITY METHODS ==========

    /**
     * Get status update message for notifications
     */
    public static String getStatusUpdateMessage(String newStatus) {
        if (newStatus == null) return "Cập nhật trạng thái thành công";

        switch (newStatus.toLowerCase()) {
            case STATUS_PROCESSING:
                return "Thanh toán đang được xử lý";
            case STATUS_COMPLETE:
                return "Thanh toán đã hoàn tất";
            case STATUS_FAILED:
                return "Thanh toán đã thất bại";
            case STATUS_REFUNDED:
                return "Thanh toán đã được hoàn tiền";
            case STATUS_CANCELLED:
                return "Thanh toán đã bị hủy";
            default:
                return "Cập nhật trạng thái thành công";
        }
    }

    /**
     * Get all available payment statuses
     */
    public static String[] getAllStatuses() {
        return new String[]{
                STATUS_PENDING,
                STATUS_PROCESSING,
                STATUS_COMPLETE,
                STATUS_FAILED,
                STATUS_REFUNDED,
                STATUS_CANCELLED
        };
    }

    /**
     * Get all available payment methods
     */
    public static String[] getAllMethods() {
        return new String[]{
                METHOD_CASH,
                METHOD_DIGITAL_WALLET,
                METHOD_CREDIT_CARD,
                METHOD_DEBIT_CARD,
                METHOD_BANK_TRANSFER
        };
    }

    /**
     * Get all payment method display names
     */
    public static String[] getAllMethodDisplayNames() {
        return new String[]{
                "Tiền mặt",
                "Ví điện tử",
                "Thẻ tín dụng",
                "Thẻ ghi nợ",
                "Chuyển khoản"
        };
    }

    /**
     * Get status priority for sorting (lower number = higher priority)
     */
    public static int getStatusPriority(String status) {
        if (status == null) return 999;

        switch (status.toLowerCase()) {
            case STATUS_PENDING:
                return 1;
            case STATUS_PROCESSING:
                return 2;
            case STATUS_COMPLETE:
                return 3;
            case STATUS_FAILED:
                return 4;
            case STATUS_REFUNDED:
                return 5;
            case STATUS_CANCELLED:
                return 6;
            default:
                return 999;
        }
    }

    /**
     * Generate transaction reference
     */
    public static String generateTransactionReference() {
        return "PAY" + System.currentTimeMillis();
    }

    public static String generatePaymentReference(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            prefix = "TXN";
        }
        return prefix + "_" + System.currentTimeMillis();
    }

    /**
     * Validate payment amount
     */
    public static boolean isValidAmount(double amount) {
        return amount > 0 && amount <= 999999999; // Max 999M VND
    }

    public static boolean canCreatePaymentForOrder(String orderStatus) {
        if (orderStatus == null) return false;

        switch (orderStatus.toLowerCase()) {
            case "confirmed":
            case "preparing":
            case "ready":
            case "delivered":
                return true;
            case "pending":
            case "cancelled":
            default:
                return false;
        }
    }

    public static String getOrderStatusForPayment(String orderStatus) {
        if (orderStatus == null) return "Không xác định";

        switch (orderStatus.toLowerCase()) {
            case "confirmed":
                return "Đã xác nhận - Có thể tạo thanh toán";
            case "preparing":
                return "Đang chuẩn bị - Có thể tạo thanh toán";
            case "ready":
                return "Sẵn sàng - Có thể tạo thanh toán";
            case "delivered":
                return "Đã giao - Có thể tạo thanh toán";
            case "pending":
                return "Chờ xác nhận - Chưa thể tạo thanh toán";
            case "cancelled":
                return "Đã hủy - Không thể tạo thanh toán";
            default:
                return orderStatus;
        }
    }

    /**
     * Get payment summary text
     */
    public static String getPaymentSummary(Payment payment) {
        if (payment == null) return "Không có thông tin";

        return String.format("Thanh toán #%d - %s - %s",
                payment.getPaymentId(),
                formatAmount(payment.getPaymentAmount()),
                getStatusDisplayText(payment.getPaymentStatus()));
    }

    /**
     * Check if payment needs attention (pending too long, failed, etc.)
     */
    public static boolean needsAttention(Payment payment) {
        if (payment == null) return false;

        String status = payment.getPaymentStatus();
        return STATUS_PENDING.equalsIgnoreCase(status) ||
                STATUS_FAILED.equalsIgnoreCase(status);
    }

    /**
     * Get action button text based on current status
     */
    public static String getActionButtonText(String currentStatus) {
        if (currentStatus == null) return "";

        switch (currentStatus.toLowerCase()) {
            case STATUS_PENDING:
                return "Xử lý thanh toán";
            case STATUS_PROCESSING:
                return "Hoàn tất thanh toán";
            default:
                return "Cập nhật";
        }
    }

    // ========== SEARCH AND FILTER HELPERS ==========

    /**
     * Check if payment matches search query
     */
    public static boolean matchesSearchQuery(Payment payment, String query) {
        if (payment == null || query == null || query.trim().isEmpty()) {
            return true;
        }

        String lowerCaseQuery = query.toLowerCase();

        // Search in customer name
        if (payment.getCustomerName() != null &&
                payment.getCustomerName().toLowerCase().contains(lowerCaseQuery)) {
            return true;
        }

        // Search in order ID
        if (String.valueOf(payment.getOrderId()).contains(lowerCaseQuery)) {
            return true;
        }

        // Search in payment ID
        if (String.valueOf(payment.getPaymentId()).contains(lowerCaseQuery)) {
            return true;
        }

        // Search in transaction reference
        if (payment.getTransactionReference() != null &&
                payment.getTransactionReference().toLowerCase().contains(lowerCaseQuery)) {
            return true;
        }

        // Search in payment method
        if (getMethodDisplayText(payment.getPaymentMethod()).toLowerCase().contains(lowerCaseQuery)) {
            return true;
        }

        // Search in status
        if (getStatusDisplayText(payment.getPaymentStatus()).toLowerCase().contains(lowerCaseQuery)) {
            return true;
        }

        return false;
    }

    public static boolean isPaymentInDateRange(Payment payment, String startDate, String endDate) {
        if (payment == null || payment.getPaymentDate() == null) {
            return false;
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date paymentDate = format.parse(payment.getPaymentDate().substring(0, 10));

            if (startDate != null && !startDate.isEmpty()) {
                Date start = format.parse(startDate);
                if (paymentDate.before(start)) {
                    return false;
                }
            }

            if (endDate != null && !endDate.isEmpty()) {
                Date end = format.parse(endDate);
                if (paymentDate.after(end)) {
                    return false;
                }
            }

            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isPaymentInAmountRange(Payment payment, double minAmount, double maxAmount) {
        if (payment == null) {
            return false;
        }

        double amount = payment.getPaymentAmount();

        if (minAmount > 0 && amount < minAmount) {
            return false;
        }

        if (maxAmount > 0 && amount > maxAmount) {
            return false;
        }

        return true;
    }

    public static double calculateProcessingFee(double amount, String method) {
        if (amount <= 0) return 0.0;

        switch (method.toLowerCase()) {
            case METHOD_CASH:
                return 0.0; // No fee for cash
            case METHOD_DIGITAL_WALLET:
                return amount * 0.01; // 1% fee
            case METHOD_CREDIT_CARD:
            case METHOD_DEBIT_CARD:
                return amount * 0.025; // 2.5% fee
            case METHOD_BANK_TRANSFER:
                return Math.min(amount * 0.005, 50000); // 0.5% fee, max 50k
            default:
                return 0.0;
        }
    }

    public static String getEstimatedProcessingTime(String method) {
        if (method == null) return "Không xác định";

        switch (method.toLowerCase()) {
            case METHOD_CASH:
                return "Ngay lập tức";
            case METHOD_DIGITAL_WALLET:
                return "1-2 phút";
            case METHOD_CREDIT_CARD:
            case METHOD_DEBIT_CARD:
                return "2-5 phút";
            case METHOD_BANK_TRANSFER:
                return "5-15 phút";
            default:
                return "Không xác định";
        }
    }

    public static boolean requiresVerification(String method) {
        if (method == null) return false;

        switch (method.toLowerCase()) {
            case METHOD_CASH:
                return false;
            case METHOD_DIGITAL_WALLET:
            case METHOD_CREDIT_CARD:
            case METHOD_DEBIT_CARD:
            case METHOD_BANK_TRANSFER:
                return true;
            default:
                return false;
        }
    }
}