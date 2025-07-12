package com.example.prm_noodle_mobile.customer.orderconfirm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.OrderCombo;
import com.example.prm_noodle_mobile.data.model.Combo;
import java.util.List;

public class OrderComboAdapter extends RecyclerView.Adapter<OrderComboAdapter.ViewHolder> {
    private List<OrderCombo> combos;
    private List<Combo> comboDetailList;

    public OrderComboAdapter(List<OrderCombo> combos, List<Combo> comboDetailList) {
        this.combos = combos;
        this.comboDetailList = comboDetailList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_combo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderCombo orderCombo = combos.get(position);
        Combo combo = getComboById(orderCombo.getComboId());
        if (combo != null) {
            holder.comboName.setText(combo.getComboName());
            holder.comboQuantity.setText("x" + orderCombo.getQuantity());
            holder.comboPrice.setText(String.format("%,d VND", combo.getPrice()));
        } else {
            holder.comboName.setText("Combo " + orderCombo.getComboId());
            holder.comboQuantity.setText("x" + orderCombo.getQuantity());
            holder.comboPrice.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return combos.size();
    }

    private Combo getComboById(int comboId) {
        if (comboDetailList != null) {
            for (Combo combo : comboDetailList) {
                if (combo.getComboId() == comboId) {
                    return combo;
                }
            }
        }
        return null;
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