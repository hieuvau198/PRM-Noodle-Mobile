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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.LinearLayout;
import java.util.List;
import android.content.Intent;
import android.content.SharedPreferences;
import com.example.prm_noodle_mobile.auth.LoginActivity;
import com.example.prm_noodle_mobile.customer.home.HomeFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CartFragment extends Fragment implements CartContract.View {

    private CartContract.Presenter presenter;
    private RecyclerView cartRecyclerView;
    private FeaturedProductAdapter adapter; // Reuse for simplicity
    private TextView tvUsername;
    private ImageButton btnLogout;
    private LinearLayout layoutCartContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Khi vào CartFragment, tự động chuyển về HomeFragment
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, new HomeFragment());
            transaction.commit();
        }
        // Trả về view rỗng
        return new View(getContext());
    }

    private void initViews(View view) {
        tvUsername = view.findViewById(R.id.tv_username);
        btnLogout = view.findViewById(R.id.btn_logout);
        layoutCartContent = view.findViewById(R.id.layout_cart_content);
        cartRecyclerView = view.findViewById(R.id.rv_cart_items);
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