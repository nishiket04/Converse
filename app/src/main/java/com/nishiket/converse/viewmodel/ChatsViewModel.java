package com.nishiket.converse.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.nishiket.converse.model.ChatModel;
import com.nishiket.converse.repository.ChatsRepository;

import java.util.List;

public class ChatsViewModel extends AndroidViewModel implements ChatsRepository.firebaseComplte {

    private MutableLiveData<List<ChatModel>> mutableLiveData = new MutableLiveData<>();
    private ChatsRepository chatsRepository;

    public MutableLiveData<List<ChatModel>> getMutableLiveData() {
        return mutableLiveData;
    }

    public ChatsViewModel(@NonNull Application application) {
        super(application);
        chatsRepository = new ChatsRepository(application,this);
    }

    public void getChats(String room, String email){
        chatsRepository.getChats(room, email);
    }

    @Override
    public void onGetChat(List<ChatModel> chatModelList) {
            mutableLiveData.setValue(chatModelList);
    }
}
