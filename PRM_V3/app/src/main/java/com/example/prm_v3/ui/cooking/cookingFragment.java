package com.example.prm_v3.ui.cooking;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.prm_v3.R;
import com.example.prm_v3.adapter.CookingOrderAdapter;
import com.example.prm_v3.databinding.FragmentCookingBinding;
import com.example.prm_v3.model.Order;

import java.util.ArrayList;
import java.util.List;

public class cookingFragment extends Fragment implements CookingOrderAdapter.OnCookingOrderActionListener {
    private static final String TAG = "CookingFragment";

    private FragmentCookingBinding binding;
    private cookingViewModel cookingViewModel;
    private CookingOrderAdapter cookingAdapter;
    private List<Order> preparingOrders = new ArrayList<>();

    // Auto-refresh control
    private Handler refreshHandler = new Handler(Looper.getMainLooper());
    private boolean shouldAutoRefresh = true;
    private static final long AUTO_REFRESH_INTERVAL = 30000; // 30 seconds

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView started");

        binding = FragmentCookingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupViewModel();
        setupRecyclerView();
        setupSwipeRefresh();
        observeViewModel();

        // Load initial data
        cookingViewModel.loadPreparingOrders();

        // Start auto-refresh
        startAutoRefresh();

        return root;
    }

    private void setupViewModel() {
        cookingViewModel = new ViewModelProvider(this).get(cookingViewModel.class);
    }

    private void setupRecyclerView() {
        cookingAdapter = new CookingOrderAdapter(getContext(), preparingOrders);
        cookingAdapter.setOnCookingOrderActionListener(this);

        binding.recyclerViewCooking.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCooking.setAdapter(cookingAdapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.orange_600);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "Manual refresh triggered");
            cookingViewModel.refreshOrders();
        });
    }

    private void observeViewModel() {
        // Observe preparing orders
        cookingViewModel.getPreparingOrders().observe(getViewLifecycleOwner(), orders -> {
            Log.d(TAG, "Preparing orders updated: " + (orders != null ? orders.size() : "null"));

            binding.swipeRefreshLayout.setRefreshing(false);

            if (orders != null) {
                preparingOrders.clear();
                preparingOrders.addAll(orders);

                // Update UI on main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        cookingAdapter.updateOrders(new ArrayList<>(preparingOrders));
                        updateEmptyState();
                    });
                }
            } else {
                preparingOrders.clear();
                cookingAdapter.updateOrders(new ArrayList<>());
                updateEmptyState();
            }
        });

        // Observe loading state
        cookingViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "Loading state: " + isLoading);

            if (isLoading && !binding.swipeRefreshLayout.isRefreshing()) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.recyclerViewCooking.setVisibility(View.GONE);
                binding.layoutEmptyState.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                updateEmptyState();
            }
        });

        // Observe error state
        cookingViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            binding.progressBar.setVisibility(View.GONE);

            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Error: " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                updateEmptyState();
            }
        });

        // Observe status messages
        cookingViewModel.getStatusMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Status message: " + message);
            }
        });
    }

    private void updateEmptyState() {
        if (preparingOrders.isEmpty()) {
            binding.recyclerViewCooking.setVisibility(View.GONE);
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerViewCooking.setVisibility(View.VISIBLE);
            binding.layoutEmptyState.setVisibility(View.GONE);
        }
    }

    // ========== ORDER ACTION HANDLERS ==========

    @Override
    public void onMarkOrderReady(Order order) {
        Log.d(TAG, "Mark order ready: " + order.getOrderId());

        // Optimistic update - remove from list immediately
        removeOrderFromList(order.getOrderId());

        // Update status via API
        cookingViewModel.markOrderAsReady(order.getOrderId());

        Toast.makeText(getContext(), "Đang cập nhật trạng thái...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelOrder(Order order) {
        Log.d(TAG, "Cancel order: " + order.getOrderId());

        // Optimistic update - remove from list immediately
        removeOrderFromList(order.getOrderId());

        // Cancel via API
        cookingViewModel.cancelOrder(order.getOrderId());

        Toast.makeText(getContext(), "Đang hủy đơn hàng...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOrderClick(Order order) {
        // Navigate to order detail if needed
        // For now, just log
        Log.d(TAG, "Order clicked: " + order.getOrderId());
    }

    // ========== HELPER METHODS ==========

    private void removeOrderFromList(int orderId) {
        preparingOrders.removeIf(order -> order.getOrderId() == orderId);

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                cookingAdapter.updateOrders(new ArrayList<>(preparingOrders));
                updateEmptyState();
            });
        }
    }

    // ========== AUTO REFRESH ==========

    private void startAutoRefresh() {
        if (shouldAutoRefresh) {
            refreshHandler.postDelayed(autoRefreshRunnable, AUTO_REFRESH_INTERVAL);
        }
    }

    private void stopAutoRefresh() {
        refreshHandler.removeCallbacks(autoRefreshRunnable);
    }

    private final Runnable autoRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (shouldAutoRefresh && cookingViewModel != null) {
                Log.d(TAG, "Auto refresh triggered");
                cookingViewModel.refreshOrders();

                // Schedule next refresh
                refreshHandler.postDelayed(this, AUTO_REFRESH_INTERVAL);
            }
        }
    };

    // ========== LIFECYCLE METHODS ==========

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        shouldAutoRefresh = true;
        startAutoRefresh();

        // Refresh data when coming back to fragment
        if (cookingViewModel != null) {
            cookingViewModel.refreshOrders();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        shouldAutoRefresh = false;
        stopAutoRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");

        stopAutoRefresh();
        binding = null;
    }

    // ========== PUBLIC METHODS ==========

    /**
     * Manual refresh for external calls
     */
    public void refreshData() {
        if (cookingViewModel != null) {
            cookingViewModel.refreshOrders();
        }
    }

    /**
     * Get current orders count
     */
    public int getCurrentOrdersCount() {
        return preparingOrders.size();
    }

    /**
     * Force refresh for testing
     */
    public void forceRefresh() {
        if (cookingViewModel != null) {
            cookingViewModel.refreshOrders();
        }
    }
}