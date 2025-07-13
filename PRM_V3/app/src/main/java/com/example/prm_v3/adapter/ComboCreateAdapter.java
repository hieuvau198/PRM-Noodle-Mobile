package com.example.prm_v3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_v3.R;
import com.example.prm_v3.model.Combo;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ComboCreateAdapter extends RecyclerView.Adapter<ComboCreateAdapter.ComboViewHolder> {

    private List<Combo> combos = new ArrayList<>();
    private OnComboQuantityChangeListener listener;

    public interface OnComboQuantityChangeListener {
        void onComboQuantityChanged(Combo combo, int quantity);
        int getCurrentComboQuantity(int comboId);
    }

    public ComboCreateAdapter(OnComboQuantityChangeListener listener) {
        this.listener = listener;
    }

    public void updateCombos(List<Combo> newCombos) {
        this.combos.clear();
        if (newCombos != null) {
            this.combos.addAll(newCombos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_combo_create, parent, false);
        return new ComboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComboViewHolder holder, int position) {
        Combo combo = combos.get(position);
        holder.bind(combo);
    }

    @Override
    public int getItemCount() {
        return combos.size();
    }

    class ComboViewHolder extends RecyclerView.ViewHolder {
        private TextView tvComboName;
        private TextView tvComboDescription;
        private TextView tvComboPrice;
        private TextView tvComboDiscount;
        private TextView tvQuantity;
        private MaterialButton btnMinus;
        private MaterialButton btnPlus;

        public ComboViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComboName = itemView.findViewById(R.id.tv_combo_name);
            tvComboDescription = itemView.findViewById(R.id.tv_combo_description);
            tvComboPrice = itemView.findViewById(R.id.tv_combo_price);
            tvComboDiscount = itemView.findViewById(R.id.tv_combo_discount);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
        }

        public void bind(Combo combo) {
            tvComboName.setText(combo.getComboName());

            if (combo.hasDescription()) {
                tvComboDescription.setText(combo.getDescription());
                tvComboDescription.setVisibility(View.VISIBLE);
            } else {
                tvComboDescription.setVisibility(View.GONE);
            }

            tvComboPrice.setText(combo.getFormattedPrice());

            // Show discount if available
            tvComboDiscount.setVisibility(View.VISIBLE);
            tvComboDiscount.setText("Tiết kiệm 15.000đ");

            // Update quantity display
            updateQuantityDisplay(combo);

            // Set click listeners
            setupClickListeners(combo);
        }

        private void updateQuantityDisplay(Combo combo) {
            int currentQuantity = listener != null ? listener.getCurrentComboQuantity(combo.getComboId()) : 0;
            tvQuantity.setText(String.valueOf(currentQuantity));

            // Enable/disable minus button based on quantity
            btnMinus.setEnabled(currentQuantity > 0);
            btnMinus.setAlpha(currentQuantity > 0 ? 1.0f : 0.5f);
        }

        private void setupClickListeners(Combo combo) {
            btnPlus.setOnClickListener(v -> {
                if (listener != null) {
                    int currentQuantity = listener.getCurrentComboQuantity(combo.getComboId());
                    int newQuantity = currentQuantity + 1;
                    listener.onComboQuantityChanged(combo, newQuantity);
                    updateQuantityDisplay(combo);
                }
            });

            btnMinus.setOnClickListener(v -> {
                if (listener != null) {
                    int currentQuantity = listener.getCurrentComboQuantity(combo.getComboId());
                    if (currentQuantity > 0) {
                        int newQuantity = currentQuantity - 1;
                        listener.onComboQuantityChanged(combo, newQuantity);
                        updateQuantityDisplay(combo);
                    }
                }
            });
        }
    }
}