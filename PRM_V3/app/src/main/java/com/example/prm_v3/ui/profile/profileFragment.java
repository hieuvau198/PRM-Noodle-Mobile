package com.example.prm_v3.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.prm_v3.R;
import com.example.prm_v3.databinding.FragmentProfileBinding;
import com.example.prm_v3.model.User;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView started");

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupViewModel();
        setupClickListeners();
        observeViewModel();

        // Load profile data
        profileViewModel.loadUserProfile();

        return root;
    }

    private void setupViewModel() {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    private void setupClickListeners() {
        // Edit profile button
        binding.btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        // Change password button
        binding.btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        // Logout button
        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmDialog());

        // Refresh profile
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            profileViewModel.refreshProfile();
        });
    }

    private void observeViewModel() {
        // Observe profile data
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), this::updateUI);

        // Observe loading state
        profileViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "Loading state: " + isLoading);

            if (isLoading && !binding.swipeRefreshLayout.isRefreshing()) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.scrollView.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.scrollView.setVisibility(View.VISIBLE);
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Observe errors
        profileViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Error: " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        // Observe success messages
        profileViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Log.d(TAG, "Success: " + message);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(User user) {
        if (user == null) {
            Log.w(TAG, "User profile is null");
            return;
        }

        Log.d(TAG, "Updating UI with user: " + user.getFullName());

        // Update profile info
        binding.tvFullName.setText(user.getFullName());
        binding.tvUsername.setText("@" + user.getUsername());
        binding.tvRole.setText(user.getDisplayRole());

        // Update contact info
        binding.tvEmail.setText(user.getEmail());
        binding.tvFullNameDetail.setText(user.getFullName());
        binding.tvUsernameDetail.setText(user.getUsername());

        // Phone
        if (user.hasPhone()) {
            binding.tvPhone.setText(user.getPhone());
            binding.tvPhone.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            binding.tvPhone.setText("Chưa cập nhật");
            binding.tvPhone.setTextColor(getResources().getColor(R.color.gray_600));
        }

        // Address
        if (user.hasAddress()) {
            binding.tvAddress.setText(user.getAddress());
            binding.tvAddress.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            binding.tvAddress.setText("Chưa cập nhật");
            binding.tvAddress.setTextColor(getResources().getColor(R.color.gray_600));
        }
    }

    private void showEditProfileDialog() {
        User currentUser = profileViewModel.getUserProfile().getValue();
        if (currentUser == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);

        EditText etFullName = dialogView.findViewById(R.id.et_full_name);
        EditText etUsername = dialogView.findViewById(R.id.et_username);
        EditText etPhone = dialogView.findViewById(R.id.et_phone);
        EditText etAddress = dialogView.findViewById(R.id.et_address);

        // Pre-fill current data
        etFullName.setText(currentUser.getFullName());
        etUsername.setText(currentUser.getUsername());
        etPhone.setText(currentUser.getPhone());
        etAddress.setText(currentUser.getAddress());

        new AlertDialog.Builder(getContext())
                .setTitle("Chỉnh sửa thông tin")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String fullName = etFullName.getText().toString().trim();
                    String username = etUsername.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String address = etAddress.getText().toString().trim();

                    profileViewModel.updateProfile(username, fullName, phone, address);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);

        EditText etCurrentPassword = dialogView.findViewById(R.id.et_current_password);
        EditText etNewPassword = dialogView.findViewById(R.id.et_new_password);
        EditText etConfirmPassword = dialogView.findViewById(R.id.et_confirm_password);

        new AlertDialog.Builder(getContext())
                .setTitle("Đổi mật khẩu")
                .setView(dialogView)
                .setPositiveButton("Đổi mật khẩu", (dialog, which) -> {
                    String currentPassword = etCurrentPassword.getText().toString().trim();
                    String newPassword = etNewPassword.getText().toString().trim();
                    String confirmPassword = etConfirmPassword.getText().toString().trim();

                    if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(getContext(), "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    profileViewModel.changePassword(currentPassword, newPassword);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // Handle logout logic here
                    // For example: clear preferences, navigate to login
                    Toast.makeText(getContext(), "Đang đăng xuất...", Toast.LENGTH_SHORT).show();

                    // You can implement actual logout logic here
                    // Example:
                    // SharedPreferences prefs = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    // prefs.edit().clear().apply();
                    // startActivity(new Intent(getContext(), LoginActivity.class));
                    // getActivity().finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        if (profileViewModel != null) {
            profileViewModel.clearMessages();
        }
        binding = null;
    }
}