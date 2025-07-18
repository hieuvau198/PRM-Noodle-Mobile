// ===== FIXED paymentFragment.java - All errors resolved =====
package com.example.prm_v3.ui.payment;

import android.app.Activity;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.prm_v3.R;
import com.example.prm_v3.adapter.PaymentAdapter;
import com.example.prm_v3.databinding.FragmentPaymentBinding;
import com.example.prm_v3.model.Payment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class paymentFragment extends Fragment implements PaymentAdapter.OnPaymentActionListener {
    private static final String TAG = "paymentFragment";
    private static final int CREATE_PAYMENT_REQUEST_CODE = 1001;

    private FragmentPaymentBinding binding;
    private paymentViewModel paymentViewModel;
    private PaymentAdapter paymentAdapter;
    private List<Payment> allPayments = new ArrayList<>();
    private String currentFilter = "all";

    // Pagination variables
    private int currentPage = 1;
    private static final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    // Auto-refresh control
    private Handler refreshHandler = new Handler(Looper.getMainLooper());
    private boolean shouldAutoRefresh = true;

    // Tab views - FIXED: Use correct variable names
    private TextView tabAll, tabPending, tabProcessing, tabComplete, tabFailed;
    private RecyclerView recyclerViewPayments;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState, layoutPaginationLoading;
    private FloatingActionButton fabAddPayment; // FIXED: Add this variable

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView started");
        paymentViewModel = new ViewModelProvider(this).get(paymentViewModel.class);
        binding = FragmentPaymentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initViews();
        setupRecyclerView();
        setupTabListeners();
        setupFAB();
        observeViewModel();

        // Load initial data
        loadFirstPage();

        return root;
    }

    private void initViews() {
        Log.d(TAG, "initViews");
        View root = binding.getRoot();

        // Tab views
        tabAll = binding.tabAll;
        tabPending = binding.tabPending;
        tabProcessing = binding.tabProcessing;
        tabComplete = binding.tabComplete;
        tabFailed = binding.tabFailed;

        // Main views - sử dụng tên chính xác từ XML với ViewBinding
        recyclerViewPayments = binding.recyclerViewPayments;
        swipeRefreshLayout = binding.swipeRefreshLayout;

        // FIXED: Sử dụng findViewById với tên chính xác từ XML
        progressBar = root.findViewById(R.id.progress_bar);
        layoutEmptyState = root.findViewById(R.id.layout_empty_state);
        layoutPaginationLoading = root.findViewById(R.id.layout_pagination_loading);
        fabAddPayment = root.findViewById(R.id.fab_add_payment);
    }

    private void setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView");
        paymentAdapter = new PaymentAdapter();
        paymentAdapter.setOnPaymentActionListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewPayments.setLayoutManager(layoutManager);
        recyclerViewPayments.setAdapter(paymentAdapter);

        // Setup pagination scroll listener
        recyclerViewPayments.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        tabProcessing.setOnClickListener(v -> selectTab("processing"));
        tabComplete.setOnClickListener(v -> selectTab("complete"));  // Updated: paid -> complete
        tabFailed.setOnClickListener(v -> selectTab("failed"));
    }

    private void setupFAB() {
        // Ensure FAB is visible and functional
        if (fabAddPayment != null) {
            fabAddPayment.setVisibility(View.VISIBLE);
            fabAddPayment.setOnClickListener(v -> {
                Intent intent = CreatePaymentActivity.newIntent(getContext());
                startActivityForResult(intent, CREATE_PAYMENT_REQUEST_CODE);
            });
        }
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
        allPayments.clear();
        paymentAdapter.setPayments(new ArrayList<>());
        loadFirstPage();
    }

    // ========== LOADING METHODS ==========

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage - currentFilter: " + currentFilter);
        isLoading = true;
        currentPage = 1;

        paymentViewModel.enableAutoRefresh(true);

        // Load based on current filter
        if (currentFilter.equals("all")) {
            paymentViewModel.loadPayments(currentPage, PAGE_SIZE);
        } else {
            paymentViewModel.loadPaymentsByStatus(currentFilter, currentPage, PAGE_SIZE);
        }
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage - page: " + (currentPage + 1));
        isLoading = true;
        currentPage++;

        if (layoutPaginationLoading != null) {
            layoutPaginationLoading.setVisibility(View.VISIBLE);
        }

        if (currentFilter.equals("all")) {
            paymentViewModel.loadPayments(currentPage, PAGE_SIZE);
        } else {
            paymentViewModel.loadPaymentsByStatus(currentFilter, currentPage, PAGE_SIZE);
        }
    }

    private void updateTabAppearance() {
        // Reset all tabs
        resetTab(tabAll);
        resetTab(tabPending);
        resetTab(tabProcessing);
        resetTab(tabComplete);  // Updated: paid -> complete
        resetTab(tabFailed);

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
            case "all":
                return tabAll;
            case "pending":
                return tabPending;
            case "processing":
                return tabProcessing;
            case "complete":
                return tabComplete;  // Updated: paid -> complete
            case "failed":
                return tabFailed;
            default:
                return tabAll;
        }
    }

    private void observeViewModel() {
        Log.d(TAG, "observeViewModel setup");

        paymentViewModel.getPayments().observe(getViewLifecycleOwner(), payments -> {
            Log.d(TAG, "Payments observer triggered - payments: " + (payments != null ? payments.size() : "null"));

            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);

            if (layoutPaginationLoading != null) {
                layoutPaginationLoading.setVisibility(View.GONE);
            }

            if (payments != null) {
                Log.d(TAG, "Processing " + payments.size() + " payments");

                // Update data
                if (currentPage == 1) {
                    allPayments.clear();
                }

                if (!payments.isEmpty()) {
                    allPayments.addAll(payments);
                }

                // Force UI update on main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        paymentAdapter.setPayments(new ArrayList<>(allPayments));

                        if (allPayments.isEmpty()) {
                            recyclerViewPayments.setVisibility(View.GONE);
                            layoutEmptyState.setVisibility(View.VISIBLE);
                            updateEmptyStateMessage();
                            showCreatePaymentTutorial();
                        } else {
                            recyclerViewPayments.setVisibility(View.VISIBLE);
                            layoutEmptyState.setVisibility(View.GONE);
                        }

                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "UI updated - showing " + allPayments.size() + " payments");
                    });
                }

                isLastPage = payments.size() < PAGE_SIZE;
            } else {
                Log.d(TAG, "Received null payments");
                if (currentPage == 1) {
                    allPayments.clear();
                    paymentAdapter.setPayments(new ArrayList<>());
                    recyclerViewPayments.setVisibility(View.GONE);
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    updateEmptyStateMessage();
                    showCreatePaymentTutorial();
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        paymentViewModel.getLoading().observe(getViewLifecycleOwner(), isLoadingData -> {
            Log.d(TAG, "Loading: " + isLoadingData);
            if (isLoadingData && currentPage == 1 && !swipeRefreshLayout.isRefreshing()) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerViewPayments.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.GONE);
            } else if (!isLoadingData) {
                progressBar.setVisibility(View.GONE);
            }
        });

        paymentViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);

            if (layoutPaginationLoading != null) {
                layoutPaginationLoading.setVisibility(View.GONE);
            }

            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Error: " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        paymentViewModel.getStatusMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                scheduleAutoRefresh();
            }
        });
    }

    private void showCreatePaymentTutorial() {
        if (allPayments.isEmpty() && currentFilter.equals("all")) {
            // Show tutorial when no payments exist
            Toast.makeText(getContext(),
                    "Nhấn nút + để tạo thanh toán cho đơn hàng",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void updateEmptyStateMessage() {
        TextView emptyMessage = layoutEmptyState.findViewById(R.id.tv_empty_message);
        if (emptyMessage != null) {
            String message = "Không có thanh toán nào";
            if (currentFilter.equals("pending")) {
                message = "Chưa có thanh toán chờ xử lý\nNhấn nút + để tạo thanh toán mới";
            } else if (currentFilter.equals("processing")) {
                message = "Không có thanh toán đang xử lý";
            } else if (currentFilter.equals("complete")) {  // Updated: paid -> complete
                message = "Chưa có thanh toán đã hoàn thành";
            } else if (currentFilter.equals("failed")) {
                message = "Không có thanh toán thất bại";
            }
            emptyMessage.setText(message);
        }
    }

    // ========== PAYMENT ACTION HANDLERS ==========

    @Override
    public void onProcessPayment(Payment payment) {
        Log.d(TAG, "onProcessPayment: " + payment.getPaymentId());
        String currentStatus = payment.getPaymentStatus();

        if ("pending".equalsIgnoreCase(currentStatus)) {
            // Move to processing
            processPaymentStatus(payment, "processing");
        } else if ("processing".equalsIgnoreCase(currentStatus)) {
            // Move to complete (updated from paid)
            processPaymentStatus(payment, "complete");
        }
    }

    private void processPaymentStatus(Payment payment, String targetStatus) {
        // Optimistic UI update
        if (shouldRemoveFromCurrentFilter(targetStatus)) {
            removePaymentFromList(payment.getPaymentId());
        }

        if ("processing".equalsIgnoreCase(targetStatus)) {
            paymentViewModel.processPayment(payment.getPaymentId());
            Toast.makeText(getContext(), "Đang xử lý thanh toán...", Toast.LENGTH_SHORT).show();
        } else if ("complete".equalsIgnoreCase(targetStatus)) {  // Updated: paid -> complete
            paymentViewModel.completePayment(payment.getPaymentId());
            Toast.makeText(getContext(), "Đang hoàn tất thanh toán...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailPayment(Payment payment) {
        Log.d(TAG, "onFailPayment: " + payment.getPaymentId());

        // Show confirmation dialog
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn đánh dấu thanh toán này là thất bại?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    // Optimistic UI update
                    if (shouldRemoveFromCurrentFilter("failed")) {
                        removePaymentFromList(payment.getPaymentId());
                    }

                    paymentViewModel.failPayment(payment.getPaymentId());
                    Toast.makeText(getContext(), "Đang cập nhật trạng thái thất bại...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onPaymentClick(Payment payment) {
        // Navigate to payment detail
        Intent intent = PaymentDetailActivity.newIntent(getContext(), payment.getPaymentId());
        startActivity(intent);
    }

    // ========== SMART UI UPDATE METHODS ==========

    private boolean shouldRemoveFromCurrentFilter(String newStatus) {
        if (currentFilter.equals("all")) {
            return false; // "all" tab shows all payments
        }
        return !currentFilter.equalsIgnoreCase(newStatus);
    }

    private void removePaymentFromList(int paymentId) {
        Iterator<Payment> iterator = allPayments.iterator();
        boolean removed = false;

        while (iterator.hasNext()) {
            Payment payment = iterator.next();
            if (payment.getPaymentId() == paymentId) {
                iterator.remove();
                removed = true;
                Log.d(TAG, "Optimistically removed payment " + paymentId + " from UI");
                break;
            }
        }

        if (removed) {
            // Update UI immediately
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    paymentAdapter.setPayments(new ArrayList<>(allPayments));

                    // Show empty state if no payments left
                    if (allPayments.isEmpty()) {
                        recyclerViewPayments.setVisibility(View.GONE);
                        layoutEmptyState.setVisibility(View.VISIBLE);
                        updateEmptyStateMessage();
                    }
                });
            }
        }
    }

    // ========== AUTO REFRESH CONTROL ==========

    private void scheduleAutoRefresh() {
        if (!shouldAutoRefresh) return;

        refreshHandler.removeCallbacks(autoRefreshRunnable);
        refreshHandler.postDelayed(autoRefreshRunnable, 2000);
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

    private void refreshDataSilently() {
        Log.d(TAG, "Silent refresh for current filter: " + currentFilter);
        currentPage = 1;
        isLastPage = false;

        if (currentFilter.equals("all")) {
            paymentViewModel.loadPayments(currentPage, PAGE_SIZE);
        } else {
            paymentViewModel.loadPaymentsByStatus(currentFilter, currentPage, PAGE_SIZE);
        }
    }

    public void setAutoRefresh(boolean enabled) {
        this.shouldAutoRefresh = enabled;
        paymentViewModel.enableAutoRefresh(enabled);
        Log.d(TAG, "Auto refresh " + (enabled ? "enabled" : "disabled"));
    }

    // ========== ACTIVITY RESULT HANDLER ==========

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_PAYMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Payment created successfully, refresh the list
            Log.d(TAG, "Payment created successfully, refreshing list");

            // Show success message
            Toast.makeText(getContext(), "Thanh toán đã được tạo thành công!", Toast.LENGTH_SHORT).show();

            // Switch to pending tab to show new payment
            if (!currentFilter.equals("pending")) {
                selectTab("pending");
            } else {
                // If already on pending tab, just refresh
                refreshDataComplete();
            }
        }
    }

    // ========== PUBLIC METHODS FOR EXTERNAL ACCESS ==========

    /**
     * Manual refresh method for external calls
     */
    public void refreshData() {
        if (paymentViewModel != null) {
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
        loadFirstPage();
    }

    /**
     * Get current filter for external access
     */
    public String getCurrentFilter() {
        return currentFilter;
    }

    /**
     * Get current payments count
     */
    public int getCurrentPaymentsCount() {
        return allPayments.size();
    }

    /**
     * Refresh after payment creation from external source
     */
    public void refreshAfterPaymentCreation(String paymentStatus) {
        Log.d(TAG, "refreshAfterPaymentCreation - paymentStatus: " + paymentStatus);

        // If the new payment status matches current filter, refresh
        if (currentFilter.equals("all") || currentFilter.equals(paymentStatus)) {
            refreshDataComplete();
        }
    }

    // ========== LIFECYCLE METHODS ==========

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - current filter: " + currentFilter);

        setAutoRefresh(true);

        if (allPayments.isEmpty()) {
            loadFirstPage();
        } else {
            refreshDataSilently();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        setAutoRefresh(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");

        if (refreshHandler != null) {
            refreshHandler.removeCallbacks(autoRefreshRunnable);
        }

        setAutoRefresh(false);
        binding = null;
    }

    // ========== ERROR RECOVERY ==========

    /**
     * Retry last operation in case of failure
     */
    public void retryLastOperation() {
        if (!isLoading) {
            Log.d(TAG, "Retrying last operation");
            loadFirstPage();
        }
    }

    /**
     * Clear error state and reload
     */
    public void clearErrorAndReload() {
        paymentViewModel.clearError();
        refreshDataComplete();
    }

    // ========== SEARCH AND FILTER HELPERS ==========

    /**
     * Filter payments by search query
     */
    public void searchPayments(String query) {
        // For now, just show all payments and let user manually search
        // In future, can implement server-side search
        if (query == null || query.trim().isEmpty()) {
            // Show all payments for current filter
            loadFirstPage();
        } else {
            // Future implementation: server-side search
            Log.d(TAG, "Search not implemented yet: " + query);
            Toast.makeText(getContext(), "Tính năng tìm kiếm đang phát triển", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get payment statistics for current filter
     */
    public void loadPaymentStatistics() {
        paymentViewModel.loadPaymentStatistics();
    }

    // ========== DEBUG AND TESTING METHODS ==========

    /**
     * Test performance
     */
    public void testPerformance() {
        Log.d(TAG, "Testing performance with current filter: " + currentFilter);
        long startTime = System.currentTimeMillis();
        loadFirstPage();
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "Load time: " + (endTime - startTime) + "ms");
    }

    /**
     * Force refresh for debugging
     */
    public void forceRefresh() {
        Log.d(TAG, "Force refresh triggered");
        refreshDataComplete();
    }

    /**
     * Simulate payment status update for testing
     */
    public void simulateStatusUpdate(int paymentId, String newStatus) {
        if (shouldRemoveFromCurrentFilter(newStatus)) {
            removePaymentFromList(paymentId);
        }
        scheduleAutoRefresh();
    }
}