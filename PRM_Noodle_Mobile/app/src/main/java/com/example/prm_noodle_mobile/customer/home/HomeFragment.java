// FILE: app/src/main/java/com/example/prm_noodle_mobile/customer/home/HomeFragment.java
package com.example.prm_noodle_mobile.customer.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.Product;
import java.util.List;

public class HomeFragment extends Fragment implements HomeContract.View {

    private HomeContract.Presenter presenter;
    private RecyclerView featuredRecyclerView;
    private FeaturedProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        presenter = new HomePresenter(this);
        presenter.loadFeaturedProducts();

        return view;
    }

    private void initViews(View view) {
        featuredRecyclerView = view.findViewById(R.id.rv_featured_products);
        featuredRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        adapter = new FeaturedProductAdapter();
        featuredRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showFeaturedProducts(List<Product> products) {
        adapter.updateProducts(products);
    }

    @Override
    public void showLoading() {
        // TODO: Show loading indicator
    }

    @Override
    public void hideLoading() {
        // TODO: Hide loading indicator
    }

    @Override
    public void showError(String message) {
        // TODO: Show error message
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}
