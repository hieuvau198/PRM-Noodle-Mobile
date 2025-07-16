package com.example.prm_v3.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {
    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_USERNAME = "user_username";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_ADDRESS = "user_address";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_TOKEN_EXPIRY = "token_expiry";

    private static UserManager instance;
    private SharedPreferences sharedPreferences;
    private Context context;

    private UserManager(Context context) {
        this.context = context.getApplicationContext();
        this.sharedPreferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context);
        }
        return instance;
    }

    // User ID methods
    public int getCurrentUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public void setCurrentUserId(int userId) {
        sharedPreferences.edit().putInt(KEY_USER_ID, userId).apply();
    }

    // Username methods
    public String getCurrentUsername() {
        return sharedPreferences.getString(KEY_USER_USERNAME, "");
    }

    public void setCurrentUsername(String username) {
        sharedPreferences.edit().putString(KEY_USER_USERNAME, username).apply();
    }

    // User info methods
    public String getCurrentUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public void setCurrentUserName(String name) {
        sharedPreferences.edit().putString(KEY_USER_NAME, name).apply();
    }

    public String getCurrentUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public void setCurrentUserEmail(String email) {
        sharedPreferences.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public String getCurrentUserPhone() {
        return sharedPreferences.getString(KEY_USER_PHONE, "");
    }

    public void setCurrentUserPhone(String phone) {
        sharedPreferences.edit().putString(KEY_USER_PHONE, phone).apply();
    }

    public String getCurrentUserAddress() {
        return sharedPreferences.getString(KEY_USER_ADDRESS, "");
    }

    public void setCurrentUserAddress(String address) {
        sharedPreferences.edit().putString(KEY_USER_ADDRESS, address).apply();
    }

    public String getCurrentUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, "");
    }

    public void setCurrentUserRole(String role) {
        sharedPreferences.edit().putString(KEY_USER_ROLE, role).apply();
    }

    // Token methods
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void setToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public long getTokenExpiry() {
        return sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0);
    }

    public void setTokenExpiry(long expiry) {
        sharedPreferences.edit().putLong(KEY_TOKEN_EXPIRY, expiry).apply();
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        int userId = getCurrentUserId();
        String token = getToken();
        long expiry = getTokenExpiry();
        String role = getCurrentUserRole();

        if (userId == -1 || token == null || token.isEmpty()) {
            return false;
        }

        // Check if user has valid role (only admin and staff allowed)
        if (!isValidRole(role)) {
            clearUserData();
            return false;
        }

        // Check if token is expired
        if (expiry > 0 && System.currentTimeMillis() > expiry) {
            clearUserData();
            return false;
        }

        return true;
    }

    // Check if role is valid for this app (only admin and staff)
    private boolean isValidRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return false;
        }

        String normalizedRole = role.toLowerCase().trim();
        return normalizedRole.equals("admin") || normalizedRole.equals("staff");
    }

    // Public method to check if user has valid role
    public boolean hasValidRole() {
        return isValidRole(getCurrentUserRole());
    }

    // Check if token is valid
    public boolean isTokenValid() {
        String token = getToken();
        long expiry = getTokenExpiry();

        if (token == null || token.isEmpty()) {
            return false;
        }

        return expiry == 0 || System.currentTimeMillis() <= expiry;
    }

    // Clear all user data (logout)
    public void clearUserData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_USERNAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_PHONE);
        editor.remove(KEY_USER_ADDRESS);
        editor.remove(KEY_USER_ROLE);
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_TOKEN_EXPIRY);
        editor.apply();
    }

    // Get user display name
    public String getUserDisplayName() {
        String name = getCurrentUserName();
        if (name != null && !name.isEmpty()) {
            return name;
        }

        String email = getCurrentUserEmail();
        if (email != null && !email.isEmpty()) {
            return email;
        }

        return "User #" + getCurrentUserId();
    }

    // Check if user has specific role
    public boolean hasRole(String role) {
        String currentRole = getCurrentUserRole();
        return currentRole != null && currentRole.equalsIgnoreCase(role);
    }

    // Check if user is admin
    public boolean isAdmin() {
        return hasRole("admin") || hasRole("administrator");
    }

    // Check if user is staff
    public boolean isStaff() {
        return hasRole("staff");
    }

    // Check if user is customer (not allowed in this app)
    public boolean isCustomer() {
        return hasRole("customer");
    }

    // Update user info
    public void updateUserInfo(String name, String username, String email, String phone, String address) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (name != null) editor.putString(KEY_USER_NAME, name);
        if (username != null) editor.putString(KEY_USER_USERNAME, username);
        if (email != null) editor.putString(KEY_USER_EMAIL, email);
        if (phone != null) editor.putString(KEY_USER_PHONE, phone);
        if (address != null) editor.putString(KEY_USER_ADDRESS, address);
        editor.apply();
    }

    // Get default delivery address
    public String getDefaultDeliveryAddress() {
        return getCurrentUserAddress();
    }

    // Debug info
    public String getDebugInfo() {
        return String.format("UserManager Debug:\n" +
                        "UserId: %d\n" +
                        "Username: %s\n" +
                        "Name: %s\n" +
                        "Email: %s\n" +
                        "Phone: %s\n" +
                        "Address: %s\n" +
                        "Role: %s\n" +
                        "Token: %s\n" +
                        "Token Expiry: %s\n" +
                        "Is Logged In: %b\n" +
                        "Is Token Valid: %b",
                getCurrentUserId(),
                getCurrentUsername(),
                getCurrentUserName(),
                getCurrentUserEmail(),
                getCurrentUserPhone(),
                getCurrentUserAddress(),
                getCurrentUserRole(),
                getToken() != null ? "Present" : "None",
                getTokenExpiry() > 0 ? "Set" : "None",
                isLoggedIn(),
                isTokenValid()
        );
    }
}