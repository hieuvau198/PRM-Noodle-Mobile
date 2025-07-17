package com.example.prm_v3.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.prm_v3.api.ApiClient;
import com.example.prm_v3.api.ApiService;
import com.example.prm_v3.model.Payment;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentRepository {
    private static final String TAG = "PaymentRepository";
    private static PaymentRepository instance;
    private ApiService apiService;

    // LiveData for UI observation
    private MutableLiveData<List<Payment>> paymentsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    // Track current state for pagination
    private String currentStatus = "all";
    private int currentPage = 1;
    private int currentPageSize = 20;
    private boolean isRefreshEnabled = true;

    private PaymentRepository() {
        apiService = ApiClient.getApiService();
    }

    public static synchronized PaymentRepository getInstance() {
        if (instance == null) {
            instance = new PaymentRepository();
        }
        return instance;
    }

    // Getters for LiveData
    public LiveData<List<Payment>> getPaymentsLiveData() {
        return paymentsLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // ========== FETCH PAYMENTS ==========

    /**
     * Fetch all payments with pagination
     */
    public void fetchPayments(int page, int pageSize) {
        Log.d(TAG, "fetchPayments - page: " + page + ", pageSize: " + pageSize);

        setCurrentFilter("all", page, pageSize);
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        Call<List<Payment>> call = apiService.getPayments();
        executeCall(call, "fetchPayments");
    }

    /**
     * Fetch payments with status filter
     */
    public void fetchPaymentsByStatus(String status, int page, int pageSize) {
        Log.d(TAG, "fetchPaymentsByStatus - status: " + status);

        setCurrentFilter(status, page, pageSize);
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        // For now, fetch all and filter locally since no direct status endpoints exist
        Call<List<Payment>> call = apiService.getPayments();
        call.enqueue(new Callback<List<Payment>>() {
            @Override
            public void onResponse(Call<List<Payment>> call, Response<List<Payment>> response) {
                loadingLiveData.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Payment> allPayments = response.body();
                    List<Payment> filteredPayments = filterPaymentsByStatus(allPayments, status);

                    Log.d(TAG, "fetchPaymentsByStatus success: " + filteredPayments.size() + " payments");
                    paymentsLiveData.setValue(filteredPayments);
                    errorLiveData.setValue(null);
                } else {
                    String errorMsg = handleErrorResponse(response.code());
                    Log.e(TAG, "fetchPaymentsByStatus error: " + errorMsg);
                    errorLiveData.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<List<Payment>> call, Throwable t) {
                loadingLiveData.setValue(false);
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "fetchPaymentsByStatus failure: " + errorMsg, t);
                errorLiveData.setValue(errorMsg);
            }
        });
    }

    /**
     * Filter payments by status locally
     */
    private List<Payment> filterPaymentsByStatus(List<Payment> allPayments, String status) {
        if (status == null || status.equals("all")) {
            return allPayments;
        }

        List<Payment> filtered = new ArrayList<>();
        for (Payment payment : allPayments) {
            if (payment.getPaymentStatus() != null &&
                    payment.getPaymentStatus().equalsIgnoreCase(status)) {
                filtered.add(payment);
            }
        }
        return filtered;
    }

    // ========== PAYMENT PROCESSING ==========

    /**
     * Process payment (mark as processing)
     */
    public void processPayment(int paymentId, OnPaymentActionListener listener) {
        Log.d(TAG, "processPayment: " + paymentId);

        Call<Payment> call = apiService.processPayment(paymentId);
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (listener != null) {
                        listener.onSuccess("Thanh toán đã được xử lý");
                    }
                    refreshCurrentData();
                } else {
                    String errorMsg = handleErrorResponse(response.code());
                    if (listener != null) {
                        listener.onError(errorMsg);
                    }
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                if (listener != null) {
                    listener.onError(errorMsg);
                }
            }
        });
    }

    /**
     * Complete payment (mark as paid)
     */
    public void completePayment(int paymentId, OnPaymentActionListener listener) {
        Log.d(TAG, "completePayment: " + paymentId);

        Call<Payment> call = apiService.completePayment(paymentId);
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (listener != null) {
                        listener.onSuccess("Thanh toán đã hoàn tất");
                    }
                    refreshCurrentData();
                } else {
                    String errorMsg = handleErrorResponse(response.code());
                    if (listener != null) {
                        listener.onError(errorMsg);
                    }
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                if (listener != null) {
                    listener.onError(errorMsg);
                }
            }
        });
    }

    /**
     * Fail payment (mark as failed)
     */
    public void failPayment(int paymentId, OnPaymentActionListener listener) {
        Log.d(TAG, "failPayment: " + paymentId);

        Call<Payment> call = apiService.failPayment(paymentId);
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (listener != null) {
                        listener.onSuccess("Thanh toán đã được đánh dấu thất bại");
                    }
                    refreshCurrentData();
                } else {
                    String errorMsg = handleErrorResponse(response.code());
                    if (listener != null) {
                        listener.onError(errorMsg);
                    }
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                if (listener != null) {
                    listener.onError(errorMsg);
                }
            }
        });
    }

    /**
     * Create new payment
     */
    public void createPayment(Payment payment, OnPaymentActionListener listener) {
        Log.d(TAG, "createPayment for order: " + payment.getOrderId());

        Call<Payment> call = apiService.createPayment(payment);
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (listener != null) {
                        listener.onSuccess("Thanh toán đã được tạo");
                    }
                    refreshCurrentData();
                } else {
                    String errorMsg = handleErrorResponse(response.code());
                    if (listener != null) {
                        listener.onError(errorMsg);
                    }
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                if (listener != null) {
                    listener.onError(errorMsg);
                }
            }
        });
    }

    // ========== HELPER METHODS ==========

    private void executeCall(Call<List<Payment>> call, String operation) {
        call.enqueue(new Callback<List<Payment>>() {
            @Override
            public void onResponse(Call<List<Payment>> call, Response<List<Payment>> response) {
                loadingLiveData.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Payment> payments = response.body();
                    Log.d(TAG, operation + " success: " + payments.size() + " payments");
                    paymentsLiveData.setValue(payments);
                    errorLiveData.setValue(null);
                } else {
                    String errorMsg = handleErrorResponse(response.code());
                    Log.e(TAG, operation + " error: " + errorMsg);
                    errorLiveData.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<List<Payment>> call, Throwable t) {
                loadingLiveData.setValue(false);
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, operation + " failure: " + errorMsg, t);
                errorLiveData.setValue(errorMsg);
            }
        });
    }

    private String handleErrorResponse(int code) {
        switch (code) {
            case 400:
                return "Yêu cầu không hợp lệ";
            case 401:
                return "Phiên đăng nhập đã hết hạn";
            case 403:
                return "Không có quyền truy cập";
            case 404:
                return "Không tìm thấy thanh toán";
            case 500:
                return "Lỗi máy chủ, vui lòng thử lại";
            default:
                return "Lỗi không xác định: " + code;
        }
    }

    // ========== STATE MANAGEMENT ==========

    private void setCurrentFilter(String status, int page, int pageSize) {
        this.currentStatus = status;
        this.currentPage = page;
        this.currentPageSize = pageSize;
        Log.d(TAG, "Current filter set: " + status + ", page: " + page);
    }

    public void enableAutoRefresh(boolean enabled) {
        this.isRefreshEnabled = enabled;
        Log.d(TAG, "Auto refresh: " + (enabled ? "enabled" : "disabled"));
    }

    public void refreshCurrentData() {
        Log.d(TAG, "Manual refresh requested for: " + currentStatus);
        if (currentStatus.equals("all")) {
            fetchPayments(1, currentPageSize);
        } else {
            fetchPaymentsByStatus(currentStatus, 1, currentPageSize);
        }
    }

    public void clearError() {
        errorLiveData.setValue(null);
        Log.d(TAG, "Error state cleared");
    }

    public void clearAllStates() {
        errorLiveData.setValue(null);
        loadingLiveData.setValue(false);
        Log.d(TAG, "All states cleared");
    }

    // ========== GETTERS ==========

    public String getCurrentStatus() { return currentStatus; }
    public int getCurrentPage() { return currentPage; }
    public int getCurrentPageSize() { return currentPageSize; }

    // ========== INTERFACES ==========

    public interface OnPaymentActionListener {
        void onSuccess(String message);
        void onError(String error);
    }
}