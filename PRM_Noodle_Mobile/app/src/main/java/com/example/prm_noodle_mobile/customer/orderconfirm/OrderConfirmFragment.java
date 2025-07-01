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
import com.example.prm_noodle_mobile.data.api.ToppingApi;
import com.example.prm_noodle_mobile.data.model.Order;
import com.example.prm_noodle_mobile.data.model.OrderCombo;
import com.example.prm_noodle_mobile.data.model.OrderItem;
import com.example.prm_noodle_mobile.data.model.Topping;
import com.example.prm_noodle_mobile.data.model.ToppingOrder;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderConfirmFragment extends Fragment {
    private EditText editDeliveryAddress, editNotes;
    private RadioGroup radioPaymentMethod;
    private RecyclerView recyclerOrderProducts, recyclerOrderCombos, recyclerToppings;
    private Button confirmOrderButton;
    private ToppingAdapter toppingAdapter;
    private List<Topping> toppingList = new ArrayList<>();
    private List<Topping> selectedToppings = new ArrayList<>();
    private List<OrderItem> orderItems = new ArrayList<>();
    private List<OrderCombo> orderCombos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_confirm, container, false);
        editDeliveryAddress = view.findViewById(R.id.edit_delivery_address);
        editNotes = view.findViewById(R.id.edit_notes);
        radioPaymentMethod = view.findViewById(R.id.radio_payment_method);
        recyclerOrderProducts = view.findViewById(R.id.recycler_order_products);
        recyclerOrderCombos = view.findViewById(R.id.recycler_order_combos);
        recyclerToppings = view.findViewById(R.id.recycler_toppings);
        confirmOrderButton = view.findViewById(R.id.confirm_order_button);

        recyclerToppings.setLayoutManager(new LinearLayoutManager(getContext()));
        toppingAdapter = new ToppingAdapter(toppingList, (topping, checked) -> {
            if (checked) selectedToppings.add(topping);
            else selectedToppings.remove(topping);
        });
        recyclerToppings.setAdapter(toppingAdapter);

        // TODO: Load orderItems, orderCombos từ cart hoặc intent
        recyclerOrderProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerOrderProducts.setAdapter(new OrderProductAdapter(orderItems));
        recyclerOrderCombos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerOrderCombos.setAdapter(new OrderComboAdapter(orderCombos));

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
                    toppingAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Topping>> call, Throwable t) {
                Toast.makeText(getContext(), "Không lấy được topping", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmOrder() {
        int userId = 1; // TODO: Lấy userId thực tế
        String address = editDeliveryAddress.getText().toString();
        String notes = editNotes.getText().toString();
        String paymentMethod = ((RadioButton) getView().findViewById(radioPaymentMethod.getCheckedRadioButtonId())).getText().toString();
        // TODO: Gán topping vào orderItems nếu cần
        Order order = new Order(userId, address, notes, paymentMethod, orderItems, orderCombos);
        OrderApi api = ApiClient.getClient().create(OrderApi.class);
        api.createOrder(order).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}