package com.nishiket.converse.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.nishiket.converse.model.UserDetailModel;
import com.nishiket.converse.repository.UsersRepository;

import java.util.List;

public class UserDataViewModel extends AndroidViewModel implements UsersRepository.firebaseComplte {
    private MutableLiveData<List<UserDetailModel>> mutableLiveData = new MutableLiveData<>();
    private UsersRepository usersRepository;

    public MutableLiveData<List<UserDetailModel>> getMutableLiveData() {
        return mutableLiveData;
    }

    public UserDataViewModel(@NonNull Application application) {
        super(application);
        usersRepository = new UsersRepository(application,this);
    }

    public void  getUserDetail(String email){
            usersRepository.getUserDetail(email);
    }

    @Override
    public void onComplete(List<UserDetailModel> userFirebaseModelList) {
        mutableLiveData.setValue(userFirebaseModelList);
    }
}
