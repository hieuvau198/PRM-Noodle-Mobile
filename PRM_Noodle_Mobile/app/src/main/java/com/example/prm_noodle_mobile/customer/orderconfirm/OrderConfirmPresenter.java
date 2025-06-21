package com.example.prm_noodle_mobile.customer.orderconfirm;

import com.example.prm_noodle_mobile.customer.cart.CartPresenter;
import com.example.prm_noodle_mobile.data.model.Product;
import java.util.ArrayList;
import java.util.List;

public class OrderConfirmPresenter implements OrderConfirmContract.Presenter {

    private OrderConfirmContract.View view;
    private List<Product> orderItems = new ArrayList<>();

    public OrderConfirmPresenter(OrderConfirmContract.View view) {
        this.view = view;
    }

    @Override
    public void loadOrderDetails() {
        if (view != null) {
            view.showLoading();
            // Simulate fetching cart items for order
            view.hideLoading();
            view.showOrderDetails(orderItems);
        }
    }

    @Override
    public void confirmOrder() {
        if (view != null) {
            view.showLoading();
            // Simulate order confirmation
            orderItems.clear();
            view.hideLoading();
            view.onOrderConfirmed();
        }
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}