package com.example.prm_v3.ui.payment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.prm_v3.api.ApiClient;
import com.example.prm_v3.api.ApiService;
import com.example.prm_v3.model.Payment;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class paymentViewModel extends ViewModel {


    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<Payment>> paymentsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    private final ApiService apiService;

    public paymentViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Payment fragment");
        apiService = ApiClient.getApiService();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<Payment>> getPaymentsLiveData() {
        return paymentsLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    public void fetchPayments() {
        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);
        Call<List<Payment>> call = apiService.getPayments();
        call.enqueue(new Callback<List<Payment>>() {
            @Override
            public void onResponse(Call<List<Payment>> call, Response<List<Payment>> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    paymentsLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue("Không thể tải danh sách payment");
                    paymentsLiveData.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<List<Payment>> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue("Lỗi kết nối: " + t.getMessage());
                paymentsLiveData.setValue(null);
            }
        });
    }
}