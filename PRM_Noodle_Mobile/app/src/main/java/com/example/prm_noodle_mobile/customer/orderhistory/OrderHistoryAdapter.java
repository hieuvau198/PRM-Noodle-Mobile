package com.example.prm_noodle_mobile.customer.orderhistory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.Order;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {
    private List<Order> orders;
    private SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

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
        holder.tvOrderStatus.setText(order.getOrderStatus());
        holder.tvTotalItems.setText(String.format("%d món", order.getTotalItems()));
        holder.tvTotalAmount.setText(String.format(Locale.getDefault(), "%,.0fđ", order.getTotalAmount()));
        holder.tvPaymentMethod.setText(order.getPaymentMethod());

        // Set status color
        int statusColor;
        switch (order.getOrderStatus()) {
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
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderStatus;
        TextView tvTotalItems, tvTotalAmount, tvPaymentMethod;

        OrderViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvTotalItems = itemView.findViewById(R.id.tv_total_items);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
        }
    }
}
