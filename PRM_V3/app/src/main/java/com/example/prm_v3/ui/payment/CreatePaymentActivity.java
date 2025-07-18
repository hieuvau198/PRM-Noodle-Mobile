package com.example.prm_v3.ui.payment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm_v3.R;
import com.example.prm_v3.databinding.ActivityCreatePaymentBinding;
import com.example.prm_v3.model.Payment;
import com.example.prm_v3.utils.PaymentHelper;

public class CreatePaymentActivity extends AppCompatActivity {
    private static final String TAG = "CreatePaymentActivity";

    private ActivityCreatePaymentBinding binding;
    private CreatePaymentViewModel viewModel;

    public static Intent newIntent(Context context) {
        return new Intent(context, CreatePaymentActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupViewModel();
        setupSpinners();
        setupClickListeners();
        setupTextWatchers(); // Add this
        observeViewModel();

        // Enable button by default for manual entry
        updateCreateButtonState();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tạo thanh toán cho đơn hàng");
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CreatePaymentViewModel.class);
    }

    private void setupSpinners() {
        // Payment Method Spinner
        String[] paymentMethods = {"Tiền mặt", "Ví điện tử", "Thẻ tín dụng", "Thẻ ghi nợ", "Chuyển khoản"};
        ArrayAdapter<String> methodAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, paymentMethods);
        methodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPaymentMethod.setAdapter(methodAdapter);

        // Set default to "Tiền mặt" (Cash)
        binding.spinnerPaymentMethod.setSelection(0);
    }

    private void setupClickListeners() {
        binding.btnCreatePayment.setOnClickListener(v -> {
            Log.d(TAG, "Create payment button clicked");
            createPayment();
        });

        binding.btnCancel.setOnClickListener(v -> finish());

        // Auto-load order info when Order ID is entered
        binding.btnLoadOrder.setOnClickListener(v -> loadOrderInfo());
    }

    // ADD THIS METHOD - Setup text watchers to enable/disable create button
    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateCreateButtonState();
            }
        };

        binding.etOrderId.addTextChangedListener(textWatcher);
        binding.etCustomerName.addTextChangedListener(textWatcher);
        binding.etCustomerId.addTextChangedListener(textWatcher);
        binding.etAmount.addTextChangedListener(textWatcher);
    }

    // ADD THIS METHOD - Update create button state based on form validation
    private void updateCreateButtonState() {
        boolean isValid = validateInputQuick();
        binding.btnCreatePayment.setEnabled(isValid);
        Log.d(TAG, "Create button enabled: " + isValid);
    }

    // ADD THIS METHOD - Quick validation for enabling button
    private boolean validateInputQuick() {
        String orderIdStr = binding.etOrderId.getText().toString().trim();
        String customerName = binding.etCustomerName.getText().toString().trim();
        String customerIdStr = binding.etCustomerId.getText().toString().trim();
        String amountStr = binding.etAmount.getText().toString().trim();

        if (orderIdStr.isEmpty() || customerName.isEmpty() ||
                customerIdStr.isEmpty() || amountStr.isEmpty()) {
            return false;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            int customerId = Integer.parseInt(customerIdStr);
            double amount = Double.parseDouble(amountStr);

            return orderId > 0 && customerId > 0 && amount > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void loadOrderInfo() {
        String orderIdStr = binding.etOrderId.getText().toString().trim();
        if (orderIdStr.isEmpty()) {
            binding.etOrderId.setError("Vui lòng nhập mã đơn hàng");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            Log.d(TAG, "Loading order info for ID: " + orderId);
            viewModel.loadOrderForPayment(orderId);
        } catch (NumberFormatException e) {
            binding.etOrderId.setError("Mã đơn hàng không hợp lệ");
        }
    }

    private void observeViewModel() {
        viewModel.getLoading().observe(this, isLoading -> {
            Log.d(TAG, "Loading state: " + isLoading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);

            // Only disable create button during API calls, not permanently
            if (isLoading) {
                binding.btnCreatePayment.setEnabled(false);
                binding.btnLoadOrder.setEnabled(false);
            } else {
                updateCreateButtonState(); // Re-evaluate based on form state
                binding.btnLoadOrder.setEnabled(true);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Error: " + error);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Log.d(TAG, "Success: " + message);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                // Set result to refresh payment fragment
                setResult(RESULT_OK);
                finish();
            }
        });

        viewModel.getOrderData().observe(this, order -> {
            if (order != null) {
                Log.d(TAG, "Order data loaded: " + order.getOrderId());

                // Auto-fill data from order
                binding.etAmount.setText(String.valueOf((int)order.getTotalAmount()));
                binding.etCustomerName.setText(order.getUserName());
                binding.etCustomerId.setText(String.valueOf(order.getUserId()));

                // Set payment method based on order
                setPaymentMethodSpinner(order.getPaymentMethod());

                // Show order info
                binding.tvOrderInfo.setText(String.format("Đơn hàng #%d - %s - %s",
                        order.getOrderId(),
                        order.getStatusDisplayText(),
                        order.getFormattedAmount()));
                binding.tvOrderInfo.setVisibility(View.VISIBLE);

                // Update button state after auto-fill
                updateCreateButtonState();
            }
        });
    }

    private void setPaymentMethodSpinner(String paymentMethod) {
        if (paymentMethod == null) return;

        String[] methods = {"cash", "digital_wallet", "credit_card", "debit_card", "bank_transfer"};

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].equalsIgnoreCase(paymentMethod)) {
                binding.spinnerPaymentMethod.setSelection(i);
                break;
            }
        }
    }

    private void createPayment() {
        Log.d(TAG, "createPayment() called");

        // Validate input
        if (!validateInput()) {
            Log.w(TAG, "Validation failed");
            return;
        }

        // Create payment object
        Payment payment = new Payment();

        // Set basic info
        try {
            payment.setOrderId(Integer.parseInt(binding.etOrderId.getText().toString().trim()));
            payment.setCustomerUserId(Integer.parseInt(binding.etCustomerId.getText().toString().trim()));
            payment.setCustomerName(binding.etCustomerName.getText().toString().trim());
            payment.setPaymentAmount(Double.parseDouble(binding.etAmount.getText().toString().trim()));

            // Set payment method
            String selectedMethod = getPaymentMethodCode(binding.spinnerPaymentMethod.getSelectedItemPosition());
            payment.setPaymentMethod(selectedMethod);

            // Set default status as pending
            payment.setPaymentStatus("pending");

            // Set transaction reference if provided
            String transactionRef = binding.etTransactionRef.getText().toString().trim();
            if (!transactionRef.isEmpty()) {
                payment.setTransactionReference(transactionRef);
            } else {
                payment.setTransactionReference(PaymentHelper.generateTransactionReference());
            }

            Log.d(TAG, "Creating payment: OrderID=" + payment.getOrderId() +
                    ", Amount=" + payment.getPaymentAmount() +
                    ", Method=" + payment.getPaymentMethod());

            // Send to ViewModel
            viewModel.createPayment(payment);

        } catch (Exception e) {
            Log.e(TAG, "Error creating payment object", e);
            Toast.makeText(this, "Lỗi khi tạo thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput() {
        boolean isValid = true;

        // Validate Order ID
        String orderIdStr = binding.etOrderId.getText().toString().trim();
        if (orderIdStr.isEmpty()) {
            binding.etOrderId.setError("Vui lòng nhập mã đơn hàng");
            isValid = false;
        } else {
            try {
                int orderId = Integer.parseInt(orderIdStr);
                if (orderId <= 0) {
                    binding.etOrderId.setError("Mã đơn hàng không hợp lệ");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                binding.etOrderId.setError("Mã đơn hàng phải là số");
                isValid = false;
            }
        }

        // Validate Customer Name
        String customerName = binding.etCustomerName.getText().toString().trim();
        if (customerName.isEmpty()) {
            binding.etCustomerName.setError("Vui lòng nhập tên khách hàng");
            isValid = false;
        }

        // Validate Customer ID
        String customerIdStr = binding.etCustomerId.getText().toString().trim();
        if (customerIdStr.isEmpty()) {
            binding.etCustomerId.setError("Vui lòng nhập mã khách hàng");
            isValid = false;
        } else {
            try {
                int customerId = Integer.parseInt(customerIdStr);
                if (customerId <= 0) {
                    binding.etCustomerId.setError("Mã khách hàng không hợp lệ");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                binding.etCustomerId.setError("Mã khách hàng phải là số");
                isValid = false;
            }
        }

        // Validate Amount
        String amountStr = binding.etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            binding.etAmount.setError("Vui lòng nhập số tiền");
            isValid = false;
        } else {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    binding.etAmount.setError("Số tiền phải lớn hơn 0");
                    isValid = false;
                } else if (amount > 999999999) {
                    binding.etAmount.setError("Số tiền quá lớn");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                binding.etAmount.setError("Số tiền không hợp lệ");
                isValid = false;
            }
        }

        Log.d(TAG, "Validation result: " + isValid);
        return isValid;
    }

    private String getPaymentMethodCode(int position) {
        String[] methods = {"cash", "digital_wallet", "credit_card", "debit_card", "bank_transfer"};
        return methods[position];
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