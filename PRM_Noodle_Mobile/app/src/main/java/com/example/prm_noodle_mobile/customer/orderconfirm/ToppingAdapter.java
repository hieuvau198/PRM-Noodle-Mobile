package com.example.prm_noodle_mobile.customer.orderconfirm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.Topping;
import java.util.List;

public class ToppingAdapter extends RecyclerView.Adapter<ToppingAdapter.ViewHolder> {
    private List<Topping> toppings;
    private OnToppingCheckedListener listener;

    public ToppingAdapter(List<Topping> toppings, OnToppingCheckedListener listener) {
        this.toppings = toppings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topping, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Topping topping = toppings.get(position);
        holder.toppingName.setText(topping.getToppingName());
        holder.toppingPrice.setText("+" + topping.getPrice() + "đ");
    }

    @Override
    public int getItemCount() {
        return toppings.size();
    }

    public interface OnToppingCheckedListener {
        void onToppingChecked(Topping topping, boolean checked);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView toppingName, toppingPrice;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            toppingName = itemView.findViewById(R.id.topping_name);
            toppingPrice = itemView.findViewById(R.id.topping_price);
        }
    }
} 