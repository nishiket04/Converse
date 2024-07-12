package com.nishiket.converse.view.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nishiket.converse.R;
import com.nishiket.converse.adapter.AddToGroupAdapter;
import com.nishiket.converse.adapter.UserChatAdapter;
import com.nishiket.converse.databinding.FragmentAddGroupBinding;
import com.nishiket.converse.model.ChatModel;
import com.nishiket.converse.model.UserChatModel;
import com.nishiket.converse.model.UserDetailModel;
import com.nishiket.converse.model.UserFriendsModel;
import com.nishiket.converse.viewmodel.AuthViewModel;
import com.nishiket.converse.viewmodel.UserDataViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddGroupFragment extends Fragment {
    private FragmentAddGroupBinding addGroupBinding;
    private List<UserChatModel> userChatModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addGroupBinding = FragmentAddGroupBinding.inflate(inflater,container,false);
        return addGroupBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserDataViewModel userDataViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(UserDataViewModel.class);
        AuthViewModel authViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AuthViewModel.class);

        try{ // don't know why but this method is invoking when we are in LoginSignUpActivity.. so i put it in a try catch block, also invoking from onbaring when we try to go to LoginSignupActivity
            userDataViewModel.getUserFriends(authViewModel.getCurrentUser().getEmail());
        }catch (Exception e){
            Log.d("data", "onViewCreated: "+e.toString());
        }

        userDataViewModel.getUserFriendsMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<UserFriendsModel>>() {
            @Override
            public void onChanged(List<UserFriendsModel> userFriendsModelList) {
                userDataViewModel.getFriendsDetails(userFriendsModelList);
                userDataViewModel.getUserFriendsDetailsMutableLiveDara().observe(getViewLifecycleOwner(), new Observer<List<UserDetailModel>>() {
                    @Override
                    public void onChanged(List<UserDetailModel> userDetailModelList) {
                        AddToGroupAdapter userChatAdapter = new AddToGroupAdapter(getActivity());
                        addGroupBinding.users.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        addGroupBinding.users.setAdapter(userChatAdapter);
                        userChatAdapter.setUserChatModelList(userDetailModelList);
                        userChatAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}