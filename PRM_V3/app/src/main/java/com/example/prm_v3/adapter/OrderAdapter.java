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
import com.example.prm_v3.utils.StatusHelper;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private static final String TAG = "OrderAdapter";

    private Context context;
    private List<Order> orders;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onConfirmOrder(Order order);
        void onCancelOrder(Order order);
        void onOrderClick(Order order);
    }

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders != null ? orders : new ArrayList<>();
        Log.d(TAG, "OrderAdapter created with " + this.orders.size() + " orders");
    }

    public void setOnOrderActionListener(OnOrderActionListener listener) {
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
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
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

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderTitle;
        private TextView tvOrderStatus;
        private TextView tvCustomerName;
        private TextView tvOrderDate;
        private TextView tvTotalAmount;
        private TextView tvPaymentMethod;
        private TextView tvItemCount;
        private TextView tvPaymentStatus;
        private Button btnConfirmOrder;
        private Button btnCancelOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderTitle = itemView.findViewById(R.id.tv_order_title);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
            tvPaymentStatus = itemView.findViewById(R.id.tv_payment_status);
            btnConfirmOrder = itemView.findViewById(R.id.btn_confirm_order);
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

            // Order header
            tvOrderTitle.setText("Đơn hàng #" + order.getOrderId());

            // Use StatusHelper for status display
            String status = order.getOrderStatus();
            tvOrderStatus.setText(StatusHelper.getStatusDisplayText(status));
            tvOrderStatus.setTextColor(ContextCompat.getColor(context, StatusHelper.getStatusColor(status)));
            tvOrderStatus.setBackground(ContextCompat.getDrawable(context, StatusHelper.getStatusBackground(status)));

            // Customer info
            String customerText = "Khách hàng: " + (order.getUserName() != null ? order.getUserName() : "N/A");
            tvCustomerName.setText(customerText);
            tvOrderDate.setText("Ngày đặt: " + order.getFormattedDate());

            // Order details
            tvTotalAmount.setText(order.getFormattedAmount());
            tvPaymentMethod.setText(order.getPaymentMethodText());

            // Item count - try API first, then calculate
            int totalItems = order.getTotalItemsFromAPI();
            if (totalItems <= 0) {
                totalItems = calculateTotalItems(order);
            }
            tvItemCount.setText(totalItems + " món");
            Log.d(TAG, "Set item count: " + totalItems + " món");

            tvPaymentStatus.setText(order.getPaymentStatusText());

            // Setup buttons using StatusHelper
            setupButtons(order);
        }

        private int calculateTotalItems(Order order) {
            int total = 0;

            // Count items from orderItems
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                for (var item : order.getOrderItems()) {
                    total += item.getQuantity();
                }
                Log.d(TAG, "Items from orderItems: " + total);
            }

            // Count items from orderCombos
            if (order.getOrderCombos() != null && !order.getOrderCombos().isEmpty()) {
                for (var combo : order.getOrderCombos()) {
                    total += combo.getQuantity();
                }
                Log.d(TAG, "Items from orderCombos added, total now: " + total);
            }

            Log.d(TAG, "Calculated total items: " + total);
            return total;
        }

        private void setupButtons(Order order) {
            String status = order.getOrderStatus();

            // Check if order can be updated or cancelled using StatusHelper
            boolean canUpdate = StatusHelper.canUpdateToNextStatus(status);
            boolean canCancel = StatusHelper.canCancelOrder(status);

            if (canUpdate) {
                btnConfirmOrder.setVisibility(View.VISIBLE);
                btnConfirmOrder.setText(StatusHelper.getActionButtonText(status));
                setupButtonClickListeners(order);
            } else {
                btnConfirmOrder.setVisibility(View.GONE);
            }

            if (canCancel) {
                btnCancelOrder.setVisibility(View.VISIBLE);
                btnCancelOrder.setText("Hủy đơn");
                setupButtonClickListeners(order);
            } else {
                btnCancelOrder.setVisibility(View.GONE);
            }
            switch (status.toLowerCase()) {
                case "pending":
                    btnConfirmOrder.setVisibility(View.VISIBLE);
                    btnCancelOrder.setVisibility(View.VISIBLE);
                    btnConfirmOrder.setText("Xác nhận");
                    setupButtonClickListeners(order);
                    break;

                case "confirmed":
                case "preparing":
                    btnConfirmOrder.setVisibility(View.VISIBLE);
                    btnCancelOrder.setVisibility(View.GONE);
                    btnConfirmOrder.setText(status.equalsIgnoreCase("confirmed") ? "Chuẩn bị" : "Giao hàng");
                    setupButtonClickListeners(order);
                    break;

                case "delivered":
                case "completed":
                case "cancelled":
                    btnConfirmOrder.setVisibility(View.GONE);
                    btnCancelOrder.setVisibility(View.GONE);
                    break;

                default:
                    btnConfirmOrder.setVisibility(View.GONE);
                    btnCancelOrder.setVisibility(View.GONE);
                    break;
            }
            // Special case for final statuses
            if (StatusHelper.isFinalStatus(status)) {
                btnConfirmOrder.setVisibility(View.GONE);
                btnCancelOrder.setVisibility(View.GONE);
            }
        }

        private void setupButtonClickListeners(Order order) {
            btnConfirmOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConfirmOrder(order);
                }
            });

            btnCancelOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelOrder(order);
                }
            });
        }
    }
}