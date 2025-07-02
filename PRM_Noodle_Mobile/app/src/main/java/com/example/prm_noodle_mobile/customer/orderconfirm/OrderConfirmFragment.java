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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.api.ApiClient;
import com.example.prm_noodle_mobile.data.api.OrderApi;
import com.example.prm_noodle_mobile.data.api.ToppingApi;
import com.example.prm_noodle_mobile.data.api.ProductApi;
import com.example.prm_noodle_mobile.data.api.ComboApi;
import com.example.prm_noodle_mobile.data.model.Order;
import com.example.prm_noodle_mobile.data.model.OrderCombo;
import com.example.prm_noodle_mobile.data.model.OrderItem;
import com.example.prm_noodle_mobile.data.model.Topping;
import com.example.prm_noodle_mobile.data.model.ToppingOrder;
import com.example.prm_noodle_mobile.data.model.Product;
import com.example.prm_noodle_mobile.data.model.Combo;
import com.example.prm_noodle_mobile.customer.orderconfirm.OrderProductAdapter;
import com.example.prm_noodle_mobile.customer.combo.ComboListAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.prm_noodle_mobile.customer.orderconfirm.OrderProductAdapter.OnAddToCartListener;
import com.example.prm_noodle_mobile.customer.cart.CartManager;

public class OrderConfirmFragment extends Fragment {
    private EditText editDeliveryAddress, editNotes;
    private RadioGroup radioPaymentMethod;
    private RecyclerView recyclerOrderProducts, recyclerOrderCombos;
    private Button confirmOrderButton;
    private List<Topping> toppingList = new ArrayList<>();
    private List<Topping> selectedToppings = new ArrayList<>();
    private List<OrderItem> orderItems = new ArrayList<>();
    private List<OrderCombo> orderCombos = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private List<Combo> comboList = new ArrayList<>();
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
        // Không show topping list nữa
        // recyclerToppings = view.findViewById(R.id.recycler_toppings);
        // toppingAdapter = new ToppingAdapter(toppingList, ...);
        // recyclerToppings.setAdapter(toppingAdapter);
        // Lấy orderItems từ CartManager
        orderItems = new ArrayList<>(com.example.prm_noodle_mobile.customer.cart.CartManager.getInstance().getOrderItems());
        recyclerOrderProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new com.example.prm_noodle_mobile.customer.orderconfirm.OrderProductAdapter(orderItems, productList);
        recyclerOrderProducts.setAdapter(productAdapter);
        recyclerOrderCombos.setLayoutManager(new LinearLayoutManager(getContext()));
        comboListAdapter = new com.example.prm_noodle_mobile.customer.combo.ComboListAdapter(comboList);
        recyclerOrderCombos.setAdapter(comboListAdapter);
        loadCombos();
        loadToppings();
        confirmOrderButton.setOnClickListener(v -> confirmOrder());
        return view;
    }

    private void loadToppings() {
        ToppingApi api = ApiClient.getClient().create(ToppingApi.class);
        api.getAvailableToppings().enqueue(new Callback<List<Topping>>() {
            @Override
            public void onResponse(Call<List<Topping>> call, Response<List<Topping>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    toppingList.clear();
                    toppingList.addAll(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Topping>> call, Throwable t) {
                Toast.makeText(getContext(), "Không lấy được topping", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCombos() {
        ComboApi api = ApiClient.getClient().create(ComboApi.class);
        api.getAvailableCombos().enqueue(new Callback<List<Combo>>() {
            @Override
            public void onResponse(Call<List<Combo>> call, Response<List<Combo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    comboList.clear();
                    comboList.addAll(response.body());
                    comboListAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Combo>> call, Throwable t) {
                Toast.makeText(getContext(), "Không lấy được combo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmOrder() {
        int userId = getUserId();
        if (orderItems == null || orderItems.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn ít nhất 1 sản phẩm!", Toast.LENGTH_SHORT).show();
            return;
        }
        int checkedId = radioPaymentMethod.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(getContext(), "Vui lòng chọn phương thức thanh toán!", Toast.LENGTH_SHORT).show();
            return;
        }
        String address = editDeliveryAddress.getText().toString();
        String notes = editNotes.getText().toString();
        String paymentMethod = ((RadioButton) getView().findViewById(checkedId)).getText().toString();
        Order order = new Order(userId, address, notes, paymentMethod, orderItems, orderCombos);
        OrderApi api = ApiClient.getClient().create(OrderApi.class);
        api.createOrder(order).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đặt hàng thành công! Cảm ơn bạn đã mua hàng.", Toast.LENGTH_LONG).show();
                    // Sau khi đặt hàng thành công, reload lại trang OrderConfirm
                    requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new OrderConfirmFragment())
                        .commit();
                } else {
                    Toast.makeText(getContext(), "Đặt hàng thất bại! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getUserId() {
        // Lấy userId từ SharedPreferences hoặc Bundle
        // Ví dụ lấy từ SharedPreferences
        android.content.SharedPreferences prefs = getContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
        return prefs.getInt("user_id", 1); // fallback 1 nếu chưa đăng nhập
    }
}