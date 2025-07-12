package com.example.prm_noodle_mobile.customer.cart;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.OrderItem;
import com.example.prm_noodle_mobile.data.model.ToppingOrder;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    private List<OrderItem> orderItems;

    public CartItemAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(orderItems.get(position));
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvProductName;
        private final TextView tvQuantity;
        private final TextView tvToppings;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvToppings = itemView.findViewById(R.id.tv_toppings);
        }

        public void bind(OrderItem item) {
            tvProductName.setText(item.getProductName());
            tvQuantity.setText("Số lượng: " + item.getQuantity());

            List<ToppingOrder> toppings = item.getToppings();
            if (toppings != null && !toppings.isEmpty()) {
                StringBuilder toppingNames = new StringBuilder("Topping: ");
                for (ToppingOrder topping : toppings) {
                    toppingNames.append("ID ").append(topping.getToppingId())
                            .append(" x").append(topping.getQuantity()).append(", ");
                }
                tvToppings.setText(toppingNames.substring(0, toppingNames.length() - 2));
            } else {
                tvToppings.setText("Không có topping");
            }
        }
    }
}

