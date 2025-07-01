package com.example.prm_noodle_mobile.customer.product;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.Product;
import com.example.prm_noodle_mobile.customer.productdetail.ProductDetailActivity;
import com.squareup.picasso.Picasso;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.prm_noodle_mobile.data.api.ApiClient;
import com.example.prm_noodle_mobile.data.api.ToppingApi;
import com.example.prm_noodle_mobile.data.model.Topping;
import com.example.prm_noodle_mobile.data.model.ToppingOrder;
import com.example.prm_noodle_mobile.data.model.OrderItem;
import com.example.prm_noodle_mobile.customer.cart.CartManager;
import com.example.prm_noodle_mobile.customer.orderconfirm.ToppingDialogAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList = new ArrayList<>();

    public void setProductList(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false); // item_product.xml là layout cho từng sản phẩm
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvDesc, tvPrice, tvSpice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.product_image);
            tvName = itemView.findViewById(R.id.product_name);
            tvDesc = itemView.findViewById(R.id.product_description);
            tvPrice = itemView.findViewById(R.id.product_price);
            tvSpice = itemView.findViewById(R.id.badge_spicy);
        }

        public void bind(Product product) {
            tvName.setText(product.getProductName());
            tvDesc.setText(product.getDescription());
            tvPrice.setText(String.format("%,d đ", product.getBasePrice()));
            if (product.getSpiceLevel() != null && !product.getSpiceLevel().equalsIgnoreCase("None")) {
                tvSpice.setText(product.getSpiceLevel());
                tvSpice.setVisibility(View.VISIBLE);
            } else {
                tvSpice.setVisibility(View.GONE);
            }
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Picasso.get().load(product.getImageUrl())
                        .placeholder(R.drawable.placeholder_noodle_bowl)
                        .into(imgProduct);
            } else {
                imgProduct.setImageResource(R.drawable.placeholder_noodle_bowl);
            }

            // Bấm vào sản phẩm để mở dialog order
            itemView.setOnClickListener(v -> {
                ToppingApi api = ApiClient.getClient().create(ToppingApi.class);
                api.getAvailableToppings().enqueue(new Callback<List<Topping>>() {
                    @Override
                    public void onResponse(Call<List<Topping>> call, Response<List<Topping>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            showOrderDialog(itemView.getContext(), product, response.body());
                        } else {
                            Toast.makeText(itemView.getContext(), "Không lấy được topping!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Topping>> call, Throwable t) {
                        Toast.makeText(itemView.getContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        private void showOrderDialog(Context context, Product product, List<Topping> toppingList) {
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
            ArrayList<Topping> selectedToppings = new ArrayList<>();
            ToppingDialogAdapter toppingAdapter = new ToppingDialogAdapter(toppingList, selectedToppings);
            recyclerTopping.setLayoutManager(new LinearLayoutManager(context));
            recyclerTopping.setAdapter(toppingAdapter);
            btnAdd.setOnClickListener(v -> {
                ArrayList<ToppingOrder> toppingOrders = new ArrayList<>();
                for (Topping topping : selectedToppings) {
                    toppingOrders.add(new ToppingOrder(topping.getToppingId(), 1)); // mặc định 1 topping mỗi loại
                }
                OrderItem orderItem = new OrderItem(product.getProductId(), quantity[0], toppingOrders);
                CartManager.getInstance().addOrderItem(orderItem);
                Toast.makeText(context, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                // Sau khi thêm, reload lại ProductFragment để kiểm tra
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).recreate();
                }
            });
            dialog.show();
        }
    }
}
