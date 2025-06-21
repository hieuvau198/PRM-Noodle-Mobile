package com.example.prm_noodle_mobile.customer.cart;

import com.example.prm_noodle_mobile.data.model.Product;
import java.util.ArrayList;
import java.util.List;

public class CartPresenter implements CartContract.Presenter {

    private CartContract.View view;
    private List<Product> cartItems = new ArrayList<>();

    public CartPresenter(CartContract.View view) {
        this.view = view;
    }

    @Override
    public void loadCartItems() {
        if (view != null) {
            view.showLoading();
            // Simulate cart items
            view.hideLoading();
            view.showCartItems(cartItems);
        }
    }

    @Override
    public void addToCart(Product product) {
        if (view != null) {
            cartItems.add(product);
            view.showCartItems(cartItems);
        }
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}