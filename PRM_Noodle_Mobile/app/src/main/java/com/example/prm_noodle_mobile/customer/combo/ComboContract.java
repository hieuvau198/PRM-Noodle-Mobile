package com.example.prm_noodle_mobile.customer.combo;

import com.example.prm_noodle_mobile.data.model.Combo;
import java.util.List;

public interface ComboContract {
    interface View {
        void showCombos(List<Combo> combos);
        void showLoading();
        void hideLoading();
        void showError(String message);
    }

    interface Presenter {
        void loadCombos();
        void onDestroy();
    }
} 