package com.example.prm_v3.ui.payment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.prm_v3.model.Payment;
import com.example.prm_v3.repository.PaymentRepository;
import java.util.List;

public class paymentViewModel extends ViewModel {

    private PaymentRepository paymentRepository;
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    public paymentViewModel() {
        paymentRepository = PaymentRepository.getInstance();
    }

    // ========== LIVEDATA GETTERS ==========

    public LiveData<List<Payment>> getPayments() {
        return paymentRepository.getPaymentsLiveData();
    }

    public LiveData<Boolean> getLoading() {
        return paymentRepository.getLoadingLiveData();
    }

    public LiveData<String> getError() {
        return paymentRepository.getErrorLiveData();
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    // ========== LOAD PAYMENTS ==========

    /**
     * Load all payments with pagination
     */
    public void loadPayments(int page, int pageSize) {
        paymentRepository.fetchPayments(page, pageSize);
    }

    /**
     * Load payments by status filter
     */
    public void loadPaymentsByStatus(String status, int page, int pageSize) {
        paymentRepository.fetchPaymentsByStatus(status, page, pageSize);
    }

    /**
     * Load payments by status (default pagination)
     */
    public void loadPaymentsByStatus(String status) {
        paymentRepository.fetchPaymentsByStatus(status, 1, 20);
    }

    /**
     * Convenience methods for specific statuses
     */
    public void loadPendingPayments() {
        loadPaymentsByStatus("pending");
    }

    public void loadPaidPayments() {
        loadPaymentsByStatus("paid");
    }

    public void loadAllPayments() {
        loadPaymentsByStatus("all");
    }

    // ========== PAYMENT ACTIONS ==========

    /**
     * Process payment (pending -> processing)
     */
    public void processPayment(int paymentId) {
        paymentRepository.processPayment(paymentId, new PaymentRepository.OnPaymentActionListener() {
            @Override
            public void onSuccess(String message) {
                statusMessage.setValue(message);
            }

            @Override
            public void onError(String error) {
                statusMessage.setValue(error);
            }
        });
    }

    /**
     * Complete payment (processing -> paid)
     */
    public void completePayment(int paymentId) {
        paymentRepository.completePayment(paymentId, new PaymentRepository.OnPaymentActionListener() {
            @Override
            public void onSuccess(String message) {
                statusMessage.setValue(message);
            }

            @Override
            public void onError(String error) {
                statusMessage.setValue(error);
            }
        });
    }

    /**
     * Fail payment (mark as failed)
     */
    public void failPayment(int paymentId) {
        paymentRepository.failPayment(paymentId, new PaymentRepository.OnPaymentActionListener() {
            @Override
            public void onSuccess(String message) {
                statusMessage.setValue(message);
            }

            @Override
            public void onError(String error) {
                statusMessage.setValue(error);
            }
        });
    }

    /**
     * Create new payment
     */
    public void createPayment(Payment payment) {
        paymentRepository.createPayment(payment, new PaymentRepository.OnPaymentActionListener() {
            @Override
            public void onSuccess(String message) {
                statusMessage.setValue(message);
            }

            @Override
            public void onError(String error) {
                statusMessage.setValue(error);
            }
        });
    }

    // ========== REFRESH & STATE MANAGEMENT ==========

    /**
     * Manual refresh current data
     */
    public void refreshPayments() {
        paymentRepository.refreshCurrentData();
    }

    /**
     * Enable/disable auto refresh
     */
    public void enableAutoRefresh(boolean enabled) {
        paymentRepository.enableAutoRefresh(enabled);
    }

    /**
     * Clear error state
     */
    public void clearError() {
        paymentRepository.clearError();
    }

    /**
     * Clear all states
     */
    public void clearAllStates() {
        paymentRepository.clearAllStates();
    }

    // ========== STATE GETTERS ==========

    public String getCurrentStatus() {
        return paymentRepository.getCurrentStatus();
    }

    public int getCurrentPage() {
        return paymentRepository.getCurrentPage();
    }

    public int getCurrentPageSize() {
        return paymentRepository.getCurrentPageSize();
    }

    // ========== PAYMENT STATISTICS ==========

    /**
     * Load payment statistics for dashboard
     */
    public void loadPaymentStatistics() {
        loadPendingPayments();
        loadPaidPayments();
    }

    // ========== SEARCH & FILTER ==========

    /**
     * Search payments by customer name or order ID
     */
    public void searchPayments(String query) {
        // For now, load all and filter in UI
        // In future, can implement server-side search
        loadAllPayments();
    }

    /**
     * Filter payments by date range
     */
    public void filterPaymentsByDateRange(String startDate, String endDate) {
        // Future implementation - server-side date filtering
        loadAllPayments();
    }

    /**
     * Filter payments by amount range
     */
    public void filterPaymentsByAmountRange(double minAmount, double maxAmount) {
        // Future implementation - server-side amount filtering
        loadAllPayments();
    }

    // ========== HELPER METHODS ==========

    /**
     * Get payment status display text
     */
    public String getPaymentStatusDisplayText(String status) {
        if (status == null) return "Không xác định";

        switch (status.toLowerCase()) {
            case "pending":
                return "Chờ thanh toán";
            case "processing":
                return "Đang xử lý";
            case "paid":
                return "Đã thanh toán";
            case "failed":
                return "Thất bại";
            case "refunded":
                return "Đã hoàn tiền";
            case "cancelled":
                return "Đã hủy";
            default:
                return status;
        }
    }

    /**
     * Get payment method display text
     */
    public String getPaymentMethodDisplayText(String method) {
        if (method == null) return "Không xác định";

        switch (method.toLowerCase()) {
            case "cash":
                return "Tiền mặt";
            case "digital_wallet":
                return "Ví điện tử";
            case "credit_card":
                return "Thẻ tín dụng";
            case "debit_card":
                return "Thẻ ghi nợ";
            case "bank_transfer":
                return "Chuyển khoản";
            default:
                return method;
        }
    }

    /**
     * Check if payment can be processed
     */
    public boolean canProcessPayment(Payment payment) {
        return payment != null &&
                "pending".equalsIgnoreCase(payment.getPaymentStatus());
    }

    /**
     * Check if payment can be completed
     */
    public boolean canCompletePayment(Payment payment) {
        return payment != null &&
                ("pending".equalsIgnoreCase(payment.getPaymentStatus()) ||
                        "processing".equalsIgnoreCase(payment.getPaymentStatus()));
    }

    /**
     * Check if payment can be marked as failed
     */
    public boolean canFailPayment(Payment payment) {
        return payment != null &&
                ("pending".equalsIgnoreCase(payment.getPaymentStatus()) ||
                        "processing".equalsIgnoreCase(payment.getPaymentStatus()));
    }

    // ========== CLEANUP ==========

    @Override
    protected void onCleared() {
        super.onCleared();
        // Disable auto refresh when ViewModel is cleared
        enableAutoRefresh(false);

        // Clear status message
        statusMessage.setValue(null);
    }
}