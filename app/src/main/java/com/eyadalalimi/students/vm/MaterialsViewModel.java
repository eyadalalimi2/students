package com.eyadalalimi.students.vm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MaterialsViewModel extends ViewModel {
    public final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    public final MutableLiveData<String> error = new MutableLiveData<>(null);
}
