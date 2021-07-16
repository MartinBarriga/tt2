package com.example.martin.AndroidApp.ui.datosUsuario;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DatosUsuarioViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DatosUsuarioViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}