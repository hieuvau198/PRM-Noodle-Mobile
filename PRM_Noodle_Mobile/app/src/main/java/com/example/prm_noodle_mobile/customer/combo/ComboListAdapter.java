package com.example.prm_noodle_mobile.customer.combo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.Combo;
import java.util.List;

public class ComboListAdapter extends RecyclerView.Adapter<ComboListAdapter.ViewHolder> {
    private List<Combo> combos;
    private OnComboClickListener listener;

    public ComboListAdapter(List<Combo> combos) {
        this.combos = combos;
    }

    public void setCombos(List<Combo> combos) {
        this.combos = combos;
    }

    public void setOnComboClickListener(OnComboClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_combo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Combo combo = combos.get(position);
        holder.bind(combo);
    }

    @Override
    public int getItemCount() {
        return combos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, description;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.combo_name);
            price = itemView.findViewById(R.id.combo_price);
            description = itemView.findViewById(R.id.combo_description);
            image = itemView.findViewById(R.id.combo_image);
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onComboClick(combos.get(getAdapterPosition()));
            });
        }
        void bind(Combo combo) {
            name.setText(combo.getComboName());
            price.setText(String.format("%,d VND", combo.getPrice()));
            description.setText(combo.getDescription());
            if (combo.getImageUrl() != null && !combo.getImageUrl().isEmpty()) {
                Glide.with(image.getContext())
                        .load(combo.getImageUrl())
                        .placeholder(R.drawable.ic_noodle_placeholder)
                        .into(image);
            } else {
                image.setImageResource(R.drawable.ic_noodle_placeholder);
            }
        }
    }

    public interface OnComboClickListener {
        void onComboClick(Combo combo);
    }
} 