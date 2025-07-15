package com.example.prm_noodle_mobile.customer.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import android.content.SharedPreferences;
import android.content.Intent;
import android.widget.Toast;

import com.example.prm_noodle_mobile.adapter.BannerAdapter;
import com.example.prm_noodle_mobile.model.Banner;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.app.AlertDialog;
import android.widget.EditText;
import android.os.AsyncTask;
import org.json.JSONObject;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.Product;
import com.example.prm_noodle_mobile.auth.LoginActivity;
import com.example.prm_noodle_mobile.data.api.ChatbotApi;
import com.example.prm_noodle_mobile.data.api.ApiClient;
import com.example.prm_noodle_mobile.data.model.ChatMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import android.app.Dialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import java.util.UUID;
import android.os.Handler;

public class HomeFragment extends Fragment implements HomeContract.View {

    private RecyclerView categoryRecycler, bestSellerRecycler;
    private BestSellerAdapter bestSellerAdapter;
    private HomePresenter presenter;
    private TextView tvUsernameHome;
    private ImageButton btnLogoutHome;
    private FloatingActionButton fabChatbot;

    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            ViewPager2 viewPager = getView().findViewById(R.id.view_pager_banners);
            if (viewPager != null) {
                if (viewPager.getCurrentItem() == bannerAdapter.getItemCount() - 1) {
                    viewPager.setCurrentItem(0, true); // true để có animation
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                }
            }
            sliderHandler.postDelayed(this, 3000); // Tự động gọi lại sau 3 giây
        }
    };

    private BannerAdapter bannerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Header user
        tvUsernameHome = view.findViewById(R.id.tv_username_home);
        btnLogoutHome = view.findViewById(R.id.btn_logout_home);
        // Lấy tên user từ SharedPreferences nếu có
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", getContext().MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", "User");
        tvUsernameHome.setText("Hi, " + userEmail);
        // Xử lý logout
        btnLogoutHome.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Toast.makeText(getContext(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finishAffinity();
        });

        // Danh mục
        categoryRecycler = view.findViewById(R.id.recycler_view_categories);
        categoryRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecycler.setAdapter(new CategoryAdapter(getCategories()));

        // Sản phẩm bán chạy
        bestSellerRecycler = view.findViewById(R.id.recycler_view_best_sellers);
        bestSellerRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        bestSellerAdapter = new BestSellerAdapter(new ArrayList<>());
        bestSellerRecycler.setAdapter(bestSellerAdapter);

        presenter = new HomePresenter(this, getContext());
        presenter.loadFeaturedProducts();

        fabChatbot = view.findViewById(R.id.fab_chatbot);
        fabChatbot.setOnClickListener(v -> showChatbotDialog());

        // Banner
        ViewPager2 viewPager = view.findViewById(R.id.view_pager_banners);
        List<Banner> banners = new ArrayList<>();
        banners.add(new Banner(R.drawable.banner_1, "Khám phá ẩm thực Hàn Quốc\nMì Cay Seoul"));
        banners.add(new Banner(R.drawable.banner_2, "Đặt hàng ngay\nGiảm 20% cho đơn từ 200K"));
        banners.add(new Banner(R.drawable.banner_3, "Combo mì cay 2 người\nChỉ từ 150K"));
        banners.add(new Banner(R.drawable.banner_4, "Topping đa dạng\nThêm vào mì cay theo ý thích"));
        banners.add(new Banner(R.drawable.banner_5, "Ưu đãi đặc biệt mỗi thứ 6\nTặng nước miễn phí cho mọi đơn!"));

        bannerAdapter = new BannerAdapter(banners);
        viewPager.setAdapter(bannerAdapter);
        
        // Set page transformer for smooth transition
        viewPager.setPageTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        // Enable infinite scrolling
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });

        // Start auto sliding
        sliderHandler.postDelayed(sliderRunnable, 3000);

        // Search
        EditText etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                performSearch(s.toString());
            }
        });

        // Thêm nút xóa text tìm kiếm khi focus
        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && etSearch.getText().toString().isEmpty()) {
                // Reset lại danh sách sản phẩm khi focus vào ô tìm kiếm
                if (presenter != null) {
                    presenter.loadFeaturedProducts();
                }
            }
        });

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

    private String getSessionId() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", getContext().MODE_PRIVATE);
        String email = sharedPreferences.getString("userEmail", null);
        if (email != null && !email.isEmpty()) return email;
        String uuid = sharedPreferences.getString("chatbotSessionId", null);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            sharedPreferences.edit().putString("chatbotSessionId", uuid).apply();
        }
        return uuid;
    }

    private void performSearch(String query) {
        if (presenter != null) {
            // Chuyển query về chữ thường để tìm kiếm không phân biệt hoa thường
            query = query.toLowerCase().trim();
            List<Product> allProducts = bestSellerAdapter.getProducts();
            List<Product> filteredProducts = new ArrayList<>();
            
            for (Product product : allProducts) {
                // Tìm theo tên hoặc mô tả sản phẩm
                if (product.getProductName().toLowerCase().contains(query) ||
                    (product.getDescription() != null && 
                     product.getDescription().toLowerCase().contains(query))) {
                    filteredProducts.add(product);
                }
            }
            
            // Cập nhật RecyclerView với kết quả tìm kiếm
            bestSellerAdapter.setProducts(filteredProducts);
            bestSellerAdapter.notifyDataSetChanged();
        }
    }

    private void showChatbotDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_chatbot);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        RecyclerView rv = dialog.findViewById(R.id.rv_chat_messages);
        EditText etInput = dialog.findViewById(R.id.et_chat_input);
        ImageButton btnSend = dialog.findViewById(R.id.btn_send_chat);
        ArrayList<ChatMessageLocal> chatList = new ArrayList<>();
        ChatbotAdapter adapter = new ChatbotAdapter(chatList);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        // Lời chào bot
        adapter.addMessage(new ChatMessageLocal("Xin chào! Tôi là trợ lý ảo của nhà hàng, chuyên giúp bạn chọn món.", true));
        btnSend.setOnClickListener(v -> {
            String msg = etInput.getText().toString().trim();
            if (msg.isEmpty()) return;
            adapter.addMessage(new ChatMessageLocal(msg, false));
            rv.scrollToPosition(adapter.getItemCount() - 1);
            etInput.setText("");
            // Gửi API
            ChatbotApi api = ApiClient.getClient(getContext()).create(ChatbotApi.class);
            String sessionId = getSessionId();
            ChatMessage chatMessage = new ChatMessage(msg, sessionId);
            api.sendMessage(chatMessage).enqueue(new retrofit2.Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                    String resp = "Không nhận được phản hồi từ chatbot";
                    if (response.isSuccessful() && response.body() != null) {
                        Object body = response.body();
                        if (body instanceof java.util.Map) {
                            Object r = ((java.util.Map<?,?>)body).get("response");
                            if (r != null) resp = r.toString();
                        } else {
                            resp = body.toString();
                        }
                    }
                    adapter.addMessage(new ChatMessageLocal(resp, true));
                    rv.scrollToPosition(adapter.getItemCount() - 1);
                }
                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    adapter.addMessage(new ChatMessageLocal("Lỗi: " + t.getMessage(), true));
                    rv.scrollToPosition(adapter.getItemCount() - 1);
                }
            });
        });
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }
}