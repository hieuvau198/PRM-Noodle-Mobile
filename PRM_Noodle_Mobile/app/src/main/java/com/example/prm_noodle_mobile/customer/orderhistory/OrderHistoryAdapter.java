package com.example.prm_noodle_mobile.customer.orderhistory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.Order;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {
    private List<Order> orders;

    public OrderHistoryAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tvOrderId.setText(String.format("Đơn hàng #%d", order.getOrderId()));
        holder.tvOrderDate.setText(order.getOrderDate());
        holder.tvOrderStatus.setText(order.getFormattedStatus());
        holder.tvTotalItems.setText(String.format("%d món", order.getTotalItems()));
        holder.tvTotalAmount.setText(String.format(Locale.getDefault(), "%,.0fđ", order.getTotalAmount()));
        holder.tvPaymentMethod.setText(order.getFormattedPaymentMethod());

        // Hiển thị payment status với text đã format
        holder.tvPaymentStatus.setText(getFormattedPaymentStatus(order.getPaymentStatus()));

        // Debug: Log để kiểm tra payment status
        android.util.Log.d("OrderAdapter", "Order #" + order.getOrderId() + " - PaymentStatus: " + order.getPaymentStatus());

        // Set order status color
        int statusColor;
        switch (order.getOrderStatus() != null ? order.getOrderStatus().toLowerCase() : "") {
            case "delivered":
                statusColor = holder.itemView.getContext().getColor(R.color.status_delivered);
                break;
            case "confirmed":
                statusColor = holder.itemView.getContext().getColor(R.color.status_confirmed);
                break;
            case "cancelled":
                statusColor = holder.itemView.getContext().getColor(R.color.status_cancelled);
                break;
            default:
                statusColor = holder.itemView.getContext().getColor(R.color.status_pending);
        }
        holder.tvOrderStatus.setTextColor(statusColor);

        // Set payment status color - giống như order status
        int paymentStatusColor;
        switch (order.getPaymentStatus() != null ? order.getPaymentStatus().toLowerCase() : "") {
            case "paid":
            case "complete":  // Thêm case này
                paymentStatusColor = holder.itemView.getContext().getColor(R.color.payment_paid);
                break;
            case "pending":
                paymentStatusColor = holder.itemView.getContext().getColor(R.color.payment_pending);
                break;
            case "failed":
                paymentStatusColor = holder.itemView.getContext().getColor(R.color.payment_failed);
                break;
            case "refunded":
                paymentStatusColor = holder.itemView.getContext().getColor(R.color.payment_refunded);
                break;
            default:
                paymentStatusColor = holder.itemView.getContext().getColor(R.color.payment_pending);
        }
        holder.tvPaymentStatus.setTextColor(paymentStatusColor);
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    private String getFormattedPaymentStatus(String paymentStatus) {
        if (paymentStatus == null) return "Chưa rõ";
        switch (paymentStatus.toLowerCase()) {
            case "paid": return "Đã thanh toán";
            case "complete": return "Hoàn thành";  // Thêm case này
            case "pending": return "Chờ thanh toán";
            case "failed": return "Thất bại";
            case "refunded": return "Đã hoàn tiền";
            default: return paymentStatus;
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderStatus;
        TextView tvTotalItems, tvTotalAmount, tvPaymentMethod, tvPaymentStatus;

        OrderViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvTotalItems = itemView.findViewById(R.id.tv_total_items);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
            tvPaymentStatus = itemView.findViewById(R.id.tv_payment_status);
        }
    }
}