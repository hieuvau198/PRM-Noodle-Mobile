package com.example.prm_v3.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prm_v3.R;
import com.example.prm_v3.api.ApiClient;
import com.example.prm_v3.api.ApiService;
import com.example.prm_v3.api.AuthResponse;
import com.example.prm_v3.api.LoginRequest;
import com.example.prm_v3.model.Login;
import com.example.prm_v3.model.User;
import com.example.prm_v3.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ImageButton;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

public class LoginActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getApiService();
        Login request = new Login(email, password);
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    User user = auth.getUser();

                    // Kiểm tra role của user trước khi cho phép đăng nhập
                    if (user != null) {
                        String userRole = user.getRole();
                        if (userRole == null ||
                                (!userRole.equalsIgnoreCase("admin") && !userRole.equalsIgnoreCase("staff"))) {
                            Toast.makeText(LoginActivity.this,
                                    "Bạn không có quyền truy cập ứng dụng này. Chỉ admin và staff được phép sử dụng.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Lưu token và user info vào SharedPreferences
                    long expiryMillis = System.currentTimeMillis() + 2 * 60 * 60 * 1000; // 2 tiếng
                    SharedPreferences.Editor editor = getSharedPreferences("app_prefs", MODE_PRIVATE).edit();

                    editor.putString("token", auth.getToken());
                    editor.putLong("token_expiry", expiryMillis);

                    // Lưu user info
                    editor.putInt("user_id", user.getUserId());
                    editor.putString("user_name", user.getFullName());
                    editor.putString("user_username", user.getUsername());
                    editor.putString("user_email", user.getEmail());
                    editor.putString("user_phone", user.getPhone());
                    editor.putString("user_address", user.getAddress());
                    editor.putString("user_role", user.getRole());

                    editor.apply();

                    // Thông báo đăng nhập thành công với role
                    String welcomeMessage = user.getRole().equalsIgnoreCase("admin") ?
                            "Chào mừng Admin " + user.getDisplayName() :
                            "Chào mừng " + user.getDisplayName();
                    Toast.makeText(LoginActivity.this, welcomeMessage, Toast.LENGTH_SHORT).show();

                    // Chuyển sang MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Đăng nhập thất bại";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}