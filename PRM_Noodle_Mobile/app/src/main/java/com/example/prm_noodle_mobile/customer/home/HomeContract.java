// FILE: app/src/main/java/com/example/prm_noodle_mobile/customer/home/HomeContract.java
package com.example.prm_noodle_mobile.customer.home;

import com.example.prm_noodle_mobile.data.model.Product;
import java.util.List;

public interface HomeContract {

    interface View {
        void showFeaturedProducts(List<Product> products);
        void showLoading();
        void hideLoading();
        void showError(String message);
    }

    interface Presenter {
        void loadFeaturedProducts();
        void onDestroy();
    }
}