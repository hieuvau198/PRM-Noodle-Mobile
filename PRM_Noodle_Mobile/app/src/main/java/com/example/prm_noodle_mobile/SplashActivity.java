package com.example.prm_noodle_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_noodle_mobile.auth.LoginActivity;
import com.example.prm_noodle_mobile.utils.UserSessionManager;

public class SplashActivity extends AppCompatActivity {

    private ImageView ivLogo;
    private TextView tvAppName;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Không cần tự khởi tạo sharedPreferences nữa
        // sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        initViews();
        startAnimations();
        
        // Delay for splash screen
        new Handler().postDelayed(this::checkLoginStatus, 3000);
    }

    private void initViews() {
        ivLogo = findViewById(R.id.iv_logo);
        tvAppName = findViewById(R.id.tv_app_name);
    }

    private void startAnimations() {
        // Logo animation
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale);
        ivLogo.startAnimation(logoAnimation);

        // App name animation
        Animation textAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        tvAppName.startAnimation(textAnimation);
    }

    private void checkLoginStatus() {
        UserSessionManager sessionManager = new UserSessionManager(this);
        boolean isLoggedIn = sessionManager.isLoggedIn();
        
        Intent intent;
        if (isLoggedIn) {
            // User is logged in, go to main activity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // User is not logged in, go to login activity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        
        startActivity(intent);
        finish();
    }
} 