package com.nishiket.converse.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.nishiket.converse.model.UserDetailModel;
import com.nishiket.converse.repository.AddToGroupRepository;

import java.util.ArrayList;
import java.util.List;

public class AddToGroupViewModel extends AndroidViewModel implements AddToGroupRepository.onFirebaseComplete {
    private AddToGroupRepository addToGroupRepository;
    private MutableLiveData<String> groupId = new MutableLiveData<>();
    private MutableLiveData<Boolean> booleanMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<UserDetailModel>> mutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<UserDetailModel>> getMutableLiveData() {
        return mutableLiveData;
    }

    public MutableLiveData<Boolean> getBooleanMutableLiveData() {
        return booleanMutableLiveData;
    }

    public MutableLiveData<String> getGroupId() {
        return groupId;
    }

    public AddToGroupViewModel(@NonNull Application application) {
        super(application);
        addToGroupRepository = new AddToGroupRepository(application,this);
    }

    public void createGroup(ArrayList<String> users,String name){
        addToGroupRepository.createGroup(users,name);
    }

    public void setImage(Uri uri,String room){
        addToGroupRepository.addImage(uri, room);
    }

    @Override
    public void onCreatedGroup(String id) {
        groupId.setValue(id);
    }

    @Override
    public void onUpdated(Boolean isComplete) {
        booleanMutableLiveData.setValue(isComplete);
    }

    public void getGroups(String email){
        addToGroupRepository.getGroup(email);
    }

    @Override
    public void onGetGroup(List<UserDetailModel> userDetailModelList) {
        mutableLiveData.setValue(userDetailModelList);
    }
}
