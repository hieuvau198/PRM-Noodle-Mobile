package com.example.prm_v3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_v3.R;
import com.example.prm_v3.model.Product;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ProductCreateAdapter extends RecyclerView.Adapter<ProductCreateAdapter.ProductViewHolder> {

    private List<Product> products = new ArrayList<>();
    private OnProductQuantityChangeListener listener;

    public interface OnProductQuantityChangeListener {
        void onProductQuantityChanged(Product product, int quantity);
        int getCurrentProductQuantity(int productId);
    }

    public ProductCreateAdapter(OnProductQuantityChangeListener listener) {
        this.listener = listener;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products.clear();
        if (newProducts != null) {
            this.products.addAll(newProducts);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_create, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProductName;
        private TextView tvProductDescription;
        private TextView tvProductPrice;
        private TextView tvSpicyLevel;
        private TextView tvQuantity;
        private MaterialButton btnMinus;
        private MaterialButton btnPlus;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductDescription = itemView.findViewById(R.id.tv_product_description);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvSpicyLevel = itemView.findViewById(R.id.tv_spicy_level);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
        }

        public void bind(Product product) {
            tvProductName.setText(product.getProductName());

            if (product.hasDescription()) {
                tvProductDescription.setText(product.getDescription());
                tvProductDescription.setVisibility(View.VISIBLE);
            } else {
                tvProductDescription.setVisibility(View.GONE);
            }

            tvProductPrice.setText(product.getFormattedPrice());

            // Display spicy level
            String spiceLevel = product.getSpiceLevel();
            if (spiceLevel != null && !spiceLevel.equals("none") && !spiceLevel.isEmpty()) {
                int spiceCount = getSpiceCount(spiceLevel);
                StringBuilder spiceText = new StringBuilder();
                for (int i = 0; i < spiceCount; i++) {
                    spiceText.append("🌶");
                }
                tvSpicyLevel.setText(spiceText.toString());
                tvSpicyLevel.setVisibility(View.VISIBLE);
            } else {
                tvSpicyLevel.setVisibility(View.GONE);
            }

            // Update quantity display
            updateQuantityDisplay(product);

            // Set click listeners
            setupClickListeners(product);
        }

        private void updateQuantityDisplay(Product product) {
            int currentQuantity = listener != null ? listener.getCurrentProductQuantity(product.getProductId()) : 0;
            tvQuantity.setText(String.valueOf(currentQuantity));

            // Enable/disable minus button based on quantity
            btnMinus.setEnabled(currentQuantity > 0);
            btnMinus.setAlpha(currentQuantity > 0 ? 1.0f : 0.5f);
        }

        private void setupClickListeners(Product product) {
            btnPlus.setOnClickListener(v -> {
                if (listener != null) {
                    int currentQuantity = listener.getCurrentProductQuantity(product.getProductId());
                    int newQuantity = currentQuantity + 1;
                    listener.onProductQuantityChanged(product, newQuantity);
                    updateQuantityDisplay(product);
                }
            });

            btnMinus.setOnClickListener(v -> {
                if (listener != null) {
                    int currentQuantity = listener.getCurrentProductQuantity(product.getProductId());
                    if (currentQuantity > 0) {
                        int newQuantity = currentQuantity - 1;
                        listener.onProductQuantityChanged(product, newQuantity);
                        updateQuantityDisplay(product);
                    }
                }
            });
        }

        private int getSpiceCount(String spiceLevel) {
            switch (spiceLevel.toLowerCase()) {
                case "mild": return 1;
                case "medium": return 2;
                case "hot": return 3;
                case "very_hot": return 4;
                default: return 0;
            }
        }
    }
}