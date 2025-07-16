package com.example.prm_v3.utils;

import android.content.Context;

/**
 * Helper class for managing role-based permissions
 */
public class RolePermissionHelper {

    // Valid roles for this application
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_STAFF = "staff";
    public static final String ROLE_CUSTOMER = "customer";

    // Permission constants
    public static final String PERMISSION_VIEW_ORDERS = "VIEW_ORDERS";
    public static final String PERMISSION_CREATE_ORDERS = "CREATE_ORDERS";
    public static final String PERMISSION_UPDATE_ORDER_STATUS = "UPDATE_ORDER_STATUS";
    public static final String PERMISSION_CANCEL_ORDERS = "CANCEL_ORDERS";
    public static final String PERMISSION_VIEW_PAYMENTS = "VIEW_PAYMENTS";
    public static final String PERMISSION_MANAGE_USERS = "MANAGE_USERS";
    public static final String PERMISSION_VIEW_REPORTS = "VIEW_REPORTS";
    public static final String PERMISSION_MANAGE_PRODUCTS = "MANAGE_PRODUCTS";

    private UserManager userManager;

    public RolePermissionHelper(Context context) {
        userManager = UserManager.getInstance(context);
    }

    /**
     * Check if user can access the application
     */
    public boolean canAccessApp() {
        return canAccessApp(userManager.getCurrentUserRole());
    }

    /**
     * Check if role can access the application
     */
    public static boolean canAccessApp(String role) {
        if (role == null) return false;

        String normalizedRole = role.toLowerCase().trim();
        return normalizedRole.equals(ROLE_ADMIN) || normalizedRole.equals(ROLE_STAFF);
    }

    /**
     * Check if user has specific permission
     */
    public boolean hasPermission(String permission) {
        return hasPermission(userManager.getCurrentUserRole(), permission);
    }

    /**
     * Check if role has specific permission
     */
    public static boolean hasPermission(String role, String permission) {
        if (role == null || permission == null) return false;

        String normalizedRole = role.toLowerCase().trim();

        // Customer has no permissions in this app
        if (normalizedRole.equals(ROLE_CUSTOMER)) {
            return false;
        }

        // Admin has all permissions
        if (normalizedRole.equals(ROLE_ADMIN)) {
            return true;
        }

        // Staff permissions
        if (normalizedRole.equals(ROLE_STAFF)) {
            switch (permission) {
                case PERMISSION_VIEW_ORDERS:
                case PERMISSION_CREATE_ORDERS:
                case PERMISSION_UPDATE_ORDER_STATUS:
                case PERMISSION_CANCEL_ORDERS:
                case PERMISSION_VIEW_PAYMENTS:
                    return true;
                case PERMISSION_MANAGE_USERS:
                case PERMISSION_VIEW_REPORTS:
                case PERMISSION_MANAGE_PRODUCTS:
                    return false; // Only admin can do these
                default:
                    return false;
            }
        }

        return false;
    }

    /**
     * Get user-friendly error message for permission denial
     */
    public String getPermissionDeniedMessage(String permission) {
        String role = userManager.getCurrentUserRole();

        if (role == null) {
            return "Bạn cần đăng nhập để thực hiện chức năng này";
        }

        if (role.equalsIgnoreCase(ROLE_CUSTOMER)) {
            return "Bạn không có quyền truy cập ứng dụng này";
        }

        switch (permission) {
            case PERMISSION_VIEW_ORDERS:
                return "Bạn không có quyền xem đơn hàng";
            case PERMISSION_CREATE_ORDERS:
                return "Bạn không có quyền tạo đơn hàng";
            case PERMISSION_UPDATE_ORDER_STATUS:
                return "Bạn không có quyền cập nhật trạng thái đơn hàng";
            case PERMISSION_CANCEL_ORDERS:
                return "Bạn không có quyền hủy đơn hàng";
            case PERMISSION_VIEW_PAYMENTS:
                return "Bạn không có quyền xem thông tin thanh toán";
            case PERMISSION_MANAGE_USERS:
                return "Chỉ admin mới có quyền quản lý người dùng";
            case PERMISSION_VIEW_REPORTS:
                return "Chỉ admin mới có quyền xem báo cáo";
            case PERMISSION_MANAGE_PRODUCTS:
                return "Chỉ admin mới có quyền quản lý sản phẩm";
            default:
                return "Bạn không có quyền thực hiện chức năng này";
        }
    }

    /**
     * Check if current user is admin
     */
    public boolean isAdmin() {
        return userManager.isAdmin();
    }

    /**
     * Check if current user is staff
     */
    public boolean isStaff() {
        return userManager.isStaff();
    }

    /**
     * Check if current user is customer
     */
    public boolean isCustomer() {
        return userManager.isCustomer();
    }

    /**
     * Get role display name
     */
    public static String getRoleDisplayName(String role) {
        if (role == null) return "Không xác định";

        switch (role.toLowerCase()) {
            case ROLE_ADMIN:
                return "Quản trị viên";
            case ROLE_STAFF:
                return "Nhân viên";
            case ROLE_CUSTOMER:
                return "Khách hàng";
            default:
                return role;
        }
    }

    /**
     * Get all valid roles for this app
     */
    public static String[] getValidAppRoles() {
        return new String[]{ROLE_ADMIN, ROLE_STAFF};
    }

    /**
     * Get all roles (including customer)
     */
    public static String[] getAllRoles() {
        return new String[]{ROLE_ADMIN, ROLE_STAFF, ROLE_CUSTOMER};
    }

    /**
     * Validate and log access attempt
     */
    public boolean validateAndLogAccess(String feature) {
        String role = userManager.getCurrentUserRole();
        boolean hasAccess = canAccessApp();

        if (!hasAccess) {
            // Log access attempt for security
            System.out.println("Access denied for user " + userManager.getCurrentUserId() +
                    " with role " + role + " trying to access " + feature);
        }

        return hasAccess;
    }
}