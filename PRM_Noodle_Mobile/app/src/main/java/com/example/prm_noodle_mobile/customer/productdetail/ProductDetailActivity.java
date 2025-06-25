package com.example.prm_noodle_mobile.customer.productdetail;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.Product;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Product product = getIntent().getParcelableExtra("product");
        if (product == null) {
            Toast.makeText(this, "Không tìm thấy sản phẩm!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageView image = findViewById(R.id.product_image);
        TextView name = findViewById(R.id.product_name);
        TextView price = findViewById(R.id.product_price);
        TextView desc = findViewById(R.id.product_description);
        TextView badge = findViewById(R.id.badge_spicy);

        name.setText(product.getProductName());
        desc.setText(product.getDescription());
        price.setText(String.format("%,.0fđ", product.getBasePrice()));
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Picasso.get().load(product.getImageUrl()).placeholder(R.drawable.placeholder_noodle_bowl).into(image);
        } else {
            image.setImageResource(R.drawable.placeholder_noodle_bowl);
        }
        // Badge cấp độ cay
        if (product.getSpiceLevel() != null && !product.getSpiceLevel().equalsIgnoreCase("None")) {
            badge.setText(product.getSpiceLevel());
            badge.setVisibility(TextView.VISIBLE);
        } else {
            badge.setVisibility(TextView.GONE);
        }
    }
} 