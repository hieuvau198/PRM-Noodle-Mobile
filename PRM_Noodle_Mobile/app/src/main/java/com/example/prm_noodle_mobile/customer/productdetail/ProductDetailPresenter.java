package com.example.prm_noodle_mobile.customer.productdetail;

import com.example.prm_noodle_mobile.data.api.ApiClient;
import com.example.prm_noodle_mobile.data.api.ProductApi;
import com.example.prm_noodle_mobile.data.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailPresenter implements ProductDetailContract.Presenter {

    private ProductDetailContract.View view;

    public ProductDetailPresenter(ProductDetailContract.View view) {
        this.view = view;
    }

    @Override
    public void loadProductDetails(int productId) {
        view.showLoading();

        ProductApi productApi = ApiClient.getClient(((android.content.Context)view)).create(ProductApi.class);
        Call<List<Product>> call = productApi.getProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    Product product = null;
                    for (Product p : response.body()) {
                        if (p.getProductId() == productId) {
                            product = p;
                            break;
                        }
                    }
                    if (product != null) {
                        view.showProductDetails(product);
                    } else {
                        view.showError("Không tìm thấy sản phẩm");
                    }
                } else {
                    view.showError("Không lấy được dữ liệu sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                view.hideLoading();
                view.showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}