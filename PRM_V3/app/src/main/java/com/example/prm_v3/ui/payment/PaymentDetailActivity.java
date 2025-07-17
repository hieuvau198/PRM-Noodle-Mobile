package com.example.prm_v3.ui.payment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm_v3.R;
import com.example.prm_v3.api.ApiClient;
import com.example.prm_v3.api.ApiService;
import com.example.prm_v3.databinding.ActivityPaymentDetailBinding;
import com.example.prm_v3.model.Payment;
import com.example.prm_v3.utils.StatusHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentDetailActivity extends AppCompatActivity {
    private static final String TAG = "PaymentDetailActivity";
    private static final String EXTRA_PAYMENT_ID = "extra_payment_id";

    private ActivityPaymentDetailBinding binding;
    private ApiService apiService;
    private int paymentId;
    private Payment currentPayment;

    public static Intent newIntent(Context context, int paymentId) {
        Intent intent = new Intent(context, PaymentDetailActivity.class);
        intent.putExtra(EXTRA_PAYMENT_ID, paymentId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        paymentId = getIntent().getIntExtra(EXTRA_PAYMENT_ID, -1);
        if (paymentId == -1) {
            finish();
            return;
        }

        setupToolbar();
        setupApiService();
        setupClickListeners();

        loadPaymentDetail();
    }

    private void setupToolbar() {
        if (getSupportActionBar() == null) {
            setSupportActionBar(binding.toolbar);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết thanh toán #" + paymentId);
        }
    }

    private void setupApiService() {
        apiService = ApiClient.getApiService();
    }

    private void setupClickListeners() {
        binding.btnProcessPayment.setOnClickListener(v -> {
            if (currentPayment != null) {
                processPayment();
            }
        });

        binding.btnCompletePayment.setOnClickListener(v -> {
            if (currentPayment != null) {
                completePayment();
            }
        });

        binding.btnFailPayment.setOnClickListener(v -> {
            if (currentPayment != null) {
                showFailPaymentDialog();
            }
        });

        binding.fabRefresh.setOnClickListener(v -> loadPaymentDetail());
    }

    private void loadPaymentDetail() {
        Log.d(TAG, "Loading payment detail for ID: " + paymentId);

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.scrollView.setVisibility(View.GONE);

        Call<Payment> call = apiService.getPaymentById(paymentId);
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    currentPayment = response.body();
                    updateUI(currentPayment);
                    binding.scrollView.setVisibility(View.VISIBLE);
                } else {
                    String errorMsg = "Không thể tải chi tiết thanh toán";
                    if (response.code() == 404) {
                        errorMsg = "Không tìm thấy thanh toán";
                    } else if (response.code() == 403) {
                        errorMsg = "Không có quyền truy cập thanh toán này";
                    }
                    Toast.makeText(PaymentDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(PaymentDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Payment payment) {
        if (payment == null) return;

        // Payment Information
        binding.tvPaymentId.setText("Thanh toán #" + payment.getPaymentId());
        binding.tvOrderId.setText("Đơn hàng #" + payment.getOrderId());
        binding.tvAmount.setText(String.format("%.0f₫", payment.getPaymentAmount()));

        // Payment Status
        String status = payment.getPaymentStatus();
        binding.tvStatus.setText(getStatusDisplayText(status));
        binding.tvStatus.setTextColor(getStatusColor(status));

        // Payment Method
        binding.tvMethod.setText(getMethodDisplayText(payment.getPaymentMethod()));

        // Customer Information
        binding.tvCustomerName.setText(payment.getCustomerName() != null ?
                payment.getCustomerName() : "N/A");
        binding.tvCustomerId.setText("ID: " + payment.getCustomerUserId());

        // Staff Information
        if (payment.getStaffName() != null && !payment.getStaffName().isEmpty()) {
            binding.layoutStaffInfo.setVisibility(View.VISIBLE);
            binding.tvStaffName.setText(payment.getStaffName());
            binding.tvStaffId.setText("ID: " + payment.getStaffUserId());
        } else {
            binding.layoutStaffInfo.setVisibility(View.GONE);
        }

        // Transaction Reference
        if (payment.getTransactionReference() != null && !payment.getTransactionReference().isEmpty()) {
            binding.tvTransactionRef.setText(payment.getTransactionReference());
        } else {
            binding.tvTransactionRef.setText("Chưa có");
        }

        // Dates
        binding.tvPaymentDate.setText(formatDate(payment.getPaymentDate()));
        binding.tvCreatedAt.setText(formatDate(payment.getCreatedAt()));

        // Processed Date
        if (payment.getProcessedAt() != null && !payment.getProcessedAt().isEmpty()) {
            binding.layoutProcessedDate.setVisibility(View.VISIBLE);
            binding.tvProcessedAt.setText(formatDate(payment.getProcessedAt()));
        } else {
            binding.layoutProcessedDate.setVisibility(View.GONE);
        }

        // Completed Date
        if (payment.getCompletedAt() != null && !payment.getCompletedAt().isEmpty()) {
            binding.layoutCompletedDate.setVisibility(View.VISIBLE);
            binding.tvCompletedAt.setText(formatDate(payment.getCompletedAt()));
        } else {
            binding.layoutCompletedDate.setVisibility(View.GONE);
        }

        // Deletion Information
        if (payment.isDeleted()) {
            binding.layoutDeletionInfo.setVisibility(View.VISIBLE);
            binding.tvDeletionReason.setText(payment.getDeletionReason() != null ?
                    payment.getDeletionReason() : "Không có lý do");
        } else {
            binding.layoutDeletionInfo.setVisibility(View.GONE);
        }

        // Update Action Buttons
        updateActionButtons(status);
    }

    private void updateActionButtons(String status) {
        // Process Button
        if (canProcessPayment(status)) {
            binding.btnProcessPayment.setVisibility(View.VISIBLE);
            binding.btnProcessPayment.setText("Xử lý thanh toán");
        } else {
            binding.btnProcessPayment.setVisibility(View.GONE);
        }

        // Complete Button
        if (canCompletePayment(status)) {
            binding.btnCompletePayment.setVisibility(View.VISIBLE);
            binding.btnCompletePayment.setText("Hoàn tất thanh toán");
        } else {
            binding.btnCompletePayment.setVisibility(View.GONE);
        }

        // Fail Button
        if (canFailPayment(status)) {
            binding.btnFailPayment.setVisibility(View.VISIBLE);
            binding.btnFailPayment.setText("Đánh dấu thất bại");
        } else {
            binding.btnFailPayment.setVisibility(View.GONE);
        }

        // Hide button container if no buttons are visible
        boolean hasVisibleButtons =
                binding.btnProcessPayment.getVisibility() == View.VISIBLE ||
                        binding.btnCompletePayment.getVisibility() == View.VISIBLE ||
                        binding.btnFailPayment.getVisibility() == View.VISIBLE;

        binding.layoutActionButtons.setVisibility(hasVisibleButtons ? View.VISIBLE : View.GONE);
    }

    // ========== PAYMENT ACTIONS ==========

    private void processPayment() {
        Log.d(TAG, "Processing payment: " + paymentId);

        binding.progressBar.setVisibility(View.VISIBLE);

        Call<Payment> call = apiService.processPayment(paymentId);
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    currentPayment = response.body();
                    updateUI(currentPayment);
                    Toast.makeText(PaymentDetailActivity.this,
                            "Thanh toán đã được xử lý", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Lỗi khi xử lý thanh toán";
                    if (response.code() == 400) {
                        errorMsg = "Không thể xử lý thanh toán từ trạng thái hiện tại";
                    }
                    Toast.makeText(PaymentDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(PaymentDetailActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completePayment() {
        Log.d(TAG, "Completing payment: " + paymentId);

        binding.progressBar.setVisibility(View.VISIBLE);

        Call<Payment> call = apiService.completePayment(paymentId);
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    currentPayment = response.body();
                    updateUI(currentPayment);
                    Toast.makeText(PaymentDetailActivity.this,
                            "Thanh toán đã hoàn tất", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Lỗi khi hoàn tất thanh toán";
                    if (response.code() == 400) {
                        errorMsg = "Không thể hoàn tất thanh toán từ trạng thái hiện tại";
                    }
                    Toast.makeText(PaymentDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(PaymentDetailActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFailPaymentDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn đánh dấu thanh toán này là thất bại?")
                .setPositiveButton("Đồng ý", (dialog, which) -> failPayment())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void failPayment() {
        Log.d(TAG, "Failing payment: " + paymentId);

        binding.progressBar.setVisibility(View.VISIBLE);

        Call<Payment> call = apiService.failPayment(paymentId);
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    currentPayment = response.body();
                    updateUI(currentPayment);
                    Toast.makeText(PaymentDetailActivity.this,
                            "Thanh toán đã được đánh dấu thất bại", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Lỗi khi cập nhật trạng thái thanh toán";
                    if (response.code() == 400) {
                        errorMsg = "Không thể đánh dấu thất bại từ trạng thái hiện tại";
                    }
                    Toast.makeText(PaymentDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(PaymentDetailActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ========== HELPER METHODS ==========

    private String getStatusDisplayText(String status) {
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

    private int getStatusColor(String status) {
        if (status == null) return getColor(R.color.gray_600);

        switch (status.toLowerCase()) {
            case "pending":
                return getColor(R.color.orange_600);
            case "processing":
                return getColor(R.color.blue_600);
            case "paid":
                return getColor(R.color.green_600);
            case "failed":
                return getColor(R.color.red_600);
            case "refunded":
                return getColor(R.color.purple_600);
            case "cancelled":
                return getColor(R.color.gray_600);
            default:
                return getColor(R.color.gray_600);
        }
    }

    private String getMethodDisplayText(String method) {
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

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty() || dateString.equals("null")) {
            return "Chưa có";
        }
        try {
            // Handle both ISO format and simple format
            return dateString.replace("T", " ").substring(0,
                    Math.min(16, dateString.replace("T", " ").length()));
        } catch (Exception e) {
            return dateString;
        }
    }

    private boolean canProcessPayment(String status) {
        return "pending".equalsIgnoreCase(status);
    }

    private boolean canCompletePayment(String status) {
        return "pending".equalsIgnoreCase(status) ||
                "processing".equalsIgnoreCase(status);
    }

    private boolean canFailPayment(String status) {
        return "pending".equalsIgnoreCase(status) ||
                "processing".equalsIgnoreCase(status);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}