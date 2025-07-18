package com.example.prm_v3.ui.payment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm_v3.api.ApiClient;
import com.example.prm_v3.api.ApiService;
import com.example.prm_v3.model.Order;
import com.example.prm_v3.model.Payment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePaymentViewModel extends ViewModel {
    private static final String TAG = "CreatePaymentViewModel";

    private ApiService apiService;
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<Order> orderData = new MutableLiveData<>();

    public CreatePaymentViewModel() {
        apiService = ApiClient.getApiService();
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<String> getSuccessMessage() { return successMessage; }
    public LiveData<Order> getOrderData() { return orderData; }

    public void loadOrderForPayment(int orderId) {
        loading.setValue(true);
        error.setValue(null);

        Call<Order> call = apiService.getOrderById(orderId);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                loading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    Order order = response.body();
                    orderData.setValue(order);
                } else {
                    String errorMsg = "Không tìm thấy đơn hàng #" + orderId;
                    if (response.code() == 404) {
                        errorMsg = "Đơn hàng không tồn tại";
                    } else if (response.code() == 403) {
                        errorMsg = "Không có quyền truy cập đơn hàng";
                    }
                    error.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Lỗi kết nối khi tải đơn hàng: " + t.getMessage());
            }
        });
    }

    public void createPayment(Payment payment) {
        loading.setValue(true);
        error.setValue(null);

        Call<Payment> call = apiService.createPayment(payment);
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                loading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    Payment createdPayment = response.body();
                    successMessage.setValue("Tạo thanh toán #" + createdPayment.getPaymentId() + " thành công!");
                } else {
                    String errorMsg = handleCreatePaymentError(response.code());
                    error.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Lỗi kết nối khi tạo thanh toán: " + t.getMessage());
            }
        });
    }

    private String handleCreatePaymentError(int code) {
        switch (code) {
            case 400:
                return "Thông tin thanh toán không hợp lệ";
            case 404:
                return "Không tìm thấy đơn hàng";
            case 409:
                return "Đơn hàng đã có thanh toán";
            case 422:
                return "Đơn hàng chưa thể tạo thanh toán (có thể đã bị hủy)";
            case 500:
                return "Lỗi máy chủ, vui lòng thử lại";
            default:
                return "Tạo thanh toán thất bại (Mã lỗi: " + code + ")";
        }
    }
}