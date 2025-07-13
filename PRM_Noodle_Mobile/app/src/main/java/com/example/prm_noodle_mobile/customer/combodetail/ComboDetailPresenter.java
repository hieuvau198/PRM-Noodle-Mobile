package com.example.prm_noodle_mobile.customer.combodetail;

import com.example.prm_noodle_mobile.data.api.ApiClient;
import com.example.prm_noodle_mobile.data.api.ComboApi;
import com.example.prm_noodle_mobile.data.model.Combo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComboDetailPresenter implements ComboDetailContract.Presenter {
    private ComboDetailContract.View view;

    public ComboDetailPresenter(ComboDetailContract.View view) {
        this.view = view;
    }

    @Override
    public void loadComboDetail(int comboId) {
        if (view != null) {
            view.showLoading();
            ComboApi comboApi = ApiClient.getClient(((android.content.Context)view)).create(ComboApi.class);
            Call<Combo> call = comboApi.getComboById(comboId);
            call.enqueue(new Callback<Combo>() {
                @Override
                public void onResponse(Call<Combo> call, Response<Combo> response) {
                    view.hideLoading();
                    if (response.isSuccessful() && response.body() != null) {
                        view.showComboDetail(response.body());
                    } else {
                        view.showError("Không lấy được chi tiết combo");
                    }
                }

                @Override
                public void onFailure(Call<Combo> call, Throwable t) {
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