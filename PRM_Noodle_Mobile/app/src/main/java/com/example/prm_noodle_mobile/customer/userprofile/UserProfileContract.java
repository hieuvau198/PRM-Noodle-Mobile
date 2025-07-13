package com.example.prm_noodle_mobile.customer.userprofile;


import com.example.prm_noodle_mobile.data.model.UserProfile;

public interface UserProfileContract {
    interface View {
        void showUserProfile(UserProfile user);
        void showLoading();
        void hideLoading();
        void showError(String message);
    }

    interface Presenter {
        void loadUserProfile();
        void onDestroy();
    }
}