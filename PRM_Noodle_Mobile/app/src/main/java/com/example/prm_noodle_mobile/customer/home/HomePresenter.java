// FILE: app/src/main/java/com/example/prm_noodle_mobile/customer/home/HomePresenter.java
package com.example.prm_noodle_mobile.customer.home;

import com.example.prm_noodle_mobile.data.api.ApiClient;
import com.example.prm_noodle_mobile.data.api.ProductApi;
import com.example.prm_noodle_mobile.data.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View view;

    public HomePresenter(HomeContract.View view) {
        this.view = view;
    }

    @Override
    public void loadFeaturedProducts() {
        if (view != null) {
            view.showLoading();

            ProductApi productApi = ApiClient.getClient().create(ProductApi.class);
            Call<List<Product>> call = productApi.getProducts();
            call.enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    view.hideLoading();
                    if (response.isSuccessful() && response.body() != null) {
                        List<Product> allProducts = response.body();
                        List<Product> featuredProducts = allProducts.subList(0, Math.min(10, allProducts.size()));
                        view.showFeaturedProducts(featuredProducts);
                    } else {
                        view.showFeaturedProducts(new java.util.ArrayList<>());
                    }
                }

                @Override
                public void onFailure(Call<List<Product>> call, Throwable t) {
                    view.hideLoading();
                    view.showFeaturedProducts(new java.util.ArrayList<>());
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}