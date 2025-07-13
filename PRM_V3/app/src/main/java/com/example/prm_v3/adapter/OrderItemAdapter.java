package com.example.prm_v3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_v3.R;
import com.example.prm_v3.model.OrderItem; // ← Thay đổi import
import com.example.prm_v3.model.OrderItemTopping; // ← Thêm import mới

import java.util.ArrayList;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private List<OrderItem> orderItems = new ArrayList<>(); // ← Thay đổi type

    public void updateItems(List<OrderItem> newItems) { // ← Thay đổi parameter type
        this.orderItems.clear();
        this.orderItems.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = orderItems.get(position); // ← Thay đổi type
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProductName;
        private TextView tvQuantity;
        private TextView tvUnitPrice;
        private TextView tvSubtotal;
        private TextView tvToppings;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvUnitPrice = itemView.findViewById(R.id.tv_unit_price);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
            tvToppings = itemView.findViewById(R.id.tv_toppings);
        }

        public void bind(OrderItem item) { // ← Thay đổi parameter type
            tvProductName.setText(item.getProductName());
            tvQuantity.setText("x" + item.getQuantity());
            tvUnitPrice.setText(item.getFormattedUnitPrice());
            tvSubtotal.setText(item.getFormattedSubtotal());

            // Display toppings
            if (item.hasToppings()) {
                StringBuilder toppingsText = new StringBuilder("Topping: ");
                List<OrderItemTopping> toppings = item.getToppings(); // ← Thay đổi type

                for (int i = 0; i < toppings.size(); i++) {
                    OrderItemTopping topping = toppings.get(i); // ← Thay đổi type
                    toppingsText.append(topping.getToppingName());

                    if (topping.getQuantity() > 1) {
                        toppingsText.append(" (x").append(topping.getQuantity()).append(")");
                    }

                    if (i < toppings.size() - 1) {
                        toppingsText.append(", ");
                    }
                }
                tvToppings.setText(toppingsText.toString());
                tvToppings.setVisibility(View.VISIBLE);
            } else {
                tvToppings.setVisibility(View.GONE);
            }
        }
    }
}