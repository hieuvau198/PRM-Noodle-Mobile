package com.example.prm_noodle_mobile.customer.cart;

import com.example.prm_noodle_mobile.data.model.Product;

import java.util.List;

public interface CartContract {
    interface View {
        void showCartItems(List<Product> cartItems);
        void showLoading();
        void hideLoading();
        void showError(String message);
    }

    interface Presenter {
        void loadCartItems();
        void addToCart(Product product);
        void onDestroy();
    }
}