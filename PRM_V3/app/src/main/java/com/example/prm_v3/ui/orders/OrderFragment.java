package com.example.prm_v3.ui.orders;

import android.content.Intent;
import android.os.Bundle;
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
        loadFirstPage();

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

                    // Check if we should load more data
                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0
                                && totalItemCount >= PAGE_SIZE) {
                            loadNextPage();
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
        loadFirstPage();
    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage - currentFilter: " + currentFilter);
        isLoading = true;
        currentPage = 1;

        if (currentFilter.equals("all")) {
            Log.d(TAG, "Loading all orders with pagination");
            orderViewModel.loadOrdersWithPagination(currentPage, PAGE_SIZE);
        } else {
            Log.d(TAG, "Loading orders by status: " + currentFilter);
            orderViewModel.loadOrdersByStatusWithPagination(currentFilter, currentPage, PAGE_SIZE);
        }
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage - page: " + (currentPage + 1));
        isLoading = true;
        currentPage++;

        if (currentFilter.equals("all")) {
            orderViewModel.loadOrdersWithPagination(currentPage, PAGE_SIZE);
        } else {
            orderViewModel.loadOrdersByStatusWithPagination(currentFilter, currentPage, PAGE_SIZE);
        }
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

    private void updateEmptyState(boolean isEmpty) {
        Log.d(TAG, "updateEmptyState: isEmpty=" + isEmpty + ", currentPage=" + currentPage + ", allOrders.size=" + allOrders.size());

        if (isEmpty && currentPage == 1) {
            Log.d(TAG, "Showing empty state");
            recyclerViewOrders.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Showing recycler view");
            recyclerViewOrders.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void observeViewModel() {
        Log.d(TAG, "observeViewModel setup");

        orderViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            Log.d(TAG, "Orders observer triggered - orders: " + (orders != null ? orders.size() : "null"));

            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);

            if (orders != null && !orders.isEmpty()) {
                Log.d(TAG, "Processing " + orders.size() + " orders");

                // ALWAYS update data regardless of page
                if (currentPage == 1) {
                    allOrders.clear();
                }
                allOrders.addAll(orders);

                // FORCE UI update on main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Update adapter
                        orderAdapter.updateOrders(new ArrayList<>(allOrders));

                        // Force show content
                        recyclerViewOrders.setVisibility(View.VISIBLE);
                        layoutEmptyState.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);

                        Log.d(TAG, "UI updated - RecyclerView shown with " + allOrders.size() + " items");
                    });
                }

                isLastPage = orders.size() < PAGE_SIZE;

            } else {
                Log.d(TAG, "Empty or null orders received");
                if (currentPage == 1 && allOrders.isEmpty()) {
                    // Only show empty state if we truly have no data
                    recyclerViewOrders.setVisibility(View.GONE);
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
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

        orderViewModel.getStatusMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConfirmOrder(Order order) {
        String newStatus = getNextStatus(order.getOrderStatus());
        if (newStatus != null) {
            orderViewModel.updateOrderStatus(order.getOrderId(), newStatus);
            Toast.makeText(getContext(), "Đã cập nhật trạng thái đơn hàng", Toast.LENGTH_SHORT).show();
        }
    }

    // RENAMED to avoid duplicate method error
    public void refreshData() {
        if (orderViewModel != null) {
            Log.d(TAG, "refreshData called from external");
            refreshDataComplete();
        }
    }

    @Override
    public void onCancelOrder(Order order) {
        orderViewModel.updateOrderStatus(order.getOrderId(), "cancelled");
        Toast.makeText(getContext(), "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOrderClick(Order order) {
        Intent intent = OrderDetailActivity.newIntent(getContext(), order.getOrderId());
        startActivity(intent);
    }

    private String getNextStatus(String currentStatus) {
        switch (currentStatus) {
            case "pending": return "confirmed";
            case "confirmed": return "preparing";
            case "preparing": return "delivered";
            case "delivered": return "completed";
            default: return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}