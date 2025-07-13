package com.example.prm_v3.ui.cooking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class cookingViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public cookingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Cooking fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}