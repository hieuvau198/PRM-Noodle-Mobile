package com.example.prm_v3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_v3.R;
import com.example.prm_v3.model.Payment;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import androidx.core.content.ContextCompat;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {
    private List<Payment> payments = new ArrayList<>();
    private Context context;

    public PaymentAdapter() {}
    public PaymentAdapter(Context context) {
        this.context = context;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments != null ? payments : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        Payment payment = payments.get(position);
        holder.bind(payment);
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    class PaymentViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvPaymentId, tvOrderId, tvCustomerName, tvAmount, tvStatus, tvMethod, tvDate;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPaymentId = itemView.findViewById(R.id.tvPaymentId);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvMethod = itemView.findViewById(R.id.tvMethod);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        public void bind(Payment payment) {
            tvPaymentId.setText("#" + payment.getPaymentId());
            tvOrderId.setText("Đơn hàng: " + payment.getOrderId());
            tvCustomerName.setText("Khách: " + payment.getCustomerName());
            tvAmount.setText(String.format("%.0f₫", payment.getPaymentAmount()));
            tvStatus.setText(getStatusText(payment.getPaymentStatus()));
            tvStatus.setTextColor(getStatusColor(payment.getPaymentStatus()));
            tvMethod.setText(getMethodText(payment.getPaymentMethod()));
            tvDate.setText(payment.getPaymentDate());
        }

        private String getStatusText(String status) {
            if (status == null) return "Không xác định";
            switch (status.toLowerCase()) {
                case "pending": return "Chờ thanh toán";
                case "paid": return "Đã thanh toán";
                case "failed": return "Thất bại";
                case "refunded": return "Đã hoàn tiền";
                default: return status;
            }
        }

        private int getStatusColor(String status) {
            if (context == null || status == null) return 0xFF888888;
            switch (status.toLowerCase()) {
                case "pending": return ContextCompat.getColor(context, R.color.orange_600);
                case "paid": return ContextCompat.getColor(context, R.color.green_600);
                case "failed": return ContextCompat.getColor(context, R.color.red_600);
                case "refunded": return ContextCompat.getColor(context, R.color.blue_600);
                default: return ContextCompat.getColor(context, R.color.gray_600);
            }
        }

        private String getMethodText(String method) {
            if (method == null) return "Không xác định";
            switch (method.toLowerCase()) {
                case "cash": return "Tiền mặt";
                case "digital_wallet": return "Ví điện tử";
                case "credit_card": return "Thẻ tín dụng";
                default: return method;
            }
        }
    }
} 