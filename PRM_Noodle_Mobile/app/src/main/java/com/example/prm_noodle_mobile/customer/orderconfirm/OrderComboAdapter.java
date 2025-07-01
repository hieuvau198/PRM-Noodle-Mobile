package com.example.prm_noodle_mobile.customer.orderconfirm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.OrderCombo;
import java.util.List;

public class OrderComboAdapter extends RecyclerView.Adapter<OrderComboAdapter.ViewHolder> {
    private List<OrderCombo> combos;

    public OrderComboAdapter(List<OrderCombo> combos) {
        this.combos = combos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_combo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderCombo combo = combos.get(position);
        holder.comboName.setText("Combo " + combo.getComboId());
        holder.comboQuantity.setText("x" + combo.getQuantity());
        holder.comboPrice.setText("..."); // Cập nhật giá nếu có
    }

    @Override
    public int getItemCount() {
        return combos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView comboName, comboQuantity, comboPrice;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            comboName = itemView.findViewById(R.id.combo_name);
            comboQuantity = itemView.findViewById(R.id.combo_quantity);
            comboPrice = itemView.findViewById(R.id.combo_price);
        }
    }
} 