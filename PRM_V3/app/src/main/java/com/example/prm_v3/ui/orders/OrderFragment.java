package com.example.prm_v3.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.prm_v3.R;
import com.example.prm_v3.adapter.OrderAdapter;
import com.example.prm_v3.databinding.FragmentOrderBinding;
import com.example.prm_v3.model.Order;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrderFragment extends Fragment implements OrderAdapter.OnOrderActionListener {
    private static final String TAG = "OrderFragment";

    private FragmentOrderBinding binding;
    private OrderViewModel orderViewModel;
    private OrderAdapter orderAdapter;
    private List<Order> allOrders = new ArrayList<>();
    private String currentFilter = "all";

    // Pagination variables
    private int currentPage = 1;
    private static final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    // Auto-refresh control
    private Handler refreshHandler = new Handler(Looper.getMainLooper());
    private boolean shouldAutoRefresh = true;

    // Tab views
    private TextView tabAll, tabPending, tabConfirmed, tabPreparing, tabDelivered, tabCompleted, tabCancelled;
    private RecyclerView recyclerViewOrders;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView started");
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initViews();
        setupRecyclerView();
        setupTabListeners();
        observeViewModel();

        // Load initial data
        loadFirstPageOptimized();

        return root;
    }

    private void initViews() {
        Log.d(TAG, "initViews");
        tabAll = binding.tabAll;
        tabPending = binding.tabPending;
        tabConfirmed = binding.tabConfirmed;
        tabPreparing = binding.tabPreparing;
        tabDelivered = binding.tabDelivered;
        tabCompleted = binding.tabCompleted;
        tabCancelled = binding.tabCancelled;

        recyclerViewOrders = binding.recyclerViewOrders;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        progressBar = binding.progressBar;
        layoutEmptyState = binding.layoutEmptyState;
    }

    private void setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView");
        orderAdapter = new OrderAdapter(getContext(), allOrders);
        orderAdapter.setOnOrderActionListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewOrders.setLayoutManager(layoutManager);
        recyclerViewOrders.setAdapter(orderAdapter);

        // Setup pagination scroll listener
        recyclerViewOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0
                                && totalItemCount >= PAGE_SIZE) {
                            loadNextPageOptimized();
                        }
                    }
                }
            }
        });

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.orange_600);
        swipeRefreshLayout.setOnRefreshListener(this::refreshDataComplete);
    }

    private void setupTabListeners() {
        tabAll.setOnClickListener(v -> selectTab("all"));
        tabPending.setOnClickListener(v -> selectTab("pending"));
        tabConfirmed.setOnClickListener(v -> selectTab("confirmed"));
        tabPreparing.setOnClickListener(v -> selectTab("preparing"));
        tabDelivered.setOnClickListener(v -> selectTab("delivered"));
        tabCompleted.setOnClickListener(v -> selectTab("completed"));
        tabCancelled.setOnClickListener(v -> selectTab("cancelled"));
    }

    private void selectTab(String filter) {
        Log.d(TAG, "selectTab: " + filter);
        currentFilter = filter;
        updateTabAppearance();
        refreshDataComplete();
    }

    private void refreshDataComplete() {
        Log.d(TAG, "refreshDataComplete");
        currentPage = 1;
        isLastPage = false;
        allOrders.clear();
        orderAdapter.notifyDataSetChanged();
        loadFirstPageOptimized();
    }

    // ========== OPTIMIZED LOADING METHODS ==========

    private void loadFirstPageOptimized() {
        Log.d(TAG, "loadFirstPageOptimized - currentFilter: " + currentFilter);
        isLoading = true;
        currentPage = 1;

        // Enable auto-refresh in repository
        orderViewModel.enableAutoRefresh(true);

        // Use direct endpoint method for optimal performance
        orderViewModel.loadOrdersByDirectEndpoint(currentFilter, currentPage, PAGE_SIZE);
    }

    private void loadNextPageOptimized() {
        Log.d(TAG, "loadNextPageOptimized - page: " + (currentPage + 1));
        isLoading = true;
        currentPage++;

        orderViewModel.loadOrdersByDirectEndpoint(currentFilter, currentPage, PAGE_SIZE);
    }

    private void updateTabAppearance() {
        // Reset all tabs
        resetTab(tabAll);
        resetTab(tabPending);
        resetTab(tabConfirmed);
        resetTab(tabPreparing);
        resetTab(tabDelivered);
        resetTab(tabCompleted);
        resetTab(tabCancelled);

        // Highlight selected tab
        TextView selectedTab = getSelectedTab();
        if (selectedTab != null) {
            selectedTab.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_tab_selected));
            selectedTab.setTextColor(ContextCompat.getColor(getContext(), R.color.orange_600));
        }
    }

    private void resetTab(TextView tab) {
        tab.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_tab_normal));
        tab.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_600));
    }

    private TextView getSelectedTab() {
        switch (currentFilter) {
            case "all": return tabAll;
            case "pending": return tabPending;
            case "confirmed": return tabConfirmed;
            case "preparing": return tabPreparing;
            case "delivered": return tabDelivered;
            case "completed": return tabCompleted;
            case "cancelled": return tabCancelled;
            default: return tabAll;
        }
    }

    private void observeViewModel() {
        Log.d(TAG, "observeViewModel setup");

        orderViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            Log.d(TAG, "Orders observer triggered - orders: " + (orders != null ? orders.size() : "null"));

            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);

            if (orders != null) {
                Log.d(TAG, "Processing " + orders.size() + " orders");

                // Update data
                if (currentPage == 1) {
                    allOrders.clear();
                }

                if (!orders.isEmpty()) {
                    allOrders.addAll(orders);
                }

                // Force UI update on main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        orderAdapter.updateOrders(new ArrayList<>(allOrders));

                        if (allOrders.isEmpty()) {
                            recyclerViewOrders.setVisibility(View.GONE);
                            layoutEmptyState.setVisibility(View.VISIBLE);
                        } else {
                            recyclerViewOrders.setVisibility(View.VISIBLE);
                            layoutEmptyState.setVisibility(View.GONE);
                        }

                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "UI updated - showing " + allOrders.size() + " items");
                    });
                }

                isLastPage = orders.size() < PAGE_SIZE;
            } else {
                Log.d(TAG, "Received null orders");
                if (currentPage == 1) {
                    allOrders.clear();
                    orderAdapter.updateOrders(new ArrayList<>());
                    recyclerViewOrders.setVisibility(View.GONE);
                    layoutEmptyState.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        orderViewModel.getLoading().observe(getViewLifecycleOwner(), isLoadingData -> {
            Log.d(TAG, "Loading: " + isLoadingData);
            if (isLoadingData && currentPage == 1 && !swipeRefreshLayout.isRefreshing()) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerViewOrders.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.GONE);
            } else if (!isLoadingData) {
                progressBar.setVisibility(View.GONE);
            }
        });

        orderViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);

            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Error: " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        // ENHANCED: Better status message handling with auto-refresh
        orderViewModel.getStatusMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                // Smart refresh: Don't refresh immediately, let repository handle it
                Log.d(TAG, "Status update detected, repository will handle auto-refresh");
            }
        });
    }

    // ========== ORDER ACTION HANDLERS WITH SMART UPDATE ==========

    @Override
    public void onConfirmOrder(Order order) {
        String currentStatus = order.getOrderStatus();
        String nextStatus = getNextStatus(currentStatus);

        Log.d(TAG, "onConfirmOrder: " + currentStatus + " -> " + nextStatus);

        if (nextStatus != null) {
            // Optimistic UI update: Remove order from current list immediately if it won't belong
            if (shouldRemoveFromCurrentFilter(nextStatus)) {
                removeOrderFromList(order.getOrderId());
            }

            // Update status
            switch (nextStatus) {
                case "confirmed":
                    orderViewModel.confirmOrder(order.getOrderId());
                    break;
                case "preparing":
                    orderViewModel.prepareOrder(order.getOrderId());
                    break;
                case "ready":
                    orderViewModel.deliverOrder(order.getOrderId());
                    break;
                case "delivered":
                    orderViewModel.completeOrder(order.getOrderId());
                    break;
                default:
                    orderViewModel.updateOrderStatus(order.getOrderId(), nextStatus);
                    break;
            }
            Toast.makeText(getContext(), "Đang cập nhật trạng thái đơn hàng...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Không thể cập nhật từ trạng thái: " + currentStatus, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelOrder(Order order) {
        // Optimistic UI update: Remove order from current list if not showing "all" or "cancelled"
        if (!currentFilter.equals("all") && !currentFilter.equals("cancelled")) {
            removeOrderFromList(order.getOrderId());
        }

        orderViewModel.cancelOrder(order.getOrderId());
        Toast.makeText(getContext(), "Đang hủy đơn hàng...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOrderClick(Order order) {
        Intent intent = OrderDetailActivity.newIntent(getContext(), order.getOrderId());
        startActivity(intent);
    }

    // ========== SMART UI UPDATE METHODS ==========

    private boolean shouldRemoveFromCurrentFilter(String newStatus) {
        if (currentFilter.equals("all")) {
            return false; // "all" tab shows all orders
        }
        return !currentFilter.equalsIgnoreCase(newStatus);
    }

    private void removeOrderFromList(int orderId) {
        Iterator<Order> iterator = allOrders.iterator();
        boolean removed = false;

        while (iterator.hasNext()) {
            Order order = iterator.next();
            if (order.getOrderId() == orderId) {
                iterator.remove();
                removed = true;
                Log.d(TAG, "Optimistically removed order " + orderId + " from UI");
                break;
            }
        }

        if (removed) {
            // Update UI immediately
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    orderAdapter.updateOrders(new ArrayList<>(allOrders));

                    // Show empty state if no orders left
                    if (allOrders.isEmpty()) {
                        recyclerViewOrders.setVisibility(View.GONE);
                        layoutEmptyState.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    private String getNextStatus(String currentStatus) {
        switch (currentStatus.toLowerCase()) {
            case "pending": return "confirmed";
            case "confirmed": return "preparing";
            case "preparing": return "ready";
            case "ready": return "delivered";
            default: return null;
        }
    }

    // ========== AUTO REFRESH CONTROL ==========

    /**
     * Schedule auto refresh after status update
     */
    private void scheduleAutoRefresh() {
        if (!shouldAutoRefresh) return;

        refreshHandler.removeCallbacks(autoRefreshRunnable);
        refreshHandler.postDelayed(autoRefreshRunnable, 2000); // Refresh after 2 seconds
    }

    private final Runnable autoRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (shouldAutoRefresh && !isLoading) {
                Log.d(TAG, "Auto refresh triggered");
                refreshDataSilently();
            }
        }
    };

    /**
     * Refresh data without showing loading indicators
     */
    private void refreshDataSilently() {
        Log.d(TAG, "Silent refresh for current filter: " + currentFilter);
        currentPage = 1;
        isLastPage = false;

        // Don't clear the list immediately to avoid flickering
        orderViewModel.loadOrdersByDirectEndpoint(currentFilter, currentPage, PAGE_SIZE);
    }

    /**
     * Enable/disable auto refresh
     */
    public void setAutoRefresh(boolean enabled) {
        this.shouldAutoRefresh = enabled;
        orderViewModel.enableAutoRefresh(enabled);
        Log.d(TAG, "Auto refresh " + (enabled ? "enabled" : "disabled"));
    }

    // ========== PUBLIC METHODS ==========

    /**
     * Manual refresh method for external calls
     */
    public void refreshData() {
        if (orderViewModel != null) {
            Log.d(TAG, "Manual refreshData called");
            refreshDataComplete();
        }
    }

    /**
     * Load specific status with optimization
     */
    public void loadStatusOptimized(String status) {
        currentFilter = status;
        updateTabAppearance();
        loadFirstPageOptimized();
    }

    /**
     * Get current filter for external access
     */
    public String getCurrentFilter() {
        return currentFilter;
    }

    /**
     * Get current orders count
     */
    public int getCurrentOrdersCount() {
        return allOrders.size();
    }

    // ========== LIFECYCLE METHODS ==========

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - current filter: " + currentFilter);

        // Enable auto refresh when fragment becomes visible
        setAutoRefresh(true);

        // Refresh data if the list is empty or if coming back from order detail
        if (allOrders.isEmpty()) {
            loadFirstPageOptimized();
        } else {
            // Silent refresh to check for updates
            refreshDataSilently();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        // Disable auto refresh when fragment is not visible to save resources
        setAutoRefresh(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");

        // Clean up handlers
        if (refreshHandler != null) {
            refreshHandler.removeCallbacks(autoRefreshRunnable);
        }

        // Disable auto refresh
        setAutoRefresh(false);

        binding = null;
    }

    // ========== DEBUG AND TESTING METHODS ==========

    /**
     * Test endpoint performance
     */
    public void testEndpointPerformance() {
        if (orderViewModel != null) {
            orderViewModel.testEndpointPerformance(currentFilter, 1, PAGE_SIZE);
        }
    }

    /**
     * Force refresh for debugging
     */
    public void forceRefresh() {
        Log.d(TAG, "Force refresh triggered");
        refreshDataComplete();
    }

    /**
     * Simulate order status update for testing
     */
    public void simulateStatusUpdate(int orderId, String newStatus) {
        if (shouldRemoveFromCurrentFilter(newStatus)) {
            removeOrderFromList(orderId);
        }
        scheduleAutoRefresh();
    }

    // ========== ERROR RECOVERY ==========

    /**
     * Retry last operation in case of failure
     */
    public void retryLastOperation() {
        if (!isLoading) {
            Log.d(TAG, "Retrying last operation");
            loadFirstPageOptimized();
        }
    }

    /**
     * Clear error state and reload
     */
    public void clearErrorAndReload() {
        orderViewModel.clearError(); // You may need to implement this in ViewModel
        refreshDataComplete();
    }
}