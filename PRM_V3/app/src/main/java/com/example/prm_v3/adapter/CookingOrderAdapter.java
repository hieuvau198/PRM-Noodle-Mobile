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
import com.example.prm_v3.model.Order;

import java.util.ArrayList;
import java.util.List;

public class CookingOrderAdapter extends RecyclerView.Adapter<CookingOrderAdapter.CookingOrderViewHolder> {
    private static final String TAG = "CookingOrderAdapter";

    private Context context;
    private List<Order> orders;
    private OnCookingOrderActionListener listener;

    public interface OnCookingOrderActionListener {
        void onMarkOrderReady(Order order);
        void onCancelOrder(Order order);
        void onOrderClick(Order order);
    }

    public CookingOrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders != null ? orders : new ArrayList<>();
        Log.d(TAG, "CookingOrderAdapter created with " + this.orders.size() + " orders");
    }

    public void setOnCookingOrderActionListener(OnCookingOrderActionListener listener) {
        this.listener = listener;
    }

    public void updateOrders(List<Order> newOrders) {
        Log.d(TAG, "updateOrders called with " + (newOrders != null ? newOrders.size() : "null") + " orders");

        if (newOrders != null) {
            this.orders.clear();
            this.orders.addAll(newOrders);
            Log.d(TAG, "Orders updated, new size: " + this.orders.size());
        } else {
            this.orders.clear();
            Log.d(TAG, "Orders cleared due to null input");
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CookingOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(context).inflate(R.layout.item_cooking_order, parent, false);
        return new CookingOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CookingOrderViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position: " + position + "/" + orders.size());

        if (position < orders.size()) {
            Order order = orders.get(position);
            Log.d(TAG, "Binding order: ID=" + order.getOrderId() + ", Status=" + order.getOrderStatus());
            holder.bind(order);
        } else {
            Log.w(TAG, "Position " + position + " is out of bounds for orders list size " + orders.size());
        }
    }

    @Override
    public int getItemCount() {
        int count = orders.size();
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    class CookingOrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderNumber;
        private TextView tvOrderStatus;
        private TextView tvCustomerName;
        private TextView tvOrderDate;
        private TextView tvTotalAmount;
        private TextView tvItemCount;
        private TextView tvPaymentMethod;
        private TextView tvPaymentStatus;
        private Button btnMarkReady;
        private Button btnCancelOrder;

        public CookingOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderNumber = itemView.findViewById(R.id.tv_order_number);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
            tvPaymentStatus = itemView.findViewById(R.id.tv_payment_status);
            btnMarkReady = itemView.findViewById(R.id.btn_mark_ready);
            btnCancelOrder = itemView.findViewById(R.id.btn_cancel_order);

            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null && position < orders.size()) {
                    listener.onOrderClick(orders.get(position));
                }
            });
        }

        public void bind(Order order) {
            if (order == null) {
                Log.w(TAG, "bind called with null order");
                return;
            }

            Log.d(TAG, "Binding order details: #" + order.getOrderId());

            // Order number
            tvOrderNumber.setText("Đơn hàng #" + order.getOrderId());

            // Order status
            String status = order.getOrderStatus();
            tvOrderStatus.setText(getStatusDisplayText(status));
            tvOrderStatus.setTextColor(ContextCompat.getColor(context, getStatusColor(status)));
            tvOrderStatus.setBackground(ContextCompat.getDrawable(context, getStatusBackground(status)));

            // Customer info
            tvCustomerName.setText("Khách hàng: " + (order.getUserName() != null ? order.getUserName() : "N/A"));
            tvOrderDate.setText("Ngày đặt: " + order.getFormattedDate());

            // Order details
            tvTotalAmount.setText(order.getFormattedAmount());
            tvPaymentMethod.setText(order.getPaymentMethodText());
            tvPaymentStatus.setText(order.getPaymentStatusText());

            // Item count
            int totalItems = order.getTotalItemsFromAPI();
            if (totalItems <= 0) {
                totalItems = calculateTotalItems(order);
            }
            tvItemCount.setText(totalItems + " món");

            // Setup buttons
            setupButtons(order);
        }

        private int calculateTotalItems(Order order) {
            int total = 0;

            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                for (var item : order.getOrderItems()) {
                    total += item.getQuantity();
                }
            }

            if (order.getOrderCombos() != null && !order.getOrderCombos().isEmpty()) {
                for (var combo : order.getOrderCombos()) {
                    total += combo.getQuantity();
                }
            }

            return total;
        }

        private void setupButtons(Order order) {
            // Always show "Sẵn sàng giao" button for preparing orders
            btnMarkReady.setVisibility(View.VISIBLE);
            btnMarkReady.setText("SẴN SÀNG GIAO");
            btnMarkReady.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMarkOrderReady(order);
                }
            });

            // Always show "Hủy đơn" button for preparing orders
            btnCancelOrder.setVisibility(View.VISIBLE);
            btnCancelOrder.setText("HỦY ĐƠN");
            btnCancelOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelOrder(order);
                }
            });
        }

        private String getStatusDisplayText(String status) {
            if (status == null) return "Không xác định";

            switch (status.toLowerCase()) {
                case "preparing":
                    return "Đang chuẩn bị";
                case "pending":
                    return "Chờ xác nhận";
                case "confirmed":
                    return "Đã xác nhận";
                case "ready":
                    return "Sẵn sàng giao";
                case "delivered":
                    return "Hoàn thành";
                case "cancelled":
                    return "Đã hủy";
                default:
                    return "Không xác định";
            }
        }

        private int getStatusColor(String status) {
            if (status == null) return R.color.gray_600;

            switch (status.toLowerCase()) {
                case "preparing":
                    return R.color.yellow_600;
                case "pending":
                    return R.color.orange_600;
                case "confirmed":
                    return R.color.blue_600;
                case "ready":
                    return R.color.purple_600;
                case "delivered":
                    return R.color.green_600;
                case "cancelled":
                    return R.color.red_600;
                default:
                    return R.color.gray_600;
            }
        }

        private int getStatusBackground(String status) {
            if (status == null) return R.drawable.bg_status_badge;

            switch (status.toLowerCase()) {
                case "preparing":
                    return R.drawable.bg_status_preparing;
                case "pending":
                    return R.drawable.bg_status_pending;
                case "confirmed":
                    return R.drawable.bg_status_confirmed;
                case "ready":
                    return R.drawable.bg_status_ready;
                case "delivered":
                    return R.drawable.bg_status_completed;
                case "cancelled":
                    return R.drawable.bg_status_cancelled;
                default:
                    return R.drawable.bg_status_badge;
            }
        }
    }
}