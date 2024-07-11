package com.nishiket.converse.view.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nishiket.converse.R;
import com.nishiket.converse.adapter.UserChatAdapter;
import com.nishiket.converse.databinding.FragmentNewChatBinding;
import com.nishiket.converse.model.UserChatModel;

import java.util.ArrayList;
import java.util.List;


public class NewChatFragment extends Fragment {
    private FragmentNewChatBinding newChatBinding;
    private List<UserChatModel> userChatModelList = new ArrayList<>();
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

        UserChatAdapter userChatAdapter = new UserChatAdapter(getActivity(),requireActivity());
        newChatBinding.users.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        newChatBinding.users.setAdapter(userChatAdapter);
//        userChatAdapter.setChatModelList(userChatModelList);
//        userChatAdapter.notifyDataSetChanged();

        newChatBinding.addGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_newChatFragment_to_addGroupFragment);
            }
        });
    }
}