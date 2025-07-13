package com.example.prm_noodle_mobile.customer.orderconfirm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.api.ApiClient;
import com.example.prm_noodle_mobile.data.api.OrderApi;
import com.example.prm_noodle_mobile.data.api.ComboApi;
import com.example.prm_noodle_mobile.data.api.ProductApi;
import com.example.prm_noodle_mobile.data.api.ToppingApi;
import com.example.prm_noodle_mobile.data.model.Order;
import com.example.prm_noodle_mobile.data.model.OrderCombo;
import com.example.prm_noodle_mobile.data.model.OrderItem;
import com.example.prm_noodle_mobile.data.model.Combo;
import com.example.prm_noodle_mobile.data.model.Product;
import com.example.prm_noodle_mobile.data.model.Topping;
import com.example.prm_noodle_mobile.customer.combo.ComboListAdapter;
import com.example.prm_noodle_mobile.customer.cart.CartManager;
import com.example.prm_noodle_mobile.customer.orderconfirm.OrderProductAdapter;
import com.example.prm_noodle_mobile.utils.UserSessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderConfirmFragment extends Fragment {

    private EditText editDeliveryAddress, editNotes;
    private RadioGroup radioPaymentMethod;
    private RecyclerView recyclerOrderProducts, recyclerOrderCombos;
    private Button confirmOrderButton;

    private List<OrderItem> orderItems = new ArrayList<>();
    private List<OrderCombo> orderCombos = new ArrayList<>();
    private List<Combo> comboList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private List<Topping> toppingList = new ArrayList<>();

    private OrderProductAdapter productAdapter;
    private ComboListAdapter comboListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_confirm, container, false);

        editDeliveryAddress = view.findViewById(R.id.edit_delivery_address);
        editNotes = view.findViewById(R.id.edit_notes);
        radioPaymentMethod = view.findViewById(R.id.radio_payment_method);
        recyclerOrderProducts = view.findViewById(R.id.recycler_order_products);
        recyclerOrderCombos = view.findViewById(R.id.recycler_order_combos);
        confirmOrderButton = view.findViewById(R.id.confirm_order_button);

        // Load products from cart
        orderItems = new ArrayList<>(CartManager.getInstance().getOrderItems());
        recyclerOrderProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new OrderProductAdapter(orderItems, productList, toppingList);
        recyclerOrderProducts.setAdapter(productAdapter);

        // Load combos
        recyclerOrderCombos.setLayoutManager(new LinearLayoutManager(getContext()));
        comboListAdapter = new ComboListAdapter(comboList);
        recyclerOrderCombos.setAdapter(comboListAdapter);

        loadProducts();
        loadToppings();
        loadCombos();

        confirmOrderButton.setOnClickListener(v -> confirmOrder());

        return view;
    }

    private void loadProducts() {
        ProductApi api = ApiClient.getClient(getContext()).create(ProductApi.class);
        api.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    if (productAdapter != null) productAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // Optionally show error
            }
        });
    }

    private void loadToppings() {
        ToppingApi api = ApiClient.getClient(getContext()).create(ToppingApi.class);
        api.getAvailableToppings().enqueue(new Callback<List<Topping>>() {
            @Override
            public void onResponse(Call<List<Topping>> call, Response<List<Topping>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    toppingList.clear();
                    toppingList.addAll(response.body());
                    if (productAdapter != null) productAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Topping>> call, Throwable t) {
                // Optionally show error
            }
        });
    }

    private void loadCombos() {
        ComboApi api = ApiClient.getClient(getContext()).create(ComboApi.class);
        api.getAvailableCombos().enqueue(new Callback<List<Combo>>() {
            @Override
            public void onResponse(Call<List<Combo>> call, Response<List<Combo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    comboList.clear();
                    comboList.addAll(response.body());
                    comboListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không lấy được combo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Combo>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối khi lấy combo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmOrder() {
        int userId = getUserId();

        // Lấy các combo user đã chọn, quantity mặc định = 1
        List<Combo> selectedCombos = comboListAdapter.getSelectedCombos();
        orderCombos = selectedCombos.stream()
                .map(combo -> new OrderCombo(combo.getComboId(), 1))
                .collect(Collectors.toList());

        if (orderItems.isEmpty() && orderCombos.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn ít nhất 1 sản phẩm hoặc combo!", Toast.LENGTH_SHORT).show();
            return;
        }

        int checkedId = radioPaymentMethod.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(getContext(), "Vui lòng chọn phương thức thanh toán!", Toast.LENGTH_SHORT).show();
            return;
        }

        String address = editDeliveryAddress.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập địa chỉ giao hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        String notes = editNotes.getText().toString().trim();
        String paymentMethod = getPaymentMethodString(checkedId);

        // Tạo order với cấu trúc đúng như JSON yêu cầu
        Order order = new Order(userId, address, notes, paymentMethod, orderItems, orderCombos);

        OrderApi api = ApiClient.getClient(getContext()).create(OrderApi.class);
        api.createOrder(order).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Order thành công!", Toast.LENGTH_LONG).show();
                    CartManager.getInstance().clearCart();
                    // Navigate back to home or show success screen
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Đặt hàng thất bại, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getPaymentMethodString(int radioButtonId) {
        if (radioButtonId == R.id.radio_cash) {
            return "cash";
        } else if (radioButtonId == R.id.radio_digital_wallet) {
            return "digital_wallet";
        } else if (radioButtonId == R.id.radio_bank_transfer) {
            return "bank_transfer";
        }
        return "cash"; // default
    }

    private int getUserId() {
        UserSessionManager sessionManager = new UserSessionManager(getContext());
        return sessionManager.getUserId();
    }
}
