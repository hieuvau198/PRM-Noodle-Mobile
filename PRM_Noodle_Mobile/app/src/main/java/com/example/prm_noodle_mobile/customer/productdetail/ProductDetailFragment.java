package com.example.prm_noodle_mobile.customer.productdetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.Product;
import com.example.prm_noodle_mobile.data.mock.MockProductData;

public class ProductDetailFragment extends Fragment implements ProductDetailContract.View {

    private ProductDetailContract.Presenter presenter;
    private ImageView productImage;
    private TextView productName, productDescription, productPrice, spiceLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        initViews(view);
        presenter = new ProductDetailPresenter(this);
        // Simulate loading product with ID 1
        presenter.loadProductDetails(1);

        return view;
    }

    private void initViews(View view) {
        productImage = view.findViewById(R.id.product_image);
        productName = view.findViewById(R.id.product_name);
        productDescription = view.findViewById(R.id.product_description);
        productPrice = view.findViewById(R.id.product_price);
        spiceLevel = view.findViewById(R.id.spice_level); // Add this to your XML
    }

    @Override
    public void showProductDetails(Product product) {
        productName.setText(product.getProductName());
        productDescription.setText(product.getDescription());
        productPrice.setText(String.format("%,d VND", (int)product.getBasePrice()));
        spiceLevel.setText(product.getSpiceLevel());
        // Placeholder for image loading
        productImage.setImageResource(R.drawable.ic_noodle_placeholder);
    }

    @Override
    public void showLoading() {
        // TODO: Show loading indicator
    }

    @Override
    public void hideLoading() {
        // TODO: Hide loading indicator
    }

    @Override
    public void showError(String message) {
        // TODO: Show error message
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}