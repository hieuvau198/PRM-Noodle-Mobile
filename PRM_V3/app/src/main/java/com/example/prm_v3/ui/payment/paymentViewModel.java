package com.example.prm_v3.ui.payment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class paymentViewModel extends ViewModel {


    private final MutableLiveData<String> mText;

    public paymentViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Payment fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}