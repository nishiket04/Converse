package com.nishiket.converse.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.nishiket.converse.model.UserDetailModel;
import com.nishiket.converse.repository.NewChatAndGroupRepository;

import java.util.List;

public class NewChatAndGroupViewModel extends AuthViewModel implements NewChatAndGroupRepository.firebaseComplete {

    private NewChatAndGroupRepository newChatAndGroupRepository;
    private MutableLiveData<List<UserDetailModel>> mutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<UserDetailModel>> getMutableLiveData() {
        return mutableLiveData;
    }

    public NewChatAndGroupViewModel(@NonNull Application application) {
        super(application);
        newChatAndGroupRepository = new NewChatAndGroupRepository(application,this);
    }

    public void getUsers(String email){
        newChatAndGroupRepository.getAllUsers(email);
    }

    @Override
    public void onGetUsers(List<UserDetailModel> userDetailModelList) {
        mutableLiveData.setValue(userDetailModelList);
    }
}
