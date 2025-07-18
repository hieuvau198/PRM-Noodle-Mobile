package com.example.prm_v3.ui.orders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prm_v3.R;
import com.example.prm_v3.adapter.OrderItemAdapter;
import com.example.prm_v3.adapter.OrderComboAdapter;
import com.example.prm_v3.api.ApiClient;
import com.example.prm_v3.api.ApiService;
import com.example.prm_v3.api.CreatePaymentRequest;
import com.example.prm_v3.databinding.ActivityOrderDetailBinding;
import com.example.prm_v3.model.Order;
import com.example.prm_v3.model.Payment;
import com.example.prm_v3.utils.StatusHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private static final String EXTRA_ORDER_ID = "extra_order_id";
    private static final String TAG = "OrderDetailActivity";

    private ActivityOrderDetailBinding binding;
    private OrderDetailViewModel viewModel;
    private OrderItemAdapter orderItemAdapter;
    private OrderComboAdapter orderComboAdapter;
    private ApiService apiService;
    private List<Payment> cachedPayments = new ArrayList<>(); // Cache payments để kiểm tra nhanh
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

        // Khởi tạo ApiService
        apiService = ApiClient.getApiService();

        setupToolbar();
        setupViewModel();
        setupRecyclerViews();
        observeViewModel();
        setupClickListeners();

        // Load và cache payments
        loadAndCachePayments();

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

                Log.d("OrderDetailActivity", "Current status: " + currentStatus);

                // FIXED: Backend workflow mapping
                switch (currentStatus.toLowerCase()) {
                    case "pending":
                        viewModel.confirmOrder(orderId);
                        break;
                    case "confirmed":
                        viewModel.prepareOrder(orderId);
                        break;
                    case "preparing":
                        // FIXED: preparing -> ready via deliverOrder API
                        viewModel.deliverOrder(orderId);
                        break;
                    case "ready":
                        // FIXED: ready -> delivered via completeOrder API
                        viewModel.completeOrder(orderId);
                        break;
                    default:
                        Toast.makeText(this, "Không thể cập nhật từ trạng thái: " + currentStatus, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        binding.btnCancelOrder.setOnClickListener(v -> {
            Order order = viewModel.getOrder().getValue();
            if (order != null) {
                String buttonText = binding.btnCancelOrder.getText().toString();
                if ("Hủy đơn".equals(buttonText)) {
                    viewModel.cancelOrder(orderId);
                } else if ("Tạo hóa đơn".equals(buttonText)) {
                    checkOrderPaymentAndCreateInvoice(order);
                }
            }
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
        if (status == null) {
            // Hide all buttons if status is null
            binding.btnUpdateStatus.setVisibility(View.GONE);
            binding.btnCancelOrder.setVisibility(View.GONE);
            return;
        }
        
        boolean canCancel = StatusHelper.canCancelOrder(status);
        boolean canCreateInvoice = StatusHelper.canCreateInvoice(status);
        boolean orderHasPayment = hasPayment(orderId); // Kiểm tra xem đã có payment chưa

        switch (status.toLowerCase()) {
            case "pending":
                binding.btnUpdateStatus.setVisibility(View.VISIBLE);
                binding.btnCancelOrder.setVisibility(View.VISIBLE);  // CHỈ hiện ở trạng thái pending
                binding.btnCancelOrder.setText("Hủy đơn");
                binding.btnCancelOrder.setBackgroundTintList(getColorStateList(R.color.red_600));
                binding.btnUpdateStatus.setText("Xác nhận đơn hàng");
                break;

            case "confirmed":
                binding.btnUpdateStatus.setVisibility(View.VISIBLE);
                binding.btnUpdateStatus.setText("Bắt đầu chuẩn bị");
                
                if (canCreateInvoice && !orderHasPayment) {
                    binding.btnCancelOrder.setVisibility(View.VISIBLE);
                    binding.btnCancelOrder.setText("Tạo hóa đơn");
                    binding.btnCancelOrder.setBackgroundTintList(getColorStateList(R.color.blue_600));
                } else {
                    binding.btnCancelOrder.setVisibility(View.GONE);
                }
                break;

            case "preparing":
                binding.btnUpdateStatus.setVisibility(View.VISIBLE);
                binding.btnUpdateStatus.setText("Sẵn sàng giao");  // preparing -> ready
                
                if (canCreateInvoice && !orderHasPayment) {
                    binding.btnCancelOrder.setVisibility(View.VISIBLE);
                    binding.btnCancelOrder.setText("Tạo hóa đơn");
                    binding.btnCancelOrder.setBackgroundTintList(getColorStateList(R.color.blue_600));
                } else {
                    binding.btnCancelOrder.setVisibility(View.GONE);
                }
                break;

            case "ready":
                binding.btnUpdateStatus.setVisibility(View.VISIBLE);
                binding.btnUpdateStatus.setText("Hoàn thành");     // ready -> delivered
                
                if (canCreateInvoice && !orderHasPayment) {
                    binding.btnCancelOrder.setVisibility(View.VISIBLE);
                    binding.btnCancelOrder.setText("Tạo hóa đơn");
                    binding.btnCancelOrder.setBackgroundTintList(getColorStateList(R.color.blue_600));
                } else {
                    binding.btnCancelOrder.setVisibility(View.GONE);
                }
                break;

            case "delivered":  // Final status
            case "cancelled":
                binding.btnUpdateStatus.setVisibility(View.GONE);
                binding.btnCancelOrder.setVisibility(View.GONE);
                break;
        }
    }

    // ========== PAYMENT METHODS ==========

    private void checkOrderPaymentAndCreateInvoice(Order order) {
        Log.d(TAG, "Checking payment for order: " + order.getOrderId());
        
        // Gọi API để lấy tất cả payments và tìm payment của order này
        Call<List<Payment>> call = apiService.getPayments();
        call.enqueue(new Callback<List<Payment>>() {
            @Override
            public void onResponse(Call<List<Payment>> call, Response<List<Payment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Payment> payments = response.body();
                    
                    // Tìm payment của order này
                    boolean hasPayment = false;
                    for (Payment payment : payments) {
                        if (payment.getOrderId() == order.getOrderId()) {
                            hasPayment = true;
                            break;
                        }
                    }
                    
                    if (hasPayment) {
                        Toast.makeText(OrderDetailActivity.this, 
                            "Đơn hàng #" + order.getOrderId() + " đã có hóa đơn thanh toán", 
                            Toast.LENGTH_LONG).show();
                    } else {
                        // Chưa có payment, có thể tạo hóa đơn
                        showCreateInvoiceDialog(order);
                    }
                } else {
                    Log.e(TAG, "Error checking payments: " + response.code());
                    Toast.makeText(OrderDetailActivity.this, "Lỗi khi kiểm tra hóa đơn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Payment>> call, Throwable t) {
                Log.e(TAG, "Network error checking payments", t);
                Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreateInvoiceDialog(Order order) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Tạo hóa đơn thanh toán")
                .setMessage("Bạn có muốn tạo hóa đơn thanh toán cho đơn hàng #" + order.getOrderId() + " không?\n" +
                          "Số tiền: " + order.getFormattedAmount())
                .setPositiveButton("Tạo hóa đơn", (dialog, which) -> {
                    createPaymentForOrder(order);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void createPaymentForOrder(Order order) {
        Log.d(TAG, "Creating payment for order: " + order.getOrderId());
        
        // Tạo request từ order
        // TODO: Cần lấy thông tin staff thực tế từ session/preferences
        int staffUserId = 1; // Placeholder - cần lấy từ logged in user
        String staffName = "Staff User"; // Placeholder - cần lấy từ logged in user
        
        CreatePaymentRequest request = CreatePaymentRequest.fromOrder(order, staffUserId, staffName);
        
        // Gọi API tạo payment
        Call<Payment> call = apiService.createPaymentFromOrder(request);
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Payment createdPayment = response.body();
                    Log.d(TAG, "Payment created successfully: " + createdPayment.getPaymentId());
                    
                    // Thêm payment mới vào cache ngay lập tức
                    cachedPayments.add(createdPayment);
                    
                    Toast.makeText(OrderDetailActivity.this, 
                        "Tạo hóa đơn thành công! Mã hóa đơn: #" + createdPayment.getPaymentId(), 
                        Toast.LENGTH_LONG).show();
                    
                    // Refresh button state để ẩn nút tạo hóa đơn
                    if (viewModel.getOrder().getValue() != null) {
                        updateButtons(viewModel.getOrder().getValue().getOrderStatus());
                    }
                    
                } else {
                    String errorMsg = "Lỗi khi tạo hóa đơn";
                    if (response.code() == 400) {
                        errorMsg = "Dữ liệu không hợp lệ";
                    } else if (response.code() == 409) {
                        errorMsg = "Đơn hàng đã có hóa đơn";
                    }
                    Log.e(TAG, "Error creating payment: " + response.code());
                    Toast.makeText(OrderDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                Log.e(TAG, "Network error creating payment", t);
                Toast.makeText(OrderDetailActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ========== HELPER METHODS ==========
    
    /**
     * Load và cache payments để kiểm tra hasPayment nhanh hơn
     */
    private void loadAndCachePayments() {
        Log.d(TAG, "Loading and caching payments...");
        
        Call<List<Payment>> call = apiService.getPayments();
        call.enqueue(new Callback<List<Payment>>() {
            @Override
            public void onResponse(Call<List<Payment>> call, Response<List<Payment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cachedPayments.clear();
                    cachedPayments.addAll(response.body());
                    Log.d(TAG, "Cached " + cachedPayments.size() + " payments");
                    
                    // Cập nhật lại button state sau khi cache xong
                    updateButtonStateIfOrderLoaded();
                } else {
                    Log.e(TAG, "Error loading payments for cache: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Payment>> call, Throwable t) {
                Log.e(TAG, "Failed to load payments for cache: " + t.getMessage());
            }
        });
    }
    
    /**
     * Kiểm tra xem order đã có payment chưa
     */
    private boolean hasPayment(int orderId) {
        for (Payment payment : cachedPayments) {
            if (payment.getOrderId() == orderId) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Cập nhật button state sau khi cache payments và order được load
     */
    private void updateButtonStateIfOrderLoaded() {
        if (viewModel != null && viewModel.getOrder().getValue() != null) {
            Order order = viewModel.getOrder().getValue();
            updateButtons(order.getOrderStatus());
        }
    }

    private int getStatusColor(String status) {
        if (status == null) {
            return getColor(R.color.gray_600);
        }
        
        switch (status.toLowerCase()) {
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