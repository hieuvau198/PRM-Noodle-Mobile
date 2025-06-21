package com.example.prm_noodle_mobile.customer.orderconfirm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.Product;
import java.util.List;

public class OrderConfirmFragment extends Fragment implements OrderConfirmContract.View {

    private OrderConfirmContract.Presenter presenter;
    private TextView orderDetailsText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_confirm, container, false);

        initViews(view);
        presenter = new OrderConfirmPresenter(this);
        presenter.loadOrderDetails();

        return view;
    }

    private void initViews(View view) {
        orderDetailsText = view.findViewById(R.id.order_details);
    }

    @Override
    public void showOrderDetails(List<Product> orderItems) {
        StringBuilder details = new StringBuilder("Order Details:\n");
        for (Product product : orderItems) {
            details.append(product.getProductName()).append(" - ").append(product.getBasePrice()).append(" VND\n");
        }
        orderDetailsText.setText(details.toString());
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
    public void onOrderConfirmed() {
        // TODO: Show confirmation message or navigate away
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}