package com.example.prm_v3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.prm_v3.ui.orders.OrderFragment;
import com.example.prm_v3.ui.create.CreateOrderFragment;
import com.example.prm_v3.ui.cooking.cookingFragment;
import com.example.prm_v3.ui.payment.paymentFragment;
import com.example.prm_v3.ui.profile.ProfileFragment;
import com.example.prm_v3.ui.auth.LoginActivity;
import com.example.prm_v3.utils.UserManager;
import com.example.prm_v3.utils.RolePermissionHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private UserManager userManager;
    private RolePermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize API client
        com.example.prm_v3.api.ApiClient.init(getApplicationContext());

        // Initialize UserManager and PermissionHelper
        userManager = UserManager.getInstance(this);
        permissionHelper = new RolePermissionHelper(this);

        // Check if user is logged in and has valid role
        if (!userManager.isLoggedIn() || !userManager.hasValidRole()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        // Ẩn ActionBar mặc định để sử dụng header riêng trong Fragment
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bottomNavigationView = findViewById(R.id.nav_view);

        // Setup navigation based on user role
        setupBottomNavigation();

        // Set default fragment to Orders
        if (savedInstanceState == null) {
            loadFragment(new OrderFragment());
        }

        // Display welcome message based on role
        showWelcomeMessage();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_orders) {
                if (permissionHelper.hasPermission(RolePermissionHelper.PERMISSION_VIEW_ORDERS)) {
                    selectedFragment = new OrderFragment();
                } else {
                    showPermissionDeniedMessage(RolePermissionHelper.PERMISSION_VIEW_ORDERS);
                    return false;
                }
            } else if (itemId == R.id.navigation_create) {
                if (permissionHelper.hasPermission(RolePermissionHelper.PERMISSION_CREATE_ORDERS)) {
                    selectedFragment = new CreateOrderFragment();
                } else {
                    showPermissionDeniedMessage(RolePermissionHelper.PERMISSION_CREATE_ORDERS);
                    return false;
                }
            } else if (itemId == R.id.navigation_cooking) {
                if (permissionHelper.hasPermission(RolePermissionHelper.PERMISSION_UPDATE_ORDER_STATUS)) {
                    selectedFragment = new cookingFragment();
                } else {
                    showPermissionDeniedMessage(RolePermissionHelper.PERMISSION_UPDATE_ORDER_STATUS);
                    return false;
                }
            } else if (itemId == R.id.navigation_payment) {
                if (permissionHelper.hasPermission(RolePermissionHelper.PERMISSION_VIEW_PAYMENTS)) {
                    selectedFragment = new paymentFragment();
                } else {
                    showPermissionDeniedMessage(RolePermissionHelper.PERMISSION_VIEW_PAYMENTS);
                    return false;
                }
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void showWelcomeMessage() {
        String userName = userManager.getCurrentUserName();
        String displayName = (userName != null && !userName.isEmpty()) ? userName : userManager.getCurrentUserEmail();
        String role = userManager.getCurrentUserRole();

        String welcomeMessage;
        if (userManager.isAdmin()) {
            welcomeMessage = "Chào mừng Admin " + displayName;
        } else if (userManager.isStaff()) {
            welcomeMessage = "Chào mừng " + displayName + " - Nhân viên";
        } else {
            welcomeMessage = "Chào mừng " + displayName;
        }

        Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show();
    }

    private void showPermissionDeniedMessage(String permission) {
        String message = permissionHelper.getPermissionDeniedMessage(permission);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is still logged in and has valid role when app resumes
        if (!userManager.isLoggedIn() || !userManager.hasValidRole()) {
            handleInvalidSession();
        }
    }

    private void handleInvalidSession() {
        String message;
        if (userManager.isCustomer()) {
            message = "Bạn không có quyền truy cập ứng dụng này";
        } else if (!userManager.isLoggedIn()) {
            message = "Phiên đăng nhập đã hết hạn";
        } else {
            message = "Quyền truy cập không hợp lệ";
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        // Clear user data if they don't have valid role
        if (!userManager.hasValidRole()) {
            userManager.clearUserData();
        }

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        fragmentTransaction.commit();
    }

    // Method để refresh fragment hiện tại
    public void refreshCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        if (currentFragment instanceof OrderFragment) {
            // Refresh orders if needed
            try {
                ((OrderFragment) currentFragment).refreshData();
            } catch (Exception e) {
                // Handle error if refreshData method doesn't exist
                e.printStackTrace();
            }
        } else if (currentFragment instanceof ProfileFragment) {
            // Refresh profile if needed
            try {
                // ProfileFragment will auto-refresh via ViewModel
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (currentFragment instanceof CreateOrderFragment) {
            // Refresh create order fragment if needed
            try {
                // CreateOrderFragment will auto-refresh via ViewModel
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Có thể thêm refresh cho các fragment khác nếu cần
    }

    // Helper method to get current fragment
    public Fragment getCurrentFragment() {
        return getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
    }

    // Method to navigate to specific fragment (can be used from other activities)
    public void navigateToFragment(Class<? extends Fragment> fragmentClass) {
        try {
            Fragment fragment = fragmentClass.newInstance();
            loadFragment(fragment);

            // Update bottom navigation selection
            if (fragment instanceof OrderFragment) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_orders);
            } else if (fragment instanceof CreateOrderFragment) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_create);
            } else if (fragment instanceof cookingFragment) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_cooking);
            } else if (fragment instanceof paymentFragment) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_payment);
            } else if (fragment instanceof ProfileFragment) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể chuyển đến trang này", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to handle user logout
    public void logout() {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    userManager.clearUserData();
                    Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    redirectToLogin();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Get current user info for other fragments/activities
    public UserManager getUserManager() {
        return userManager;
    }

    // Get permission helper
    public RolePermissionHelper getPermissionHelper() {
        return permissionHelper;
    }

    // Check if current user has specific permissions
    public boolean hasPermission(String permission) {
        return permissionHelper.hasPermission(permission);
    }

    // Convenience methods for common permission checks
    public boolean canViewOrders() {
        return hasPermission(RolePermissionHelper.PERMISSION_VIEW_ORDERS);
    }

    public boolean canCreateOrders() {
        return hasPermission(RolePermissionHelper.PERMISSION_CREATE_ORDERS);
    }

    public boolean canUpdateOrderStatus() {
        return hasPermission(RolePermissionHelper.PERMISSION_UPDATE_ORDER_STATUS);
    }

    public boolean canViewPayments() {
        return hasPermission(RolePermissionHelper.PERMISSION_VIEW_PAYMENTS);
    }

    public boolean canManageUsers() {
        return hasPermission(RolePermissionHelper.PERMISSION_MANAGE_USERS);
    }

    public boolean canViewReports() {
        return hasPermission(RolePermissionHelper.PERMISSION_VIEW_REPORTS);
    }

    public boolean canManageProducts() {
        return hasPermission(RolePermissionHelper.PERMISSION_MANAGE_PRODUCTS);
    }

    // Helper method to get current user display info
    public String getCurrentUserDisplayInfo() {
        return String.format("%s (%s)",
                userManager.getUserDisplayName(),
                RolePermissionHelper.getRoleDisplayName(userManager.getCurrentUserRole()));
    }

    // Method to handle back press
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getCurrentFragment();

        // If we're not on the Orders fragment, go back to Orders
        if (!(currentFragment instanceof OrderFragment)) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_orders);
            return;
        }

        // If we're on Orders fragment, show exit confirmation
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Thoát ứng dụng")
                .setMessage("Bạn có chắc chắn muốn thoát ứng dụng không?")
                .setPositiveButton("Thoát", (dialog, which) -> {
                    super.onBackPressed();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Handle activity result if needed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass result to current fragment if needed
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Method to show loading indicator
    public void showLoading(boolean show) {
        // Implement loading indicator if needed
        // For example, show/hide progress bar
    }

    // Method to handle errors
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Method to handle success messages
    public void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}