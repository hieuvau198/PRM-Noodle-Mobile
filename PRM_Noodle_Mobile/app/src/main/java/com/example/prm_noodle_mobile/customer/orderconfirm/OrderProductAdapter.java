package com.example.prm_noodle_mobile.customer.orderconfirm;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.OrderItem;
import com.example.prm_noodle_mobile.data.model.Product;
import com.example.prm_noodle_mobile.data.model.Topping;
import com.example.prm_noodle_mobile.data.model.ToppingOrder;
import java.util.ArrayList;
import java.util.List;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.ViewHolder> {
    private List<Product> productList;
    private List<Topping> toppingList;
    private OnAddToCartListener addToCartListener;
    private List<OrderItem> orderItemList;
    private List<Product> productListForOrderItems;

    public OrderProductAdapter(List<Product> productList, List<Topping> toppingList, OnAddToCartListener listener) {
        this.productList = productList;
        this.toppingList = toppingList;
        this.addToCartListener = listener;
    }

    // Constructor cho OrderConfirmFragment (chỉ hiển thị các sản phẩm đã thêm vào giỏ hàng)
    public OrderProductAdapter(List<OrderItem> orderItemList, List<Product> productList) {
        this.orderItemList = orderItemList;
        this.productListForOrderItems = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (orderItemList != null) {
            OrderItem item = orderItemList.get(position);
            Product product = null;
            if (productListForOrderItems != null) {
                for (Product p : productListForOrderItems) {
                    if (p.getProductId() == item.getProductId()) {
                        product = p;
                        break;
                    }
                }
            }
            if (product != null) {
                holder.productName.setText(product.getProductName());
                holder.productPrice.setText(String.format("%,dđ", product.getBasePrice()));
                holder.productDesc.setText(product.getDescription());
                Glide.with(holder.productImage.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_noodle_placeholder)
                        .into(holder.productImage);
            } else {
                holder.productName.setText("Sản phẩm ID: " + item.getProductId());
                holder.productPrice.setText("");
                holder.productDesc.setText("");
                holder.productImage.setImageResource(R.drawable.ic_noodle_placeholder);
            }
            // Hiển thị topping chi tiết
            holder.toppingContainer.removeAllViews();
            if (item.getToppings() != null && !item.getToppings().isEmpty()) {
                for (ToppingOrder toppingOrder : item.getToppings()) {
                    TextView tv = new TextView(holder.toppingContainer.getContext());
                    tv.setText("+ Topping ID: " + toppingOrder.getToppingId() + " x" + toppingOrder.getQuantity());
                    holder.toppingContainer.addView(tv);
                }
            }
        } else if (productList != null) {
            Product product = productList.get(position);
            holder.productName.setText(product.getProductName());
            holder.productPrice.setText(String.format("%,dđ", product.getBasePrice()));
            holder.productDesc.setText(product.getDescription());
            Glide.with(holder.productImage.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_noodle_placeholder)
                    .into(holder.productImage);
            holder.itemView.setOnClickListener(v -> showSelectDialog(holder.itemView.getContext(), product));
        }
    }

    @Override
    public int getItemCount() {
        if (orderItemList != null) return orderItemList.size();
        if (productList != null) return productList.size();
        return 0;
    }

    private void showSelectDialog(Context context, Product product) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_select_product_topping);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView img = dialog.findViewById(R.id.dialog_product_image);
        TextView name = dialog.findViewById(R.id.dialog_product_name);
        TextView price = dialog.findViewById(R.id.dialog_product_price);
        TextView desc = dialog.findViewById(R.id.dialog_product_desc);
        Button btnMinus = dialog.findViewById(R.id.dialog_btn_minus);
        Button btnPlus = dialog.findViewById(R.id.dialog_btn_plus);
        TextView quantityView = dialog.findViewById(R.id.dialog_quantity);
        RecyclerView recyclerTopping = dialog.findViewById(R.id.dialog_recycler_topping);
        Button btnAdd = dialog.findViewById(R.id.dialog_btn_add_to_cart);
        Glide.with(context).load(product.getImageUrl()).placeholder(R.drawable.ic_noodle_placeholder).into(img);
        name.setText(product.getProductName());
        price.setText(String.format("%,dđ", product.getBasePrice()));
        desc.setText(product.getDescription());
        final int[] quantity = {1};
        quantityView.setText("1");
        btnMinus.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                quantityView.setText(String.valueOf(quantity[0]));
            }
        });
        btnPlus.setOnClickListener(v -> {
            quantity[0]++;
            quantityView.setText(String.valueOf(quantity[0]));
        });
        // Topping chọn
        ArrayList<Topping> selectedToppings = new ArrayList<>();
        ToppingDialogAdapter toppingAdapter = new ToppingDialogAdapter(toppingList, selectedToppings);
        recyclerTopping.setLayoutManager(new LinearLayoutManager(context));
        recyclerTopping.setAdapter(toppingAdapter);
        btnAdd.setOnClickListener(v -> {
            if (addToCartListener != null) {
                addToCartListener.onAddToCart(product, quantity[0], selectedToppings);
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productDesc;
        ImageView productImage;
        LinearLayout toppingContainer;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productDesc = itemView.findViewById(R.id.product_description);
            productImage = itemView.findViewById(R.id.product_image);
            toppingContainer = itemView.findViewById(R.id.topping_container);
        }
    }

    public interface OnAddToCartListener {
        void onAddToCart(Product product, int quantity, List<Topping> toppings);
    }
} 