package com.nishiket.converse.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nishiket.converse.model.UserDetailModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsersRepository {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Application application;
    private firebaseComplte firebaseComplte;
    private final ExecutorService executorService;

    public UsersRepository(Application application,firebaseComplte fi) {
        this.application = application;
        this.firebaseComplte= fi;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void getUserDetail(String email){
        executorService.execute(()-> {
            db.collection("users").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot result = task.getResult();
                        if (result!=null) {
                            List<UserDetailModel> userDetailModelList = new ArrayList<>();
                            userDetailModelList.add(task.getResult().toObject(UserDetailModel.class));
                            if (firebaseComplte != null) {
                                firebaseComplte.onComplete(userDetailModelList);
                                Log.d("data", "onComplete: "+userDetailModelList.get(0));
                            }
                            Log.d("data", "getActiveComplain: " + task.getResult().getMetadata());
                        }

                    }
                    else {
                        Log.e("data", "Error getting active complains", task.getException());
                    }
                }
            });
        });
    }

    public interface firebaseComplte{
        void onComplete(List<UserDetailModel> userDetailModelList);
    }
}
