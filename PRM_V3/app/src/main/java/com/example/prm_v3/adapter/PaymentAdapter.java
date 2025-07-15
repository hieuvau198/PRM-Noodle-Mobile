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

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {
    private List<Payment> payments = new ArrayList<>();

    public void setPayments(List<Payment> payments) {
        this.payments = payments != null ? payments : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
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

    static class PaymentViewHolder extends RecyclerView.ViewHolder {
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
            tvOrderId.setText("Đơn: " + payment.getOrderId());
            tvCustomerName.setText(payment.getCustomerName());
            tvAmount.setText(String.format("%.0f₫", payment.getPaymentAmount()));
            tvStatus.setText(payment.getPaymentStatus());
            tvMethod.setText(payment.getPaymentMethod());
            tvDate.setText(payment.getPaymentDate());
        }
    }
} 