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
import com.example.prm_v3.utils.UserManager;

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
    private UserManager userManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCreateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize UserManager
        userManager = UserManager.getInstance(requireContext());

        // Check if user is logged in and has valid role
        if (!userManager.isLoggedIn() || !userManager.hasValidRole()) {
            String message = userManager.isCustomer() ?
                    "Bạn không có quyền truy cập chức năng này" :
                    "Bạn cần đăng nhập để đặt hàng";
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            // Redirect to login or handle accordingly
            return root;
        }

        setupViewModel();
        setupRecyclerViews();
        observeViewModel();
        setupClickListeners();
        prefillUserInfo();

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
                String buttonText = "Tạo đơn hàng " + viewModel.getCartSummary();
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

    private void prefillUserInfo() {
        // Pre-fill delivery address with user's default address
        String defaultAddress = userManager.getDefaultDeliveryAddress();
        if (defaultAddress != null && !defaultAddress.isEmpty()) {
            binding.etDeliveryAddress.setText(defaultAddress);
        }

        // You can also pre-fill other user info if needed
        // For example, if you have a phone number field:
        // binding.etPhone.setText(userManager.getCurrentUserPhone());
    }

    private void createOrder() {
        // Get current user ID
        int currentUserId = userManager.getCurrentUserId();

        if (currentUserId == -1) {
            Toast.makeText(getContext(), "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            return;
        }

        String deliveryAddress = binding.etDeliveryAddress.getText().toString().trim();
        String notes = binding.etNotes.getText().toString().trim();
        String paymentMethod = getSelectedPaymentMethod();

        if (deliveryAddress.isEmpty()) {
            binding.etDeliveryAddress.setError("Vui lòng nhập địa chỉ giao hàng");
            binding.etDeliveryAddress.requestFocus();
            return;
        }

        // Use current user ID instead of hardcoded value
        viewModel.createOrder(currentUserId, deliveryAddress, notes, paymentMethod);
    }

    private String getSelectedPaymentMethod() {
       return "cash";

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
        binding.btnCreateOrder.setText(" Tạo đơn hàng");
        binding.btnCreateOrder.setEnabled(false);
    }

    // Method này chỉ clear form thông thường (không phải sau khi tạo đơn)
    private void clearForm() {
        clearFormCompletely();

        // Re-fill default address after clearing
        String defaultAddress = userManager.getDefaultDeliveryAddress();
        if (defaultAddress != null && !defaultAddress.isEmpty()) {
            binding.etDeliveryAddress.setText(defaultAddress);
        }
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
        // Check if user is still logged in and has valid role
        if (!userManager.isLoggedIn() || !userManager.hasValidRole()) {
            String message = userManager.isCustomer() ?
                    "Bạn không có quyền truy cập chức năng này" :
                    "Phiên đăng nhập đã hết hạn";
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            // Handle logout or redirect
            return;
        }

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