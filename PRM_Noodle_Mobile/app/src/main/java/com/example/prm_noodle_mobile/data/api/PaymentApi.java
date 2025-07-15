package com.example.prm_noodle_mobile.data.api;

import com.example.prm_noodle_mobile.data.model.payment.Payment;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PaymentApi {
    @POST("/api/Payments")
    Call<Void> createPayment(@Body Payment payment);
}
