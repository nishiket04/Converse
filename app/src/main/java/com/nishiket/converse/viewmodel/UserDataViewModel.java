package com.nishiket.converse.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.nishiket.converse.model.UserDetailModel;
import com.nishiket.converse.model.UserFriendsModel;
import com.nishiket.converse.repository.UsersRepository;

import java.util.List;

public class UserDataViewModel extends AndroidViewModel implements UsersRepository.firebaseComplte {
    private MutableLiveData<List<UserDetailModel>> mutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<UserFriendsModel>> userFriendsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<UserDetailModel>> userFriendsDetailsMutableLiveDara = new MutableLiveData<>();
    private UsersRepository usersRepository;

    public MutableLiveData<List<UserDetailModel>> getUserFriendsDetailsMutableLiveDara() {
        return userFriendsDetailsMutableLiveDara;
    }

    public MutableLiveData<List<UserFriendsModel>> getUserFriendsMutableLiveData() {
        return userFriendsMutableLiveData;
    }

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

    public void getUserFriends(String email){
        usersRepository.getUserFriends(email);
    }

    public void getFriendsDetails(List<UserFriendsModel> userFriendsModels){
        usersRepository.getFriendsDetails(userFriendsModels);
    }

    @Override
    public void onComplete(List<UserDetailModel> userFirebaseModelList) {
        mutableLiveData.setValue(userFirebaseModelList);
    }

    @Override
    public void onGetFriewnds(List<UserFriendsModel> userFriendsModelList) {
        userFriendsMutableLiveData.setValue(userFriendsModelList);
    }

    @Override
    public void onFriendsDetails(List<UserDetailModel> userDetailModelList) {
        userFriendsDetailsMutableLiveDara.setValue(userDetailModelList);
    }
}
