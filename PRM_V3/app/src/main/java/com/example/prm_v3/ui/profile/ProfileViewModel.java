package com.example.prm_v3.ui.profile;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.prm_v3.api.ApiClient;
import com.example.prm_v3.api.ApiResponse;
import com.example.prm_v3.api.ApiService;
import com.example.prm_v3.api.ChangePasswordRequest;
import com.example.prm_v3.api.UpdateProfileRequest;
import com.example.prm_v3.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {
    private static final String TAG = "ProfileViewModel";

    private ApiService apiService;
    private MutableLiveData<User> userProfile = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();

    public ProfileViewModel() {
        apiService = ApiClient.getApiService();
        loading.setValue(false);
    }

    // LiveData getters
    public LiveData<User> getUserProfile() { return userProfile; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<String> getSuccessMessage() { return successMessage; }

    // Load user profile
    public void loadUserProfile() {
        Log.d(TAG, "Loading user profile...");
        loading.setValue(true);
        error.setValue(null);

        Call<User> call = apiService.getUserProfile();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                loading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    Log.d(TAG, "Profile loaded successfully: " + user.getFullName());
                    userProfile.setValue(user);
                    error.setValue(null);
                } else {
                    String errorMsg = handleErrorResponse(response.code());
                    Log.e(TAG, "Failed to load profile: " + errorMsg);
                    error.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loading.setValue(false);
                String errorMsg = handleNetworkError(t);
                Log.e(TAG, "Network error loading profile: " + errorMsg, t);
                error.setValue(errorMsg);
            }
        });
    }

    // Update user profile
    public void updateProfile(String username, String fullName, String phone, String address) {
        Log.d(TAG, "Updating profile...");

        // Validate input
        UpdateProfileRequest request = new UpdateProfileRequest(username, fullName, phone, address);
        if (!request.isValid()) {
            error.setValue("Ít nhất một trường thông tin phải được cập nhật");
            return;
        }

        loading.setValue(true);
        error.setValue(null);

        Call<User> call = apiService.updateProfile(request);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                loading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    User updatedUser = response.body();
                    Log.d(TAG, "Profile updated successfully");
                    userProfile.setValue(updatedUser);
                    successMessage.setValue("Cập nhật thông tin thành công");
                    error.setValue(null);
                } else {
                    String errorMsg = handleErrorResponse(response.code());
                    Log.e(TAG, "Failed to update profile: " + errorMsg);
                    error.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loading.setValue(false);
                String errorMsg = handleNetworkError(t);
                Log.e(TAG, "Network error updating profile: " + errorMsg, t);
                error.setValue(errorMsg);
            }
        });
    }

    // Change password
    public void changePassword(String currentPassword, String newPassword) {
        Log.d(TAG, "Changing password...");

        // Validate input
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);
        String validationError = request.getValidationError();
        if (validationError != null) {
            error.setValue(validationError);
            return;
        }

        loading.setValue(true);
        error.setValue(null);

        Call<ApiResponse> call = apiService.changePassword(request);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                loading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Log.d(TAG, "Password changed successfully");
                        successMessage.setValue("Đổi mật khẩu thành công");
                        error.setValue(null);
                    } else {
                        String errorMsg = apiResponse.getDisplayMessage();
                        Log.e(TAG, "Password change failed: " + errorMsg);
                        error.setValue(errorMsg);
                    }
                } else {
                    String errorMsg = handleErrorResponse(response.code());
                    if (response.code() == 400) {
                        errorMsg = "Mật khẩu hiện tại không đúng";
                    }
                    Log.e(TAG, "Failed to change password: " + errorMsg);
                    error.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                loading.setValue(false);
                String errorMsg = handleNetworkError(t);
                Log.e(TAG, "Network error changing password: " + errorMsg, t);
                error.setValue(errorMsg);
            }
        });
    }

    // Refresh profile data
    public void refreshProfile() {
        Log.d(TAG, "Refreshing profile...");
        loadUserProfile();
    }

    // Clear messages
    public void clearMessages() {
        error.setValue(null);
        successMessage.setValue(null);
    }

    // Error handling
    private String handleErrorResponse(int code) {
        switch (code) {
            case 400:
                return "Dữ liệu không hợp lệ";
            case 401:
                return "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại";
            case 403:
                return "Không có quyền truy cập";
            case 404:
                return "Không tìm thấy thông tin người dùng";
            case 500:
                return "Lỗi server. Vui lòng thử lại sau";
            default:
                return "Có lỗi xảy ra. Mã lỗi: " + code;
        }
    }

    private String handleNetworkError(Throwable t) {
        if (t instanceof java.net.UnknownHostException) {
            return "Không thể kết nối tới server. Vui lòng kiểm tra kết nối mạng";
        } else if (t instanceof java.net.SocketTimeoutException) {
            return "Kết nối quá chậm. Vui lòng thử lại";
        } else if (t instanceof java.net.ConnectException) {
            return "Không thể kết nối tới server";
        } else {
            return "Lỗi kết nối: " + t.getMessage();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "ProfileViewModel cleared");
    }
}