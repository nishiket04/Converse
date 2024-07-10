package com.nishiket.converse.view.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nishiket.converse.R;
import com.nishiket.converse.databinding.FragmentSettingBinding;
import com.nishiket.converse.model.UserDetailModel;
import com.nishiket.converse.view.login.LoginSignupActivity;
import com.nishiket.converse.viewmodel.AuthViewModel;
import com.nishiket.converse.viewmodel.UserDataViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding settingBinding;
    private ImageView selectedImageView;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    FirebaseStorage storage = FirebaseStorage.getInstance();

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

        userDataViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<UserDetailModel>>() {
            @Override
            public void onChanged(List<UserDetailModel> userDetailModels) {
                    loadUserData(userDetailModels.get(0));
            }
        });
        settingBinding.changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageDilog();
            }
        });

        settingBinding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authViewModel.signOut();
                Intent intent = new Intent(getActivity(), LoginSignupActivity.class);
                intent.putExtra("signin",true);
                startActivity(intent);
                getActivity().finish();
            }
        });

        settingBinding.editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNameDilog();
            }
        });
    }

    private void openNameDilog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dilog_change_name, null);
        builder.setView(dialogView);


        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openImageDilog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dilog_change_image, null);
        builder.setView(dialogView);

        selectedImageView = dialogView.findViewById(R.id.selected_image);
        Button chooseImageButton = dialogView.findViewById(R.id.choose_image_button);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadUserData(UserDetailModel userDetailModel){
        executorService.execute(()->{
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child(userDetailModel.getUserImage());
            // Get the download URL
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    requireActivity().runOnUiThread(()->{
                        settingBinding.userName.setText(userDetailModel.getName());
                        Glide.with(getContext()).load(uri).error(R.drawable.user_image).into(settingBinding.profileImage);
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Glide.with(getContext()).load(R.drawable.user_image).into(settingBinding.profileImage);
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executorService.shutdown();
    }
}