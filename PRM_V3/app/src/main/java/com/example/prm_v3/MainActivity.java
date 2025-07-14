package com.example.prm_v3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.prm_v3.ui.orders.OrderFragment;
import com.example.prm_v3.ui.create.CreateOrderFragment;
import com.example.prm_v3.ui.cooking.cookingFragment;
import com.example.prm_v3.ui.payment.paymentFragment;
import com.example.prm_v3.ui.profile.profileFragment;
import com.example.prm_v3.ui.auth.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Kiểm tra token, nếu chưa đăng nhập thì chuyển sang LoginActivity
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token == null || token.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);

        // Ẩn ActionBar mặc định để sử dụng header riêng trong Fragment
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bottomNavigationView = findViewById(R.id.nav_view);

        // Set default fragment to Orders
        if (savedInstanceState == null) {
            loadFragment(new OrderFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_orders) {
                selectedFragment = new OrderFragment();
            } else if (itemId == R.id.navigation_create) {
                selectedFragment = new CreateOrderFragment();
            } else if (itemId == R.id.navigation_cooking) {
                selectedFragment = new cookingFragment();
            } else if (itemId == R.id.navigation_payment) {
                selectedFragment = new paymentFragment();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new profileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        fragmentTransaction.commit();
    }

    // Method để refresh fragment hiện tại (có thể dùng sau này)
    private void refreshCurrentFragment() {
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
        }
        // Có thể thêm refresh cho các fragment khác nếu cần
    }
}