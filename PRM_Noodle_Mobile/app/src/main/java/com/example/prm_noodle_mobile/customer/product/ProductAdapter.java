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

            // Bấm vào sản phẩm để sang màn chi tiết
            itemView.setOnClickListener(v -> {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product", product); // Product phải implements Parcelable
                context.startActivity(intent);
            });
        }
    }
}
