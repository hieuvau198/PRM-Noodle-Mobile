package com.example.prm_noodle_mobile.customer.combo;

import com.example.prm_noodle_mobile.data.api.ApiClient;
import com.example.prm_noodle_mobile.data.api.ComboApi;
import com.example.prm_noodle_mobile.data.model.Combo;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComboPresenter implements ComboContract.Presenter {
    private ComboContract.View view;

    public ComboPresenter(ComboContract.View view) {
        this.view = view;
    }

    @Override
    public void loadCombos() {
        if (view != null) {
            view.showLoading();
            ComboApi comboApi = ApiClient.getClient(((android.content.Context)view)).create(ComboApi.class);
            Call<List<Combo>> call = comboApi.getAvailableCombos();
            call.enqueue(new Callback<List<Combo>>() {
                @Override
                public void onResponse(Call<List<Combo>> call, Response<List<Combo>> response) {
                    view.hideLoading();
                    if (response.isSuccessful() && response.body() != null) {
                        view.showCombos(response.body());
                    } else {
                        view.showError("Không lấy được danh sách combo");
                    }
                }

                @Override
                public void onFailure(Call<List<Combo>> call, Throwable t) {
                    view.hideLoading();
                    view.showError("Lỗi kết nối: " + t.getMessage());
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        view = null;
    }
} 