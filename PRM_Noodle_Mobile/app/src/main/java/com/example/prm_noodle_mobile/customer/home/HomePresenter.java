// FILE: app/src/main/java/com/example/prm_noodle_mobile/customer/home/HomePresenter.java
package com.example.prm_noodle_mobile.customer.home;

import com.example.prm_noodle_mobile.data.mock.MockProductData;
import com.example.prm_noodle_mobile.data.model.Product;
import java.util.List;

public class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View view;

    public HomePresenter(HomeContract.View view) {
        this.view = view;
    }

    @Override
    public void loadFeaturedProducts() {
        if (view != null) {
            view.showLoading();

            // Get featured products (first 3 items)
            List<Product> allProducts = MockProductData.getMockProducts();
            List<Product> featuredProducts = allProducts.subList(0, Math.min(3, allProducts.size()));

            view.hideLoading();
            view.showFeaturedProducts(featuredProducts);
        }
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}