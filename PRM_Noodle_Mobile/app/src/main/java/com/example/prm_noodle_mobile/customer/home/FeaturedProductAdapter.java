// FILE: app/src/main/java/com/example/prm_noodle_mobile/customer/home/FeaturedProductAdapter.java
package com.example.prm_noodle_mobile.customer.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.Product;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FeaturedProductAdapter extends RecyclerView.Adapter<FeaturedProductAdapter.ViewHolder> {

    private List<Product> products = new ArrayList<>();
    private NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    public void updateProducts(List<Product> newProducts) {
        this.products.clear();
        this.products.addAll(newProducts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_featured_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;
        private TextView spiceLevel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.iv_product_image);
            productName = itemView.findViewById(R.id.tv_product_name);
            productPrice = itemView.findViewById(R.id.tv_product_price);
            spiceLevel = itemView.findViewById(R.id.tv_spice_level);
        }

        public void bind(Product product) {
            productName.setText(product.getProductName());
            productPrice.setText(String.format("%,d VND", (int)product.getBasePrice()));
            spiceLevel.setText(product.getSpiceLevel());

            // Placeholder image - you can add Glide/Picasso later
            productImage.setImageResource(R.drawable.ic_noodle_placeholder);
        }
    }
}