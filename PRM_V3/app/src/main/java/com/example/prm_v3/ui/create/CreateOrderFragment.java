package com.example.prm_v3.ui.create;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prm_v3.R;
import com.example.prm_v3.adapter.ComboCreateAdapter;
import com.example.prm_v3.adapter.ProductCreateAdapter;
import com.example.prm_v3.databinding.FragmentCreateBinding;
import com.example.prm_v3.model.Combo;
import com.example.prm_v3.model.Product;
import com.example.prm_v3.model.Topping;
import com.example.prm_v3.ui.orders.OrderDetailActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateOrderFragment extends Fragment implements
        ProductCreateAdapter.OnProductQuantityChangeListener,
        ComboCreateAdapter.OnComboQuantityChangeListener {

    private FragmentCreateBinding binding;
    private CreateOrderViewModel viewModel;
    private ProductCreateAdapter productAdapter;
    private ComboCreateAdapter comboAdapter;
    private Map<Integer, CheckBox> toppingCheckBoxes = new HashMap<>();

    // Hardcoded user ID for demo - in real app, get from login session
    private static final int DEMO_USER_ID = 11;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCreateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupViewModel();
        setupRecyclerViews();
        observeViewModel();
        setupClickListeners();

        // Load initial data
        viewModel.loadInitialData();

        return root;
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CreateOrderViewModel.class);
    }

    private void setupRecyclerViews() {
        // Setup Products RecyclerView
        productAdapter = new ProductCreateAdapter(this);
        binding.recyclerProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerProducts.setAdapter(productAdapter);
        binding.recyclerProducts.setNestedScrollingEnabled(false);

        // Setup Combos RecyclerView
        comboAdapter = new ComboCreateAdapter(this);
        binding.recyclerCombos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerCombos.setAdapter(comboAdapter);
        binding.recyclerCombos.setNestedScrollingEnabled(false);
    }

    private void observeViewModel() {
        // Observe products
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                productAdapter.updateProducts(products);
            }
        });

        // Observe combos
        viewModel.getCombos().observe(getViewLifecycleOwner(), combos -> {
            if (combos != null) {
                comboAdapter.updateCombos(combos);
            }
        });

        // Observe toppings
        viewModel.getToppings().observe(getViewLifecycleOwner(), this::setupToppings);

        // Observe loading state
        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        // Observe errors
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe messages
        viewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe total amount
        viewModel.getTotalAmount().observe(getViewLifecycleOwner(), total -> {
            if (total != null) {
                binding.tvSubtotal.setText(viewModel.getFormattedTotal());
                binding.tvTotal.setText(viewModel.getFormattedTotal());
            }
        });

        // Observe total items
        viewModel.getTotalItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                String buttonText = "🛒 Tạo đơn hàng " + viewModel.getCartSummary();
                binding.btnCreateOrder.setText(buttonText);
                binding.btnCreateOrder.setEnabled(items > 0);
            }
        });

        // Observe created order - QUAN TRỌNG: Xử lý sau khi tạo đơn thành công
        viewModel.getCreatedOrder().observe(getViewLifecycleOwner(), order -> {
            if (order != null) {
                // Clear the form TRƯỚC KHI navigate
                clearFormCompletely();

                // Show success message
                Toast.makeText(getContext(), "Đặt hàng thành công! Đơn hàng #" + order.getOrderId(), Toast.LENGTH_LONG).show();

                // Navigate to order detail screen
                Intent intent = OrderDetailActivity.newIntent(getContext(), order.getOrderId());
                startActivity(intent);

                // Reset created order trong ViewModel để tránh trigger lại
                viewModel.resetCreatedOrder();
            }
        });
    }

    private void setupToppings(List<Topping> toppings) {
        if (toppings == null || toppings.isEmpty()) return;

        binding.gridToppings.removeAllViews();
        toppingCheckBoxes.clear();

        for (Topping topping : toppings) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(topping.getToppingName() + " (+" + topping.getFormattedPrice() + ")");
            checkBox.setTextSize(14);

            // Set layout parameters for GridLayout
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.width = 0;
            checkBox.setLayoutParams(params);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                viewModel.toggleTopping(topping.getToppingId(), isChecked);
            });

            binding.gridToppings.addView(checkBox);
            toppingCheckBoxes.put(topping.getToppingId(), checkBox);
        }
    }

    private void setupClickListeners() {
        binding.btnCreateOrder.setOnClickListener(v -> createOrder());

        binding.btnSearch.setOnClickListener(v -> {
            // Implement search functionality if needed
            Toast.makeText(getContext(), "Tính năng tìm kiếm đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void createOrder() {
        String deliveryAddress = binding.etDeliveryAddress.getText().toString().trim();
        String notes = binding.etNotes.getText().toString().trim();
        String paymentMethod = getSelectedPaymentMethod();

        if (deliveryAddress.isEmpty()) {
            binding.etDeliveryAddress.setError("Vui lòng nhập địa chỉ giao hàng");
            binding.etDeliveryAddress.requestFocus();
            return;
        }

        viewModel.createOrder(DEMO_USER_ID, deliveryAddress, notes, paymentMethod);
    }

    private String getSelectedPaymentMethod() {
        if (binding.rbCash.isChecked()) {
            return "cash";
        } else if (binding.rbDigitalWallet.isChecked()) {
            return "digital_wallet";
        } else if (binding.rbCard.isChecked()) {
            return "credit_card";
        } else {
            return "cash"; // default
        }
    }

    // QUAN TRỌNG: Method này clear form hoàn toàn
    private void clearFormCompletely() {
        // Clear input fields
        binding.etDeliveryAddress.setText("");
        binding.etNotes.setText("");
        binding.rbCash.setChecked(true);

        // Clear all topping selections
        for (CheckBox checkBox : toppingCheckBoxes.values()) {
            checkBox.setChecked(false);
        }

        // Clear cart in viewmodel - QUAN TRỌNG
        viewModel.clearCart();

        // Force refresh adapters để cập nhật UI ngay lập tức
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
        if (comboAdapter != null) {
            comboAdapter.notifyDataSetChanged();
        }

        // Reset UI manually
        binding.tvSubtotal.setText("0đ");
        binding.tvTotal.setText("0đ");
        binding.btnCreateOrder.setText("🛒 Tạo đơn hàng");
        binding.btnCreateOrder.setEnabled(false);
    }

    // Method này chỉ clear form thông thường (không phải sau khi tạo đơn)
    private void clearForm() {
        clearFormCompletely();
    }

    // ProductCreateAdapter.OnProductQuantityChangeListener implementation
    @Override
    public void onProductQuantityChanged(Product product, int quantity) {
        viewModel.updateProductQuantity(product.getProductId(), quantity);
    }

    @Override
    public int getCurrentProductQuantity(int productId) {
        return viewModel.getProductQuantity(productId);
    }

    // ComboCreateAdapter.OnComboQuantityChangeListener implementation
    @Override
    public void onComboQuantityChanged(Combo combo, int quantity) {
        viewModel.updateComboQuantity(combo.getComboId(), quantity);
    }

    @Override
    public int getCurrentComboQuantity(int comboId) {
        return viewModel.getComboQuantity(comboId);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh UI when fragment becomes visible again
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
        if (comboAdapter != null) {
            comboAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}