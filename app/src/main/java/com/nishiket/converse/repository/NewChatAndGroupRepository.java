package com.nishiket.converse.repository;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nishiket.converse.model.UserDetailModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewChatAndGroupRepository {
    private Application application;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private firebaseComplete firebaseComplete;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public NewChatAndGroupRepository(Application application,firebaseComplete firebaseComplete) {
        this.application = application;
        this.firebaseComplete = firebaseComplete;
    }

    public void getAllUsers(String email){
        executorService.execute(()->{
            db.collection("users").whereNotEqualTo(FieldPath.documentId(),email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult()!=null){
                                List<UserDetailModel> userDetailModelList = task.getResult().toObjects(UserDetailModel.class);
                                if(firebaseComplete!=null){
                                    firebaseComplete.onGetUsers(userDetailModelList);
                                }
                            }
                        }
                }
            });
        });
    }

    public interface firebaseComplete{
        void onGetUsers(List<UserDetailModel> userDetailModelList);
    }
}
