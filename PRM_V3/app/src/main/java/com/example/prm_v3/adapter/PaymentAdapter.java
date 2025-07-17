package com.example.prm_v3.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_v3.R;
import com.example.prm_v3.model.Payment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {
    private static final String TAG = "PaymentAdapter";

    private List<Payment> payments = new ArrayList<>();
    private OnPaymentActionListener listener;

    public interface OnPaymentActionListener {
        void onProcessPayment(Payment payment);
        void onCompletePayment(Payment payment);
        void onFailPayment(Payment payment);
        void onPaymentClick(Payment payment);
    }

    public PaymentAdapter() {
        Log.d(TAG, "PaymentAdapter created");
    }

    public void setOnPaymentActionListener(OnPaymentActionListener listener) {
        this.listener = listener;
    }

    public void setPayments(List<Payment> payments) {
        Log.d(TAG, "setPayments called with " + (payments != null ? payments.size() : "null") + " payments");

        this.payments.clear();
        if (payments != null) {
            this.payments.addAll(payments);
            Log.d(TAG, "Payments updated, new size: " + this.payments.size());
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position: " + position + "/" + payments.size());

        if (position < payments.size()) {
            Payment payment = payments.get(position);
            Log.d(TAG, "Binding payment: ID=" + payment.getPaymentId() + ", Status=" + payment.getPaymentStatus());
            holder.bind(payment);
        } else {
            Log.w(TAG, "Position " + position + " is out of bounds for payments list size " + payments.size());
        }
    }

    @Override
    public int getItemCount() {
        int count = payments.size();
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    class PaymentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPaymentId;
        private TextView tvOrderId;
        private TextView tvCustomerName;
        private TextView tvAmount;
        private TextView tvStatus;
        private TextView tvMethod;
        private TextView tvDate;
        private TextView tvTransactionRef;
        private MaterialButton btnProcess;
        private MaterialButton btnComplete;
        private MaterialButton btnFail;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPaymentId = itemView.findViewById(R.id.tvPaymentId);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvMethod = itemView.findViewById(R.id.tvMethod);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTransactionRef = itemView.findViewById(R.id.tvTransactionRef);
            btnProcess = itemView.findViewById(R.id.btn_process_payment);
            btnComplete = itemView.findViewById(R.id.btn_complete_payment);
            btnFail = itemView.findViewById(R.id.btn_fail_payment);

            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null && position < payments.size()) {
                    listener.onPaymentClick(payments.get(position));
                }
            });
        }

        public void bind(Payment payment) {
            if (payment == null) {
                Log.w(TAG, "bind called with null payment");
                return;
            }

            Log.d(TAG, "Binding payment details: #" + payment.getPaymentId());

            // Basic payment information
            tvPaymentId.setText("Thanh toán #" + payment.getPaymentId());
            tvOrderId.setText("Đơn: #" + payment.getOrderId());

            // Customer name with prefix
            String customerName = payment.getCustomerName() != null ? payment.getCustomerName() : "N/A";
            tvCustomerName.setText("Khách hàng: " + customerName);

            // Amount formatting
            tvAmount.setText(String.format("%.0f₫", payment.getPaymentAmount()));

            // Payment status with color coding
            String status = payment.getPaymentStatus();
            tvStatus.setText(getStatusDisplayText(status));
            tvStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), getStatusColor(status)));
            tvStatus.setBackground(ContextCompat.getDrawable(itemView.getContext(), getStatusBackground(status)));

            // Payment method
            tvMethod.setText(getMethodDisplayText(payment.getPaymentMethod()));

            // Payment date (format if needed)
            String date = payment.getPaymentDate();
            if (date != null && date.length() >= 10) {
                tvDate.setText(date.substring(0, 10)); // Show only date part
            } else {
                tvDate.setText(date != null ? date : "N/A");
            }

            // Transaction reference
            String transactionRef = payment.getTransactionReference();
            if (tvTransactionRef != null) {
                if (transactionRef != null && !transactionRef.trim().isEmpty()) {
                    tvTransactionRef.setText(transactionRef);
                } else {
                    tvTransactionRef.setText("Chưa có");
                }
            }

            // Setup action buttons
            setupActionButtons(payment);
        }

        private void setupActionButtons(Payment payment) {
            String status = payment.getPaymentStatus();
            Log.d(TAG, "Setting up buttons for status: " + status);

            // Process Button
            if (btnProcess != null) {
                if (canProcessPayment(status)) {
                    btnProcess.setVisibility(View.VISIBLE);
                    btnProcess.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onProcessPayment(payment);
                        }
                    });
                } else {
                    btnProcess.setVisibility(View.GONE);
                }
            }

            // Complete Button
            if (btnComplete != null) {
                if (canCompletePayment(status)) {
                    btnComplete.setVisibility(View.VISIBLE);
                    btnComplete.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onCompletePayment(payment);
                        }
                    });
                } else {
                    btnComplete.setVisibility(View.GONE);
                }
            }

            // Fail Button
            if (btnFail != null) {
                if (canFailPayment(status)) {
                    btnFail.setVisibility(View.VISIBLE);
                    btnFail.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onFailPayment(payment);
                        }
                    });
                } else {
                    btnFail.setVisibility(View.GONE);
                }
            }

            // Show/hide button container based on availability
            View buttonContainer = itemView.findViewById(R.id.layout_action_buttons);
            if (buttonContainer != null) {
                boolean hasAnyVisibleButton =
                        (btnProcess != null && btnProcess.getVisibility() == View.VISIBLE) ||
                                (btnComplete != null && btnComplete.getVisibility() == View.VISIBLE) ||
                                (btnFail != null && btnFail.getVisibility() == View.VISIBLE);

                buttonContainer.setVisibility(hasAnyVisibleButton ? View.VISIBLE : View.GONE);
            }
        }

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
            if (status == null) return R.color.gray_600;

            switch (status.toLowerCase()) {
                case "pending":
                    return R.color.orange_600;
                case "processing":
                    return R.color.blue_600;
                case "paid":
                    return R.color.green_600;
                case "failed":
                    return R.color.red_600;
                case "refunded":
                    return R.color.purple_600;
                case "cancelled":
                    return R.color.gray_600;
                default:
                    return R.color.gray_600;
            }
        }

        private int getStatusBackground(String status) {
            if (status == null) return R.drawable.bg_status_badge;

            switch (status.toLowerCase()) {
                case "pending":
                    return R.drawable.bg_status_pending;
                case "processing":
                    return R.drawable.bg_payment_processing;
                case "paid":
                    return R.drawable.bg_payment_paid;
                case "failed":
                    return R.drawable.bg_payment_failed;
                case "refunded":
                    return R.drawable.bg_status_cancelled;
                case "cancelled":
                    return R.drawable.bg_status_cancelled;
                default:
                    return R.drawable.bg_status_badge;
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

        // ========== BUTTON VISIBILITY LOGIC ==========

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
    }

    // ========== HELPER METHODS ==========

    /**
     * Update a specific payment in the list
     */
    public void updatePayment(Payment updatedPayment) {
        for (int i = 0; i < payments.size(); i++) {
            if (payments.get(i).getPaymentId() == updatedPayment.getPaymentId()) {
                payments.set(i, updatedPayment);
                notifyItemChanged(i);
                Log.d(TAG, "Payment updated at position: " + i);
                break;
            }
        }
    }

    /**
     * Remove a payment from the list
     */
    public void removePayment(int paymentId) {
        for (int i = 0; i < payments.size(); i++) {
            if (payments.get(i).getPaymentId() == paymentId) {
                payments.remove(i);
                notifyItemRemoved(i);
                Log.d(TAG, "Payment removed at position: " + i);
                break;
            }
        }
    }

    /**
     * Add a new payment to the list
     */
    public void addPayment(Payment payment) {
        payments.add(0, payment); // Add at the beginning
        notifyItemInserted(0);
        Log.d(TAG, "Payment added at position: 0");
    }

    /**
     * Get current payments list
     */
    public List<Payment> getPayments() {
        return new ArrayList<>(payments);
    }

    /**
     * Clear all payments
     */
    public void clearPayments() {
        int size = payments.size();
        payments.clear();
        notifyItemRangeRemoved(0, size);
        Log.d(TAG, "All payments cleared");
    }

    /**
     * Check if adapter is empty
     */
    public boolean isEmpty() {
        return payments.isEmpty();
    }

    /**
     * Get payment at specific position
     */
    public Payment getPaymentAt(int position) {
        if (position >= 0 && position < payments.size()) {
            return payments.get(position);
        }
        return null;
    }

    /**
     * Filter payments by status for local filtering
     */
    public void filterByStatus(String status) {
        // This can be implemented if needed for local filtering
        Log.d(TAG, "Filter by status: " + status);
    }
}