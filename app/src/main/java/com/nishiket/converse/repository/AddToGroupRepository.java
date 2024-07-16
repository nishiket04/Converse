package com.nishiket.converse.repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nishiket.converse.model.ChatModel;
import com.nishiket.converse.model.UserDetailModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddToGroupRepository {
    private Application application;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private onFirebaseComplete onFirebaseComplete;
    private FirebaseStorage firebaseStorage =FirebaseStorage.getInstance();

    public AddToGroupRepository(Application application,onFirebaseComplete onFirebaseComplete) {
        this.application = application;
        this.onFirebaseComplete = onFirebaseComplete;
    }

    public void createGroup(ArrayList<String> users,String name){
        Map<String,Object> list = new HashMap<>();
        list.put("users",users);
        list.put("name",name);
        executorService.execute(()->{
            db.collection("groupChat").add(list).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        if(onFirebaseComplete!=null) {
                            Log.d("grp", "id:" + task.getResult().getId());
                            onFirebaseComplete.onCreatedGroup(task.getResult().getId());
                        }
                    }
                }
            });
        });
    }

    public void addImage(Uri uri,String room){
        executorService.execute(()->{
            StorageReference ref = firebaseStorage.getReference().child(uri.getLastPathSegment());
            UploadTask uploadTask = ref.putFile(uri);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        getUrl(uri,room);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        });
    }

    private void getUrl(Uri uri,String room){
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference imageRef = storageRef.child(uri.getLastPathSegment());
        // Get the download URL
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                updateUserImage(uri,room);
            }
        });
    }
    private void updateUserImage(Uri uri,String room){
        if(uri!=null){
            Map<String,Object> image = new HashMap<>();
            image.put("userImage",uri);
            db.collection("groupChat").document(room).update(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        if(onFirebaseComplete!=null){
                            onFirebaseComplete.onUpdated(true);
                        }
                        else{
                            onFirebaseComplete.onUpdated(false);
                        }
                    }
                }
            });
        }
    }

    public void getGroup(String email){
        executorService.execute(()->{
            db.collection("groupChat").whereArrayContains("users",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult()!=null){
                            List<UserDetailModel> groupModelsList = task.getResult().toObjects(UserDetailModel.class);
                            for (UserDetailModel model:
                                 groupModelsList) {
                                model.setGroup(true);
                                model.setRoom(model.getDocumentId());
                            }
                            if(onFirebaseComplete!=null){
                                onFirebaseComplete.onGetGroup(groupModelsList);
                                Log.d("grp", "onComplete: "+groupModelsList.size());
                            }
                        }
                    }
                }
            });
        });
    }

    public void getChats(String room,String email){
        executorService.execute(()->{
            db.collection("groupChat").document(room).collection("chats").orderBy("time").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult()!=null){
                            List<ChatModel> chatModelList = task.getResult().toObjects(ChatModel.class);
                            for (ChatModel model :
                                    chatModelList) {
                                model.determineGroupType(email);
                            }
                            if(onFirebaseComplete!=null){
                                onFirebaseComplete.onGetChat(chatModelList);
                            }
                        }
                    }
                }
            });
        });
    }

    public interface onFirebaseComplete{
        void onCreatedGroup(String id);
        void onUpdated(Boolean isComplete);
        void onGetGroup(List<UserDetailModel> userDetailModelList);
        void onGetChat(List<ChatModel> chatModelList);
    }
}
