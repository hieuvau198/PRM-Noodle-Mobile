package com.example.prm_noodle_mobile.customer.orderconfirm;

import com.example.prm_noodle_mobile.data.model.Product;

import java.util.List;

public interface OrderConfirmContract {
    interface View {
        void showOrderDetails(List<Product> orderItems);
        void showLoading();
        void hideLoading();
        void showError(String message);
        void onOrderConfirmed();
    }

    interface Presenter {
        void loadOrderDetails();
        void confirmOrder();
        void onDestroy();
    }
}