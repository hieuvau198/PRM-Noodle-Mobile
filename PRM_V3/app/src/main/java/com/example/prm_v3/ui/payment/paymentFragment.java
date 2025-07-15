package com.example.prm_v3.ui.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.prm_v3.adapter.PaymentAdapter;
import com.example.prm_v3.model.Payment;
import java.util.List;

import com.example.prm_v3.databinding.FragmentPaymentBinding;

public class paymentFragment extends Fragment {

    private FragmentPaymentBinding binding;
    private paymentViewModel paymentViewModel;
    private PaymentAdapter paymentAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        paymentViewModel = new ViewModelProvider(this).get(paymentViewModel.class);
        binding = FragmentPaymentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup RecyclerView
        paymentAdapter = new PaymentAdapter();
        binding.recyclerViewPayments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPayments.setAdapter(paymentAdapter);

        // Observe payments
        paymentViewModel.getPaymentsLiveData().observe(getViewLifecycleOwner(), payments -> {
            if (payments != null) {
                paymentAdapter.setPayments(payments);
            }
        });
        paymentViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                // Hiển thị lỗi nếu cần
                android.widget.Toast.makeText(getContext(), error, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        paymentViewModel.getLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            // Hiển thị progress nếu cần
        });

        // Lắng nghe sự kiện tạo payment thành công từ OrderRepository
        com.example.prm_v3.repository.OrderRepository.getInstance().patchOrderStatus(0, "", new com.example.prm_v3.repository.OrderRepository.OnUpdateStatusListener() {
            @Override
            public void onSuccess(String message) {
                // Không cần xử lý ở đây
            }
            @Override
            public void onError(String error) {
                // Không cần xử lý ở đây
            }
            @Override
            public void onPaymentCreated(boolean success, String message) {
                if (success) {
                    paymentViewModel.fetchPayments();
                }
                android.widget.Toast.makeText(getContext(), message, android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch payments khi vào màn
        paymentViewModel.fetchPayments();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}