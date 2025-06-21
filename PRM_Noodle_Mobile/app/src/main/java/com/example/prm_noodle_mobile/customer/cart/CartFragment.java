package com.example.prm_noodle_mobile.customer.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.customer.home.FeaturedProductAdapter;
import com.example.prm_noodle_mobile.data.model.Product;
import java.util.List;

public class CartFragment extends Fragment implements CartContract.View {

    private CartContract.Presenter presenter;
    private RecyclerView cartRecyclerView;
    private FeaturedProductAdapter adapter; // Reuse for simplicity

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        initViews(view);
        presenter = new CartPresenter(this);
        presenter.loadCartItems();

        return view;
    }

    private void initViews(View view) {
        cartRecyclerView = view.findViewById(R.id.rv_cart_items); // Add this ID to your XML
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FeaturedProductAdapter();
        cartRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showCartItems(List<Product> cartItems) {
        adapter.updateProducts(cartItems);
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