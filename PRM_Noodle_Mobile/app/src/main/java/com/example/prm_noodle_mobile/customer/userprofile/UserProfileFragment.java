package com.example.prm_noodle_mobile.customer.userprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.prm_noodle_mobile.R;
import com.example.prm_noodle_mobile.data.model.UserProfile;

public class UserProfileFragment extends Fragment implements UserProfileContract.View {

    private UserProfileContract.Presenter presenter;
    private TextView tvFullName, tvUsername, tvEmail, tvPhone, tvAddress, tvRole;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        tvFullName = view.findViewById(R.id.tv_full_name);
        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvAddress = view.findViewById(R.id.tv_address);
        tvRole = view.findViewById(R.id.tv_role);

        presenter = new UserProfilePresenter(this, requireContext());
        presenter.loadUserProfile();

        return view;
    }

    @Override
    public void showUserProfile(UserProfile user) {
        tvFullName.setText("Họ tên: " + user.getFullName());
        tvUsername.setText("Username: " + user.getUsername());
        tvEmail.setText("Email: " + user.getEmail());
        tvPhone.setText("Số điện thoại: " + user.getPhone());
        tvAddress.setText("Địa chỉ: " + user.getAddress());
        tvRole.setText("Vai trò: " + user.getRole());
    }

    @Override
    public void showLoading() {
        // TODO: Hiển thị loading indicator nếu muốn
    }

    @Override
    public void hideLoading() {
        // TODO: Ẩn loading indicator nếu muốn
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