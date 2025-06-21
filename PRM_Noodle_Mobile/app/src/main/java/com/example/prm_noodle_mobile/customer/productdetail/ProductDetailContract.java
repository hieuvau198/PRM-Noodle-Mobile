package com.example.prm_noodle_mobile.customer.productdetail;

import com.example.prm_noodle_mobile.data.model.Product;

public interface ProductDetailContract {
    interface View {
        void showProductDetails(Product product);
        void showLoading();
        void hideLoading();
        void showError(String message);
    }

    interface Presenter {
        void loadProductDetails(int productId);
        void onDestroy();
    }
}