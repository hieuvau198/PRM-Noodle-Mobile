package com.example.prm_noodle_mobile.customer.productdetail;

import com.example.prm_noodle_mobile.data.mock.MockProductData;
import com.example.prm_noodle_mobile.data.model.Product;
import java.util.List;

public class ProductDetailPresenter implements ProductDetailContract.Presenter {

    private ProductDetailContract.View view;

    public ProductDetailPresenter(ProductDetailContract.View view) {
        this.view = view;
    }

    @Override
    public void loadProductDetails(int productId) {
        if (view != null) {
            view.showLoading();

            List<Product> allProducts = MockProductData.getMockProducts();
            Product product = allProducts.stream()
                    .filter(p -> p.getProductId() == productId)
                    .findFirst()
                    .orElse(null);

            if (product != null) {
                view.hideLoading();
                view.showProductDetails(product);
            } else {
                view.hideLoading();
                view.showError("Product not found");
            }
        }
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}