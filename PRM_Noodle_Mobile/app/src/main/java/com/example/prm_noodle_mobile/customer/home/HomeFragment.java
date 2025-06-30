package com.example.prm_noodle_mobile.customer.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements HomeContract.View {

    private RecyclerView categoryRecycler, bestSellerRecycler;
    private BestSellerAdapter bestSellerAdapter;
    private HomePresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Danh mục
        categoryRecycler = view.findViewById(R.id.recycler_view_categories);
        categoryRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecycler.setAdapter(new CategoryAdapter(getCategories()));

        // Sản phẩm bán chạy
        bestSellerRecycler = view.findViewById(R.id.recycler_view_best_sellers);
        bestSellerRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        bestSellerAdapter = new BestSellerAdapter(new ArrayList<>());
        bestSellerRecycler.setAdapter(bestSellerAdapter);

        presenter = new HomePresenter(this);
        presenter.loadFeaturedProducts();

        return view;
    }

    @Override
    public void showLoading() {
        // Có thể thêm progress bar nếu muốn
    }

    @Override
    public void hideLoading() {
        // Ẩn progress bar nếu có
    }

    @Override
    public void showFeaturedProducts(List<Product> products) {
        bestSellerAdapter.setProducts(products);
        bestSellerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
        // Hiển thị lỗi nếu cần
    }

    // Danh mục mẫu (bạn có thể thay icon khác nếu muốn)
    private List<CategoryAdapter.Category> getCategories() {
        List<CategoryAdapter.Category> list = new ArrayList<>();
        list.add(new CategoryAdapter.Category("Mì Kim Chi", R.drawable.mi_kim_chi));
        list.add(new CategoryAdapter.Category("Mì Lẩu Thái", R.drawable.mi_lau_thai));
        list.add(new CategoryAdapter.Category("Lẩu", R.drawable.lau_kim_chi));
        list.add(new CategoryAdapter.Category("Món Trộn", R.drawable.mi_tron));
        list.add(new CategoryAdapter.Category("Tokbokki", R.drawable.tokbokki));
        list.add(new CategoryAdapter.Category("Món Gà", R.drawable.dui_ga));
        list.add(new CategoryAdapter.Category("Bánh", R.drawable.banh_takoyaki));
        list.add(new CategoryAdapter.Category("Nước", R.drawable.ic_tra_chanh));
        return list;
    }
}