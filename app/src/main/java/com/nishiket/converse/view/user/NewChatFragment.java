package com.nishiket.converse.view.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nishiket.converse.R;
import com.nishiket.converse.adapter.ChatAdapter;
import com.nishiket.converse.databinding.FragmentNewChatBinding;
import com.nishiket.converse.model.ChatModel;

import java.util.ArrayList;
import java.util.List;


public class NewChatFragment extends Fragment {
    private FragmentNewChatBinding newChatBinding;
    private List<ChatModel> chatModelList = new ArrayList<>();
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

        ChatAdapter chatAdapter = new ChatAdapter(getActivity());
        newChatBinding.users.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        newChatBinding.users.setAdapter(chatAdapter);
        chatAdapter.setChatModelList(chatModelList);
        chatAdapter.notifyDataSetChanged();

        newChatBinding.addGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_newChatFragment_to_chatFragment);
            }
        });
    }
}