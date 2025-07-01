package com.example.prm_noodle_mobile.customer.combodetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.Combo;

public class ComboDetailFragment extends Fragment implements ComboDetailContract.View {
    private ComboDetailPresenter presenter;
    private ImageView comboImage;
    private TextView comboName, comboDescription, comboPrice;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_combo_detail, container, false);
        comboImage = view.findViewById(R.id.combo_image);
        comboName = view.findViewById(R.id.combo_name);
        comboDescription = view.findViewById(R.id.combo_description);
        comboPrice = view.findViewById(R.id.combo_price);
        progressBar = view.findViewById(R.id.progress_bar);
        presenter = new ComboDetailPresenter(this);
        int comboId = getArguments() != null ? getArguments().getInt("comboId", 1) : 1;
        presenter.loadComboDetail(comboId);
        return view;
    }

    @Override
    public void showComboDetail(Combo combo) {
        comboName.setText(combo.getComboName());
        comboDescription.setText(combo.getDescription());
        comboPrice.setText(String.format("%,d VND", combo.getPrice()));
        if (combo.getImageUrl() != null && !combo.getImageUrl().isEmpty()) {
            Glide.with(comboImage.getContext())
                    .load(combo.getImageUrl())
                    .placeholder(R.drawable.ic_noodle_placeholder)
                    .into(comboImage);
        } else {
            comboImage.setImageResource(R.drawable.ic_noodle_placeholder);
        }
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.onDestroy();
    }
} 