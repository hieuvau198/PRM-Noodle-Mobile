package com.example.prm_noodle_mobile.customer.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.OrderItem;

import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private CartItemAdapter adapter;
    private TextView emptyCartMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRecyclerView = view.findViewById(R.id.rv_cart_items);
//        emptyCartMessage = view.findViewById(R.id.tv_empty_cart);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<OrderItem> orderItems = CartManager.getInstance().getOrderItems();

        if (orderItems.isEmpty()) {
            emptyCartMessage.setVisibility(View.VISIBLE);
            cartRecyclerView.setVisibility(View.GONE);
        } else {
            emptyCartMessage.setVisibility(View.GONE);
            cartRecyclerView.setVisibility(View.VISIBLE);
            adapter = new CartItemAdapter(orderItems);
            cartRecyclerView.setAdapter(adapter);
        }

        return view;
    }
}
