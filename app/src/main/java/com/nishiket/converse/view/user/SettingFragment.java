package com.nishiket.converse.view.user;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nishiket.converse.ChatApplication;
import com.nishiket.converse.R;
import com.nishiket.converse.TopicUtils;
import com.nishiket.converse.databinding.FragmentSettingBinding;
import com.nishiket.converse.model.UserDetailModel;
import com.nishiket.converse.view.login.LoginSignupActivity;
import com.nishiket.converse.view.onboarding.OnBoardingActivity;
import com.nishiket.converse.viewmodel.AuthViewModel;
import com.nishiket.converse.viewmodel.UserDataViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.socket.client.Socket;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding settingBinding;
    private ImageView selectedImageView;
    private Uri uri = null;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private Socket mSocket;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        settingBinding = FragmentSettingBinding.inflate(inflater,container,false);
        return settingBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AuthViewModel authViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AuthViewModel.class);
        UserDataViewModel userDataViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(UserDataViewModel.class);
        userDataViewModel.getUserDetail(authViewModel.getCurrentUser().getEmail());
        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();

        userDataViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<UserDetailModel>>() {
            @Override
            public void onChanged(List<UserDetailModel> userDetailModels) {
                    loadUserData(userDetailModels.get(0));
            }
        });
        settingBinding.changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageDilog(authViewModel.getCurrentUser().getEmail(),userDataViewModel);
            }
        });

        settingBinding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authViewModel.signOut();
                mSocket.disconnect();
                Intent intent = new Intent(getActivity(), LoginSignupActivity.class);
                intent.putExtra("signin",true);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(TopicUtils.sanitizeTopicName(authViewModel.getCurrentUser().getEmail()));
                startActivity(intent);
                getActivity().finish();
            }
        });

        settingBinding.editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNameDilog(authViewModel.getCurrentUser().getEmail(),userDataViewModel);
            }
        });
    }

    private void openNameDilog(String email,UserDataViewModel userDataViewModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dilog_change_name, null);
        builder.setView(dialogView);
        EditText newName = dialogView.findViewById(R.id.newName);


        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = newName.getText().toString();
                if(!name.isEmpty()){
                    userDataViewModel.setUserName(name,email);
                    userDataViewModel.getBooleanMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if(aBoolean == true){
                                userDataViewModel.getUserDetail(email);
                                userDataViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<UserDetailModel>>() {
                                    @Override
                                    public void onChanged(List<UserDetailModel> userDetailModels) {
                                        loadUserData(userDetailModels.get(0));
                                    }
                                });
                            }
                            else {
                                Toast.makeText(getActivity(), "Somting Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openImageDilog(String email,UserDataViewModel userDataViewModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dilog_change_image, null);
        builder.setView(dialogView);

        selectedImageView = dialogView.findViewById(R.id.selected_image);
        Button chooseImageButton = dialogView.findViewById(R.id.choose_image_button);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_MEDIA_IMAGES)
                            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)!=PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED) {

                        // Permission is not granted
                        // Request the permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,Manifest.permission.POST_NOTIFICATIONS},
                                REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
                    }
                    else {
                        openGallery();
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_MEDIA_IMAGES)
                            != PackageManager.PERMISSION_GRANTED ) {

                        // Permission is not granted
                        // Request the permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
                    }
                    else {
                        openGallery();
                    }
                }else {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Permission is not granted
                        // Request the permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
                    }
                    else {
                        openGallery();
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(uri!=null) {
                    userDataViewModel.setUserImage(uri,email);
                    userDataViewModel.getBooleanMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if(aBoolean == true){
                                userDataViewModel.getUserDetail(email);
                                userDataViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<UserDetailModel>>() {
                                    @Override
                                    public void onChanged(List<UserDetailModel> userDetailModels) {
                                        loadUserData(userDetailModels.get(0));
                                    }
                                });
                            }
                            else {
                                Toast.makeText(getActivity(), "Somting Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadUserData(UserDetailModel userDetailModel){
        settingBinding.userName.setText(userDetailModel.getName());
        Glide.with(getContext()).load(userDetailModel.getUserImage()).error(R.drawable.user_image).into(settingBinding.profileImage);

    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            uri = data.getData();
            Glide.with(getContext()).load(uri).into(selectedImageView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executorService.shutdown();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if request code matches our request
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            // Check if the permission has been granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}