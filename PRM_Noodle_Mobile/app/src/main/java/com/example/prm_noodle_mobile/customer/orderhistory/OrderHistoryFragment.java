package com.example.prm_noodle_mobile.customer.orderhistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.api.ApiClient;
import com.example.prm_noodle_mobile.data.api.OrderApi;
import com.example.prm_noodle_mobile.data.model.Order;
import com.example.prm_noodle_mobile.data.model.OrderResponse;
import com.example.prm_noodle_mobile.utils.UserSessionManager;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private UserSessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        recyclerView = view.findViewById(R.id.recycler_orders);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        
        sessionManager = new UserSessionManager(requireContext());
        
        setupRecyclerView();
        setupSwipeRefresh();
        loadOrders();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderHistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(this::loadOrders);
    }

    private void loadOrders() {
        int userId = sessionManager.getUserId();
        
        OrderApi orderApi = ApiClient.getClient(requireContext()).create(OrderApi.class);
        orderApi.getUserOrders(userId, 1, 10).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setOrders(response.body().getOrders());
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
