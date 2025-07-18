package com.example.prm_v3.ui.payment;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class paymentFragment extends Fragment implements PaymentAdapter.OnPaymentActionListener {
    private static final String TAG = "paymentFragment";

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

    // Tab views
    private TextView tabAll, tabPending, tabProcessing, tabPaid;
    private RecyclerView recyclerViewPayments;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState, layoutPaginationLoading;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView started");
        paymentViewModel = new ViewModelProvider(this).get(paymentViewModel.class);
        binding = FragmentPaymentBinding.inflate(inflater, container, false);
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
        tabProcessing = binding.tabProcessing;
        tabPaid = binding.tabPaid;

        recyclerViewPayments = binding.recyclerViewPayments;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        progressBar = binding.progressBar;
        layoutEmptyState = binding.layoutEmptyState;
        layoutPaginationLoading = binding.layoutPaginationLoading;
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
        tabPaid.setOnClickListener(v -> selectTab("paid"));
    }

    private void selectTab(String filter) {
        Log.d(TAG, "selectTab: " + filter);
        currentFilter = filter;
        updateTabAppearance();
        
        Log.d(TAG, "Starting refresh for filter: " + filter);
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
        resetTab(tabPaid);

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
            case "processing": return tabProcessing;
            case "paid": return tabPaid;
            default: return tabAll;
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

    // ========== PAYMENT ACTION HANDLERS ==========

    @Override
    public void onProcessPayment(Payment payment) {
        Log.d(TAG, "onProcessPayment: " + payment.getPaymentId());
        String currentStatus = payment.getPaymentStatus();

        if ("pending".equalsIgnoreCase(currentStatus)) {
            // Move to processing
            processPaymentStatus(payment, "processing");
        } else if ("processing".equalsIgnoreCase(currentStatus)) {
            // Move to paid (complete)
            processPaymentStatus(payment, "paid");
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
        } else if ("paid".equalsIgnoreCase(targetStatus)) {
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
                    // Always remove failed payments from current view since there's no failed tab
                    removePaymentFromList(payment.getPaymentId());

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
        
        // Handle both "paid" and "complete" for the paid filter
        if (currentFilter.equalsIgnoreCase("paid")) {
            return !(newStatus.equalsIgnoreCase("paid") || newStatus.equalsIgnoreCase("complete"));
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
}