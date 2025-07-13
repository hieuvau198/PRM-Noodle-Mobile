package com.example.prm_v3.ui.create;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_v3.api.ApiClient;
import com.example.prm_v3.api.ApiService;
import com.example.prm_v3.api.CreateOrderRequest;
import com.example.prm_v3.model.Order;
import com.example.prm_v3.model.Product;
import com.example.prm_v3.model.Combo;
import com.example.prm_v3.model.Topping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrderViewModel extends ViewModel {
    private static final String TAG = "CreateOrderViewModel";

    private ApiService apiService;

    // LiveData for UI state
    private MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private MutableLiveData<List<Combo>> combos = new MutableLiveData<>();
    private MutableLiveData<List<Topping>> toppings = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<String> message = new MutableLiveData<>();
    private MutableLiveData<Order> createdOrder = new MutableLiveData<>();

    // Cart data
    private Map<Integer, Integer> productQuantities = new HashMap<>();
    private Map<Integer, Integer> comboQuantities = new HashMap<>();
    private Map<Integer, Boolean> selectedToppings = new HashMap<>();
    private MutableLiveData<Double> totalAmount = new MutableLiveData<>();
    private MutableLiveData<Integer> totalItems = new MutableLiveData<>();

    public CreateOrderViewModel() {
        apiService = ApiClient.getApiService();
        totalAmount.setValue(0.0);
        totalItems.setValue(0);
        Log.d(TAG, "CreateOrderViewModel initialized");
    }

    // Getters for LiveData
    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<List<Combo>> getCombos() { return combos; }
    public LiveData<List<Topping>> getToppings() { return toppings; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<String> getMessage() { return message; }
    public LiveData<Order> getCreatedOrder() { return createdOrder; }
    public LiveData<Double> getTotalAmount() { return totalAmount; }
    public LiveData<Integer> getTotalItems() { return totalItems; }

    // Load initial data
    public void loadInitialData() {
        Log.d(TAG, "Loading initial data...");
        loadProducts();
        loadCombos();
        loadToppings();
    }

    public void loadProducts() {
        Log.d(TAG, "Loading products - trying multiple endpoints...");
        loading.setValue(true);
        error.setValue(null);

        // Try endpoint 1: api/product (lowercase)
        tryProductEndpoint1();
    }

    private void tryProductEndpoint1() {
        Log.d(TAG, "Trying: /api/product");
        Call<List<Product>> call = apiService.getProducts(); // api/product
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                Log.d(TAG, "api/product response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "SUCCESS: Products loaded from api/product: " + response.body().size());
                    products.setValue(response.body());
                    loading.setValue(false);
                } else {
                    Log.d(TAG, "Failed api/product, trying api/products");
                    tryProductEndpoint2();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "api/product failed", t);
                tryProductEndpoint2();
            }
        });
    }

    private void tryProductEndpoint2() {
        Log.d(TAG, "Trying: /api/products");
        Call<List<Product>> call = apiService.getProductsList(); // api/products
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                Log.d(TAG, "api/products response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "SUCCESS: Products loaded from api/products: " + response.body().size());
                    products.setValue(response.body());
                    loading.setValue(false);
                } else {
                    Log.d(TAG, "Failed api/products, creating mock data");
                    createMockProducts();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "api/products failed", t);
                createMockProducts();
            }
        });
    }

    private void createMockProducts() {
        Log.d(TAG, "Creating mock products for demo");
        List<Product> mockProducts = new ArrayList<>();

        Product p1 = new Product();
        p1.setProductId(1);
        p1.setProductName("Mì cay Bussan Đặc biệt");
        p1.setDescription("Mì cay đặc biệt với gia vị Hàn Quốc");
        p1.setBasePrice(89000);
        p1.setSpiceLevel("hot");
        p1.setAvailable(true);
        mockProducts.add(p1);

        Product p2 = new Product();
        p2.setProductId(2);
        p2.setProductName("Mì cay Hàn Quốc");
        p2.setDescription("Mì cay truyền thống");
        p2.setBasePrice(75000);
        p2.setSpiceLevel("medium");
        p2.setAvailable(true);
        mockProducts.add(p2);

        products.setValue(mockProducts);
        loading.setValue(false);
        message.setValue("Đang sử dụng dữ liệu demo - API chưa sẵn sàng");
    }

    public void loadCombos() {
        Log.d(TAG, "Loading combos - trying multiple endpoints...");
        tryComboEndpoint1();
    }

    private void tryComboEndpoint1() {
        Log.d(TAG, "Trying: /api/combo");
        Call<List<Combo>> call = apiService.getCombos(); // api/combo
        call.enqueue(new Callback<List<Combo>>() {
            @Override
            public void onResponse(Call<List<Combo>> call, Response<List<Combo>> response) {
                Log.d(TAG, "api/combo response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "SUCCESS: Combos loaded from api/combo: " + response.body().size());
                    combos.setValue(response.body());
                } else {
                    Log.d(TAG, "Failed api/combo, trying api/combos");
                    tryComboEndpoint2();
                }
            }

            @Override
            public void onFailure(Call<List<Combo>> call, Throwable t) {
                Log.e(TAG, "api/combo failed", t);
                tryComboEndpoint2();
            }
        });
    }

    private void tryComboEndpoint2() {
        Log.d(TAG, "Trying: /api/combos");
        Call<List<Combo>> call = apiService.getCombosList(); // api/combos
        call.enqueue(new Callback<List<Combo>>() {
            @Override
            public void onResponse(Call<List<Combo>> call, Response<List<Combo>> response) {
                Log.d(TAG, "api/combos response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "SUCCESS: Combos loaded from api/combos: " + response.body().size());
                    combos.setValue(response.body());
                } else {
                    Log.d(TAG, "Failed api/combos, creating mock data");
                    createMockCombos();
                }
            }

            @Override
            public void onFailure(Call<List<Combo>> call, Throwable t) {
                Log.e(TAG, "api/combos failed", t);
                createMockCombos();
            }
        });
    }

    private void createMockCombos() {
        Log.d(TAG, "Creating mock combos for demo");
        List<Combo> mockCombos = new ArrayList<>();

        Combo c1 = new Combo();
        c1.setComboId(1);
        c1.setComboName("Combo Bussan Đặc Biệt");
        c1.setDescription("Mì cay Bussan + Coca Cola + Bánh ngọt");
        c1.setComboPrice(95000);
        c1.setAvailable(true);
        mockCombos.add(c1);

        Combo c2 = new Combo();
        c2.setComboId(2);
        c2.setComboName("Combo Sinh Viên");
        c2.setDescription("Mì cay + Nước suối");
        c2.setComboPrice(65000);
        c2.setAvailable(true);
        mockCombos.add(c2);

        combos.setValue(mockCombos);
    }

    public void loadToppings() {
        Log.d(TAG, "Loading toppings - trying multiple endpoints...");
        tryToppingEndpoint1();
    }

    private void tryToppingEndpoint1() {
        Log.d(TAG, "Trying: /api/topping");
        Call<List<Topping>> call = apiService.getToppings(); // api/topping
        call.enqueue(new Callback<List<Topping>>() {
            @Override
            public void onResponse(Call<List<Topping>> call, Response<List<Topping>> response) {
                Log.d(TAG, "api/topping response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "SUCCESS: Toppings loaded from api/topping: " + response.body().size());
                    toppings.setValue(response.body());
                } else {
                    Log.d(TAG, "Failed api/topping, trying api/toppings");
                    tryToppingEndpoint2();
                }
            }

            @Override
            public void onFailure(Call<List<Topping>> call, Throwable t) {
                Log.e(TAG, "api/topping failed", t);
                tryToppingEndpoint2();
            }
        });
    }

    private void tryToppingEndpoint2() {
        Log.d(TAG, "Trying: /api/toppings");
        Call<List<Topping>> call = apiService.getToppingsList(); // api/toppings
        call.enqueue(new Callback<List<Topping>>() {
            @Override
            public void onResponse(Call<List<Topping>> call, Response<List<Topping>> response) {
                Log.d(TAG, "api/toppings response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "SUCCESS: Toppings loaded from api/toppings: " + response.body().size());
                    toppings.setValue(response.body());
                } else {
                    Log.d(TAG, "Failed api/toppings, creating mock data");
                    createMockToppings();
                }
            }

            @Override
            public void onFailure(Call<List<Topping>> call, Throwable t) {
                Log.e(TAG, "api/toppings failed", t);
                createMockToppings();
            }
        });
    }

    private void createMockToppings() {
        Log.d(TAG, "Creating mock toppings for demo");
        List<Topping> mockToppings = new ArrayList<>();

        Topping t1 = new Topping();
        t1.setToppingId(1);
        t1.setToppingName("Thêm trứng");
        t1.setPrice(15000);
        t1.setAvailable(true);
        mockToppings.add(t1);

        Topping t2 = new Topping();
        t2.setToppingId(2);
        t2.setToppingName("Thêm phô mai");
        t2.setPrice(20000);
        t2.setAvailable(true);
        mockToppings.add(t2);

        Topping t3 = new Topping();
        t3.setToppingId(3);
        t3.setToppingName("Thêm xúc xích");
        t3.setPrice(25000);
        t3.setAvailable(true);
        mockToppings.add(t3);

        toppings.setValue(mockToppings);
    }

    // Rest of the methods remain the same...
    public void updateProductQuantity(int productId, int quantity) {
        if (quantity <= 0) {
            productQuantities.remove(productId);
        } else {
            productQuantities.put(productId, quantity);
        }
        calculateTotal();
    }

    public void updateComboQuantity(int comboId, int quantity) {
        if (quantity <= 0) {
            comboQuantities.remove(comboId);
        } else {
            comboQuantities.put(comboId, quantity);
        }
        calculateTotal();
    }

    public void toggleTopping(int toppingId, boolean selected) {
        if (selected) {
            selectedToppings.put(toppingId, true);
        } else {
            selectedToppings.remove(toppingId);
        }
        calculateTotal();
    }

    public int getProductQuantity(int productId) {
        return productQuantities.getOrDefault(productId, 0);
    }

    public int getComboQuantity(int comboId) {
        return comboQuantities.getOrDefault(comboId, 0);
    }

    public boolean isToppingSelected(int toppingId) {
        return selectedToppings.getOrDefault(toppingId, false);
    }

    private void calculateTotal() {
        double total = 0.0;
        int items = 0;

        List<Product> productList = products.getValue();
        List<Combo> comboList = combos.getValue();
        List<Topping> toppingList = toppings.getValue();

        // Calculate products
        if (productList != null) {
            for (Product product : productList) {
                int quantity = getProductQuantity(product.getProductId());
                if (quantity > 0) {
                    total += product.getBasePrice() * quantity;
                    items += quantity;
                }
            }
        }

        // Calculate combos
        if (comboList != null) {
            for (Combo combo : comboList) {
                int quantity = getComboQuantity(combo.getComboId());
                if (quantity > 0) {
                    total += combo.getComboPrice() * quantity;
                    items += quantity;
                }
            }
        }

        // Calculate toppings
        if (toppingList != null && items > 0) {
            for (Topping topping : toppingList) {
                if (isToppingSelected(topping.getToppingId())) {
                    total += topping.getPrice() * items;
                }
            }
        }

        totalAmount.setValue(total);
        totalItems.setValue(items);
    }

    public void createOrder(int userId, String deliveryAddress, String notes, String paymentMethod) {
        if (totalItems.getValue() == null || totalItems.getValue() == 0) {
            message.setValue("Vui lòng chọn ít nhất một món");
            return;
        }

        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            message.setValue("Vui lòng nhập địa chỉ giao hàng");
            return;
        }

        Log.d(TAG, "Creating order...");
        loading.setValue(true);
        error.setValue(null);

        CreateOrderRequest request = buildOrderRequest(userId, deliveryAddress, notes, paymentMethod);
        Log.d(TAG, "Order request built with " + request.getOrderItems().size() + " items and " + request.getOrderCombos().size() + " combos");

        Call<Order> call = apiService.createOrder(request);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                Log.d(TAG, "Create order response code: " + response.code());
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Order created successfully");
                    createdOrder.setValue(response.body());
                    message.setValue("Đặt hàng thành công!");
                    clearCart();
                } else {
                    Log.e(TAG, "Create order failed. Code: " + response.code());
                    String errorMsg = "Đặt hàng thất bại - Code: " + response.code();
                    error.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e(TAG, "Create order failed", t);
                loading.setValue(false);
                error.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private CreateOrderRequest buildOrderRequest(int userId, String deliveryAddress, String notes, String paymentMethod) {
        CreateOrderRequest request = new CreateOrderRequest(userId, deliveryAddress, notes, paymentMethod);

        // Add order items
        List<CreateOrderRequest.CreateOrderItem> orderItems = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
            CreateOrderRequest.CreateOrderItem item = new CreateOrderRequest.CreateOrderItem(
                    entry.getKey(), entry.getValue());

            // Add toppings to this item
            List<CreateOrderRequest.CreateOrderItemTopping> itemToppings = new ArrayList<>();
            for (Map.Entry<Integer, Boolean> toppingEntry : selectedToppings.entrySet()) {
                if (toppingEntry.getValue()) {
                    itemToppings.add(new CreateOrderRequest.CreateOrderItemTopping(
                            toppingEntry.getKey(), 1));
                }
            }
            item.setToppings(itemToppings);
            orderItems.add(item);
        }
        request.setOrderItems(orderItems);

        // Add order combos
        List<CreateOrderRequest.CreateOrderCombo> orderCombos = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : comboQuantities.entrySet()) {
            orderCombos.add(new CreateOrderRequest.CreateOrderCombo(
                    entry.getKey(), entry.getValue()));
        }
        request.setOrderCombos(orderCombos);

        return request;
    }

    public void clearCart() {
        productQuantities.clear();
        comboQuantities.clear();
        selectedToppings.clear();
        calculateTotal();

        // Force update LiveData để trigger UI refresh
        totalAmount.setValue(0.0);
        totalItems.setValue(0);
    }

    public String getFormattedTotal() {
        Double total = totalAmount.getValue();
        if (total == null) return "0₫";
        return String.format("%.0f₫", total);
    }

    public String getCartSummary() {
        Integer items = totalItems.getValue();
        if (items == null || items == 0) return "Chưa có món nào";
        return String.format("(%d món)", items);
    }
    public void resetCreatedOrder() {
        createdOrder.setValue(null);
    }
}