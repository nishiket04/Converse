package com.nishiket.converse.view.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nishiket.converse.R;
import com.nishiket.converse.adapter.UserChatAdapter;
import com.nishiket.converse.databinding.FragmentNewChatBinding;
import com.nishiket.converse.model.UserChatModel;
import com.nishiket.converse.model.UserDetailModel;
import com.nishiket.converse.viewmodel.AuthViewModel;
import com.nishiket.converse.viewmodel.NewChatAndGroupViewModel;

import java.util.ArrayList;
import java.util.List;


public class NewChatFragment extends Fragment implements UserChatAdapter.onClickedItem {
    private FragmentNewChatBinding newChatBinding;
//    private List<UserChatModel> userChatModelList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        newChatBinding = FragmentNewChatBinding.inflate(inflater,container,false);
        return newChatBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AuthViewModel authViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AuthViewModel.class);
        NewChatAndGroupViewModel newChatAndGroupViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(NewChatAndGroupViewModel.class);

        newChatAndGroupViewModel.getUsers(authViewModel.getCurrentUser().getEmail());
        newChatAndGroupViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<UserDetailModel>>() {
            @Override
            public void onChanged(List<UserDetailModel> userDetailModelList) {
                UserChatAdapter userChatAdapter = new UserChatAdapter(getActivity());
                newChatBinding.users.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                newChatBinding.users.setAdapter(userChatAdapter);
                userChatAdapter.setChatModelList(userDetailModelList);
                userChatAdapter.setOnClickedItem(NewChatFragment.this);
                userChatAdapter.notifyDataSetChanged();
            }
        });

        newChatBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });


        newChatBinding.addGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_newChatFragment_to_addGroupFragment);
            }
        });
    }

    @Override
    public void onCliced(int i, UserDetailModel userDetailModel) {
        Bundle bundle = new Bundle();
        bundle.putString("name",userDetailModel.getName());
        bundle.putString("email",userDetailModel.getDocumentId());
        bundle.putString("image",userDetailModel.getUserImage());
        bundle.putBoolean("new",true);
        Navigation.findNavController(newChatBinding.getRoot()).navigate(R.id.action_newChatFragment_to_chatFragment,bundle);
    }
}