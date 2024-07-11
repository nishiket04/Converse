package com.nishiket.converse.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nishiket.converse.model.ChatModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatsRepository {
    private Application application;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private firebaseComplte firebaseComplte;

    public ChatsRepository(Application application,firebaseComplte firebaseComplte) {
        this.application = application;
        this.firebaseComplte=firebaseComplte;
    }

    public void getChats(String room,String email){
        executorService.execute(()->{
            db.collection("chatRooms").document(room).collection("chats").orderBy("time").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if(task.getResult()!=null) {
                            List<ChatModel> chats = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ChatModel chat = document.toObject(ChatModel.class);
                                chat.determineType(email);
                                chats.add(chat);
                            }
                            if (firebaseComplte != null) {
                                firebaseComplte.onGetChat(chats);
                                Log.d("data", "chats: " + chats.size());

                            }
                        }
                    } else {
                        // Handle the error
                    }
                }
            });
        });
    }

    public interface firebaseComplte{
        void onGetChat(List<ChatModel> chatModelList);
    }
}
