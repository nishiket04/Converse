package com.nishiket.converse.repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nishiket.converse.R;
import com.nishiket.converse.model.UserDetailModel;
import com.nishiket.converse.model.UserFriendsModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsersRepository {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Application application;
    private FirebaseStorage firebaseStorage =FirebaseStorage.getInstance();
    private firebaseComplte firebaseComplte;
    private final ExecutorService executorService;

    public UsersRepository(Application application,firebaseComplte fi) {
        this.application = application;
        this.firebaseComplte= fi;
        this.executorService = Executors.newFixedThreadPool(10);
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

    public void getUserFriends(String email){
        executorService.execute(()->{
            db.collection("users").document(email).collection("userFriends").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot result = task.getResult();
                        if(result!=null){
                            List<UserFriendsModel> userFriendsModels = task.getResult().toObjects(UserFriendsModel.class);
                            if(firebaseComplte!=null){
                                firebaseComplte.onGetFriewnds(userFriendsModels);
                                Log.d("data", "onComplete f: "+userFriendsModels.size());
                            }
                        }
                    }
                }
            });
        });
    }

    public void getFriendsDetails(List<UserFriendsModel> userFriendsModels){
        executorService.execute(()->{
            List<String> documentIds = new ArrayList<>();
            Map<String, String> lastMessagesMap = new HashMap<>();
            for (UserFriendsModel model : userFriendsModels) {
                documentIds.add(model.getUserId());
                lastMessagesMap.put(model.getUserId(), model.getLastMessage());
//                Log.d("data", "getFriendsDetails: " +model.getUserId() );
            }
            if(!documentIds.isEmpty()) { // if there is no friends then don't run the query
                db.collection("users").whereIn(FieldPath.documentId(), documentIds).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            if (result != null) {
                                List<UserDetailModel> userDetailModelList = task.getResult().toObjects(UserDetailModel.class);
                                for (UserDetailModel userDetailModel : userDetailModelList) {
                                    String lastMessage = lastMessagesMap.get(userDetailModel.getDocumentId());
                                    userDetailModel.setLastMessage(lastMessage);
                                }

                                if (firebaseComplte != null) {
                                    firebaseComplte.onFriendsDetails(userDetailModelList);
                                    Log.d("data", "onComplete fd: " + userDetailModelList.size());
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    public void setUserName(String name, String email){
        executorService.execute(()->{
//            Map<String,Object> map = new HashMap<>();
//            map.put("name",name);
            db.collection("users").document(email).update("name",name).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        if(firebaseComplte!=null){
                            firebaseComplte.onUpdated(true);
                        }
                        Log.d("data","name:"+name);
                    }
                    else {
                        if(firebaseComplte!=null){
                            firebaseComplte.onUpdated(false);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(firebaseComplte!=null){
                        firebaseComplte.onUpdated(false);
                    }
                }
            });
        });
    }

    public void setImage(Uri uri,String email){
        executorService.execute(()->{
            StorageReference ref = firebaseStorage.getReference().child(uri.getLastPathSegment());
            UploadTask uploadTask = ref.putFile(uri);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        getUrl(uri,email);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        });
    }

    private void getUrl(Uri uri,String email){
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference imageRef = storageRef.child(uri.getLastPathSegment());
        // Get the download URL
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                    updateUserImage(uri,email);
            }
        });
    }

    private void updateUserImage(Uri uri,String email){
        if(uri!=null){
            db.collection("users").document(email).update("userImage",uri).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        if(firebaseComplte!=null){
                            firebaseComplte.onUpdated(true);
                        }
                        else{
                            firebaseComplte.onUpdated(false);
                        }
                    }
                }
            });
        }
    }

    public interface firebaseComplte{
        void onComplete(List<UserDetailModel> userDetailModelList);
        void onGetFriewnds(List<UserFriendsModel> userFriendsModelList);
        void onFriendsDetails(List<UserDetailModel> userDetailModelList);
        void onUpdated(Boolean isComplete);
    }
}
