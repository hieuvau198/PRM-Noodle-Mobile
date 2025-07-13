package com.example.prm_v3.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_v3.R;
import com.example.prm_v3.model.OrderCombo; // ← Thay đổi import này

import java.util.ArrayList;
import java.util.List;

public class OrderComboAdapter extends RecyclerView.Adapter<OrderComboAdapter.OrderComboViewHolder> {

    private List<OrderCombo> orderCombos = new ArrayList<>(); // ← Thay đổi type

    public void updateCombos(List<OrderCombo> newCombos) { // ← Thay đổi parameter type
        this.orderCombos.clear();
        this.orderCombos.addAll(newCombos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderComboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_combo, parent, false);
        return new OrderComboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderComboViewHolder holder, int position) {
        OrderCombo combo = orderCombos.get(position); // ← Thay đổi type
        holder.bind(combo);
    }

    @Override
    public int getItemCount() {
        return orderCombos.size();
    }

    static class OrderComboViewHolder extends RecyclerView.ViewHolder {
        private TextView tvComboName;
        private TextView tvQuantity;
        private TextView tvUnitPrice;
        private TextView tvSubtotal;

        public OrderComboViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComboName = itemView.findViewById(R.id.tv_combo_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvUnitPrice = itemView.findViewById(R.id.tv_unit_price);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
        }

        public void bind(OrderCombo combo) { // ← Thay đổi parameter type
            Log.d("OrderComboAdapter", "Combo name: " + combo.getComboName());
            tvComboName.setText(combo.getComboName() + " (Combo)");
            tvQuantity.setText("x" + combo.getQuantity());
            tvUnitPrice.setText(combo.getFormattedUnitPrice());
            tvSubtotal.setText(combo.getFormattedSubtotal());
        }
    }
}