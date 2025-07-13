package com.example.prm_noodle_mobile.customer.userprofile;

import android.content.Context;
import com.example.prm_noodle_mobile.data.api.ApiClient;
import com.example.prm_noodle_mobile.data.api.UserProfileApi;
import com.example.prm_noodle_mobile.data.model.UserProfile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfilePresenter implements UserProfileContract.Presenter {
    private UserProfileContract.View view;
    private Context context;

    public UserProfilePresenter(UserProfileContract.View view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void loadUserProfile() {
        view.showLoading();
        UserProfileApi api = ApiClient.getClient(context).create(UserProfileApi.class);
        api.getProfile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    view.showUserProfile(response.body());
                } else {
                    view.showError("Không lấy được thông tin user");
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
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