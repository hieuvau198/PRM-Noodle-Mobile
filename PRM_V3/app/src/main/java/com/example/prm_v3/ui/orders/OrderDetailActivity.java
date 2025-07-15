package com.example.prm_v3.ui.orders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prm_v3.R;
import com.example.prm_v3.adapter.OrderItemAdapter;
import com.example.prm_v3.adapter.OrderComboAdapter;
import com.example.prm_v3.databinding.ActivityOrderDetailBinding;
import com.example.prm_v3.model.Order;

public class OrderDetailActivity extends AppCompatActivity {
    private static final String EXTRA_ORDER_ID = "extra_order_id";

    private ActivityOrderDetailBinding binding;
    private OrderDetailViewModel viewModel;
    private OrderItemAdapter orderItemAdapter;
    private OrderComboAdapter orderComboAdapter;
    private int orderId;

    public static Intent newIntent(Context context, int orderId) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(EXTRA_ORDER_ID, orderId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderId = getIntent().getIntExtra(EXTRA_ORDER_ID, -1);
        if (orderId == -1) {
            finish();
            return;
        }

        setupToolbar();
        setupViewModel();
        setupRecyclerViews();
        observeViewModel();
        setupClickListeners();

        viewModel.loadOrderDetail(orderId);
    }

    private void setupToolbar() {
        if (getSupportActionBar() == null) {
            setSupportActionBar(binding.toolbar);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết đơn hàng #" + orderId);
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(OrderDetailViewModel.class);
    }

    private void setupRecyclerViews() {
        // Setup Order Items RecyclerView
        orderItemAdapter = new OrderItemAdapter();
        binding.recyclerOrderItems.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerOrderItems.setAdapter(orderItemAdapter);

        // Setup Order Combos RecyclerView
        orderComboAdapter = new OrderComboAdapter();
        binding.recyclerOrderCombos.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerOrderCombos.setAdapter(orderComboAdapter);
    }

    private void observeViewModel() {
        viewModel.getOrder().observe(this, this::updateUI);

        viewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.scrollView.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.scrollView.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getStatusMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        binding.btnUpdateStatus.setOnClickListener(v -> {
            Order order = viewModel.getOrder().getValue();
            if (order != null) {
                String currentStatus = order.getOrderStatus();
                // Use specific methods based on current status
                switch (currentStatus) {
                    case "pending":
                        viewModel.confirmOrder(orderId);
                        break;
                    case "confirmed":
                        viewModel.prepareOrder(orderId);
                        break;
                    case "preparing":
                        viewModel.deliverOrder(orderId);
                        break;
                    case "delivered":
                        viewModel.completeOrder(orderId);
                        break;
                    default:
                        Toast.makeText(this, "Không thể cập nhật trạng thái từ: " + currentStatus, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        binding.btnCancelOrder.setOnClickListener(v -> {
            viewModel.cancelOrder(orderId);
        });

        binding.fabRefresh.setOnClickListener(v -> {
            viewModel.loadOrderDetail(orderId);
        });
    }

    private void updateUI(Order order) {
        if (order == null) return;

        // Customer Info
        binding.tvCustomerName.setText(order.getUserName());
        binding.tvCustomerId.setText("ID: " + order.getUserId());

        // Delivery Address
        if (order.hasDeliveryAddress()) {
            binding.tvDeliveryAddress.setText(order.getDeliveryAddress());
        } else {
            binding.tvDeliveryAddress.setText("Không có địa chỉ giao hàng");
        }

        // Notes
        if (order.hasNotes()) {
            binding.labelNotes.setVisibility(View.VISIBLE);
            binding.tvNotes.setVisibility(View.VISIBLE);
            binding.tvNotes.setText(order.getNotes());
        } else {
            binding.labelNotes.setVisibility(View.GONE);
            binding.tvNotes.setVisibility(View.GONE);
        }

        // Order Info
        binding.tvOrderId.setText("Đơn hàng #" + order.getOrderId());
        binding.tvOrderDate.setText(order.getFormattedDate());
        binding.tvOrderStatus.setText(order.getStatusDisplayText());
        binding.tvOrderStatus.setTextColor(getStatusColor(order.getOrderStatus()));

        // Confirmed Date
        if (order.isConfirmed()) {
            binding.tvConfirmedDate.setText(order.getFormattedConfirmedDate());
        } else {
            binding.tvConfirmedDate.setText("Chưa xác nhận");
        }

        // Completed Date
        if (order.isCompleted()) {
            binding.layoutCompletionDate.setVisibility(View.VISIBLE);
            binding.tvCompletedDate.setText(order.getFormattedCompletedDate());
        } else {
            binding.layoutCompletionDate.setVisibility(View.GONE);
        }

        // Order Items
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            binding.recyclerOrderItems.setVisibility(View.VISIBLE);
            orderItemAdapter.updateItems(order.getOrderItems());
        } else {
            binding.recyclerOrderItems.setVisibility(View.GONE);
        }

        // Order Combos
        if (order.getOrderCombos() != null && !order.getOrderCombos().isEmpty()) {
            binding.recyclerOrderCombos.setVisibility(View.VISIBLE);
            orderComboAdapter.updateCombos(order.getOrderCombos());
        } else {
            binding.recyclerOrderCombos.setVisibility(View.GONE);
        }

        // Items Summary
        binding.tvItemCount.setText(order.getItemSummary());

        // Payment Info
        binding.tvTotalAmount.setText(order.getFormattedAmount());
        binding.tvPaymentMethod.setText(order.getPaymentMethodText());
        binding.tvPaymentStatus.setText(order.getPaymentStatusText());

        // Update buttons
        updateButtons(order.getOrderStatus());
    }

    private void updateButtons(String status) {
        switch (status) {
            case "pending":
                binding.btnUpdateStatus.setVisibility(View.VISIBLE);
                binding.btnCancelOrder.setVisibility(View.VISIBLE);
                binding.btnUpdateStatus.setText("Xác nhận đơn hàng");
                break;

            case "confirmed":
                binding.btnUpdateStatus.setVisibility(View.VISIBLE);
                binding.btnCancelOrder.setVisibility(View.VISIBLE);
                binding.btnUpdateStatus.setText("Bắt đầu chuẩn bị");
                break;

            case "preparing":
                binding.btnUpdateStatus.setVisibility(View.VISIBLE);
                binding.btnCancelOrder.setVisibility(View.VISIBLE);
                binding.btnUpdateStatus.setText("Giao hàng");
                break;

            case "delivered":
                binding.btnUpdateStatus.setVisibility(View.VISIBLE);
                binding.btnCancelOrder.setVisibility(View.GONE);
                binding.btnUpdateStatus.setText("Hoàn thành");
                break;

            case "completed":
            case "cancelled":
                binding.btnUpdateStatus.setVisibility(View.GONE);
                binding.btnCancelOrder.setVisibility(View.GONE);
                break;
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "pending": return getColor(R.color.orange_600);
            case "confirmed": return getColor(R.color.blue_600);
            case "preparing": return getColor(R.color.yellow_600);
            case "delivered": return getColor(R.color.purple_600);
            case "completed": return getColor(R.color.green_600);
            case "cancelled": return getColor(R.color.red_600);
            default: return getColor(R.color.gray_600);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}