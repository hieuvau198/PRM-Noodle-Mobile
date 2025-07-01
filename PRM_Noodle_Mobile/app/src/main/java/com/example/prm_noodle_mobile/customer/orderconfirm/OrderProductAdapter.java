package com.example.prm_noodle_mobile.customer.orderconfirm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.OrderItem;
import com.example.prm_noodle_mobile.data.model.ToppingOrder;
import java.util.List;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.ViewHolder> {
    private List<OrderItem> orderItems;

    public OrderProductAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);
        holder.productName.setText("Sản phẩm " + item.getProductId());
        holder.productQuantity.setText("x" + item.getQuantity());
        holder.productPrice.setText("..."); // Cập nhật giá nếu có
        holder.toppingContainer.removeAllViews();
        if (item.getToppings() != null) {
            for (ToppingOrder topping : item.getToppings()) {
                TextView tv = new TextView(holder.toppingContainer.getContext());
                tv.setText("+ Topping " + topping.getToppingId() + " x" + topping.getQuantity());
                holder.toppingContainer.addView(tv);
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productQuantity, productPrice;
        LinearLayout toppingContainer;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            productPrice = itemView.findViewById(R.id.product_price);
            toppingContainer = itemView.findViewById(R.id.topping_container);
        }
    }
} 