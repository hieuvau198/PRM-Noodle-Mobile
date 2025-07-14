package com.example.prm_noodle_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.prm_noodle_mobile.customer.home.HomeFragment;
import com.example.prm_noodle_mobile.customer.product.ProductFragment;
import com.example.prm_noodle_mobile.customer.productdetail.ProductDetailFragment;
import com.example.prm_noodle_mobile.customer.cart.CartFragment;
import com.example.prm_noodle_mobile.customer.orderconfirm.OrderConfirmFragment;
import com.example.prm_noodle_mobile.customer.combo.ComboFragment;
import com.example.prm_noodle_mobile.auth.LoginActivity;
import com.example.prm_noodle_mobile.customer.userprofile.UserProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleVnpayCallback(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleVnpayCallback(getIntent());
    }

    private void handleVnpayCallback(Intent intent) {
        // Đã loại bỏ callback VNPay, không cần xử lý gì ở đây nữa
    }

    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment to Home
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
//            } else if (itemId == R.id.nav_combo) {
//                selectedFragment = new ComboFragment();
            } else if (itemId == R.id.nav_product) {
                selectedFragment = new ProductFragment();
            } else if (itemId == R.id.nav_order_confirm) {
                selectedFragment = new OrderConfirmFragment();
            }else if (itemId == R.id.nav_user_profile) { // Bổ sung điều kiện này
                selectedFragment = new UserProfileFragment(); // Bổ sung điều kiện này
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Clear login state
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}