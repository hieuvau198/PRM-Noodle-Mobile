package com.example.prm_noodle_mobile.customer.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.api.ApiClient;
import com.example.prm_noodle_mobile.data.api.ProductApi;
import com.example.prm_noodle_mobile.data.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_products, container, false);
        recyclerView = view.findViewById(R.id.recycler_all_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductAdapter();
        recyclerView.setAdapter(adapter);
        loadProducts();
        return view;
    }

    private void loadProducts() {
        ProductApi api = ApiClient.getClient().create(ProductApi.class);
        api.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setProductList(response.body()); // Đổ toàn bộ danh sách vào adapter
                } else {
                    Toast.makeText(getContext(), "Không lấy được sản phẩm!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
