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

public class ToppingDialogAdapter extends RecyclerView.Adapter<ToppingDialogAdapter.ViewHolder> {
    private List<Topping> toppingList;
    private List<Topping> selectedToppings;
    private int[] toppingQuantities;

    public ToppingDialogAdapter(List<Topping> toppingList, List<Topping> selectedToppings) {
        this.toppingList = toppingList;
        this.selectedToppings = selectedToppings;
        this.toppingQuantities = new int[toppingList.size()];
        for (int i = 0; i < toppingQuantities.length; i++) toppingQuantities[i] = 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topping, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Topping topping = toppingList.get(position);
        holder.toppingName.setText(topping.getToppingName());
        holder.toppingPrice.setText(String.format("%,dđ", topping.getPrice()));
        holder.toppingQuantity.setText(String.valueOf(toppingQuantities[position]));

        // Set icon for minus and plus if drawable exists
        holder.btnMinus.setText("");
        holder.btnPlus.setText("");
        holder.btnMinus.setBackgroundResource(R.drawable.icon_minus);
        holder.btnPlus.setBackgroundResource(R.drawable.icon_plus);

        holder.btnMinus.setOnClickListener(v -> {
            if (toppingQuantities[position] > 0) {
                toppingQuantities[position]--;
                holder.toppingQuantity.setText(String.valueOf(toppingQuantities[position]));
                if (toppingQuantities[position] == 0) {
                    selectedToppings.remove(topping);
                }
            }
        });
        holder.btnPlus.setOnClickListener(v -> {
            toppingQuantities[position]++;
            holder.toppingQuantity.setText(String.valueOf(toppingQuantities[position]));
            if (!selectedToppings.contains(topping)) {
                selectedToppings.add(topping);
            }
        });
    }

    @Override
    public int getItemCount() {
        return toppingList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView toppingName, toppingPrice, toppingQuantity;
        Button btnMinus, btnPlus;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            toppingName = itemView.findViewById(R.id.topping_name);
            toppingPrice = itemView.findViewById(R.id.topping_price);
            toppingQuantity = itemView.findViewById(R.id.topping_quantity);
            btnMinus = itemView.findViewById(R.id.btn_minus_topping);
            btnPlus = itemView.findViewById(R.id.btn_plus_topping);
        }
    }
} 