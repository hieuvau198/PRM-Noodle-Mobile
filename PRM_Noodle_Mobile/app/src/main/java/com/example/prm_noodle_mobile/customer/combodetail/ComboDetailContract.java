package com.example.prm_noodle_mobile.customer.combodetail;

import com.example.prm_noodle_mobile.data.model.Combo;

public interface ComboDetailContract {
    interface View {
        void showComboDetail(Combo combo);
        void showLoading();
        void hideLoading();
        void showError(String message);
    }

    interface Presenter {
        void loadComboDetail(int comboId);
        void onDestroy();
    }
} 