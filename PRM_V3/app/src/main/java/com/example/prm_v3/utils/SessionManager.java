package com.example.prm_v3.utils;

import android.content.Context;
import com.example.prm_v3.model.User;
import com.example.prm_v3.api.AuthResponse;

/**
 * Session management utility class
 */
public class SessionManager {
    private static SessionManager instance;
    private UserManager userManager;
    private Context context;

    private SessionManager(Context context) {
        this.context = context.getApplicationContext();
        this.userManager = UserManager.getInstance(context);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    /**
     * Save user session after successful login
     */
    public void saveUserSession(AuthResponse authResponse) {
        if (authResponse == null) return;

        // Calculate token expiry (2 hours from now)
        long expiryMillis = System.currentTimeMillis() + (2 * 60 * 60 * 1000);

        // Save token and expiry
        userManager.setToken(authResponse.getToken());
        userManager.setTokenExpiry(expiryMillis);

        // Save user info
        User user = authResponse.getUser();
        if (user != null) {
            userManager.setCurrentUserId(user.getUserId());
            userManager.setCurrentUserName(user.getFullName());
            userManager.setCurrentUsername(user.getUsername());
            userManager.setCurrentUserEmail(user.getEmail());
            userManager.setCurrentUserPhone(user.getPhone());
            userManager.setCurrentUserAddress(user.getAddress());
            userManager.setCurrentUserRole(user.getRole());
        }
    }

    /**
     * Get current user ID for API calls
     */
    public int getCurrentUserId() {
        return userManager.getCurrentUserId();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return userManager.isLoggedIn();
    }

    /**
     * Check if current session is valid (logged in + valid role)
     */
    public boolean isSessionValid() {
        return userManager.isLoggedIn() && userManager.isTokenValid() && userManager.hasValidRole();
    }

    /**
     * Get current user info
     */
    public User getCurrentUser() {
        if (!isLoggedIn()) return null;

        User user = new User();
        user.setUserId(userManager.getCurrentUserId());
        user.setFullName(userManager.getCurrentUserName());
        user.setUsername(userManager.getCurrentUsername());
        user.setEmail(userManager.getCurrentUserEmail());
        user.setPhone(userManager.getCurrentUserPhone());
        user.setAddress(userManager.getCurrentUserAddress());
        user.setRole(userManager.getCurrentUserRole());
        return user;
    }

    /**
     * Get user display name
     */
    public String getUserDisplayName() {
        return userManager.getUserDisplayName();
    }

    /**
     * Get user's default delivery address
     */
    public String getDefaultDeliveryAddress() {
        return userManager.getDefaultDeliveryAddress();
    }

    /**
     * Update user profile information
     */
    public void updateUserProfile(User user) {
        if (user == null) return;

        userManager.setCurrentUserName(user.getFullName());
        userManager.setCurrentUsername(user.getUsername());
        userManager.setCurrentUserEmail(user.getEmail());
        userManager.setCurrentUserPhone(user.getPhone());
        userManager.setCurrentUserAddress(user.getAddress());
        userManager.setCurrentUserRole(user.getRole());
    }

    /**
     * Update user's delivery address
     */
    public void updateDeliveryAddress(String address) {
        userManager.setCurrentUserAddress(address);
    }

    /**
     * Check if user has specific role
     */
    public boolean hasRole(String role) {
        return userManager.hasRole(role);
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return userManager.isAdmin();
    }

    /**
     * Check if user is staff
     */
    public boolean isStaff() {
        return userManager.isStaff();
    }

    /**
     * Check if user is customer (not allowed in this app)
     */
    public boolean isCustomer() {
        return userManager.isCustomer();
    }

    /**
     * Check if user has valid role for this app
     */
    public boolean hasValidRole() {
        return userManager.hasValidRole();
    }

    /**
     * Check if user can create orders
     */
    public boolean canCreateOrders() {
        return isLoggedIn();
    }

    /**
     * Check if user can manage orders
     */
    public boolean canManageOrders() {
        return isAdmin() || isStaff();
    }

    /**
     * Check if user can view payments
     */
    public boolean canViewPayments() {
        return isAdmin() || isStaff();
    }

    /**
     * Clear user session (logout)
     */
    public void clearSession() {
        userManager.clearUserData();
    }

    /**
     * Get session token
     */
    public String getToken() {
        return userManager.getToken();
    }

    /**
     * Check if token is about to expire (within 5 minutes)
     */
    public boolean isTokenNearExpiry() {
        long expiry = userManager.getTokenExpiry();
        if (expiry == 0) return false;

        long currentTime = System.currentTimeMillis();
        long fiveMinutes = 5 * 60 * 1000;

        return (expiry - currentTime) <= fiveMinutes;
    }

    /**
     * Get time until token expires (in minutes)
     */
    public long getTokenExpiryInMinutes() {
        long expiry = userManager.getTokenExpiry();
        if (expiry == 0) return 0;

        long currentTime = System.currentTimeMillis();
        long diffMillis = expiry - currentTime;

        return Math.max(0, diffMillis / (60 * 1000));
    }

    /**
     * Refresh token if needed
     */
    public boolean needsTokenRefresh() {
        return isTokenNearExpiry();
    }

    /**
     * Validate session and return error message if invalid
     */
    public String validateSession() {
        if (!isLoggedIn()) {
            return "Bạn cần đăng nhập để thực hiện chức năng này";
        }

        if (!userManager.hasValidRole()) {
            return "Bạn không có quyền truy cập ứng dụng này";
        }

        if (!userManager.isTokenValid()) {
            return "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại";
        }

        return null; // Session is valid
    }

    /**
     * Get session debug info
     */
    public String getSessionDebugInfo() {
        return String.format("Session Info:\n" +
                        "User ID: %d\n" +
                        "User Name: %s\n" +
                        "Is Logged In: %b\n" +
                        "Token Valid: %b\n" +
                        "Token Expires In: %d minutes\n" +
                        "Is Admin: %b\n" +
                        "Is Staff: %b",
                getCurrentUserId(),
                getUserDisplayName(),
                isLoggedIn(),
                userManager.isTokenValid(),
                getTokenExpiryInMinutes(),
                isAdmin(),
                isStaff()
        );
    }
}