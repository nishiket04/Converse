package com.nishiket.converse.view.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nishiket.converse.KeyboardUtil;
import com.nishiket.converse.R;
import com.nishiket.converse.adapter.ChatAdapter;
import com.nishiket.converse.databinding.FragmentChatBinding;
import com.nishiket.converse.model.ChatModel;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {
    private FragmentChatBinding chatBinding;
    private List<ChatModel> chatModelList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        chatBinding = FragmentChatBinding.inflate(inflater, container, false);
        return chatBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KeyboardUtil.adjustResize(getActivity(), chatBinding.getRoot());

        ChatModel c1 = new ChatModel();
        ChatModel c2 = new ChatModel();
        ChatModel c3 = new ChatModel();
        ChatModel c4 = new ChatModel();
        ChatModel c5 = new ChatModel();
        ChatModel c6 = new ChatModel();
        ChatModel c7 = new ChatModel();
        ChatModel c8 = new ChatModel();
        ChatModel c9 = new ChatModel();
        ChatModel c10 = new ChatModel();
        ChatModel c11 = new ChatModel();
        ChatModel c12 = new ChatModel();

        c1.setMessage("Hi");
        c2.setMessage("Hey");
        c3.setMessage("kjbdsjfb");
        c4.setMessage("blash");
        c5.setMessage("go go go");
        c6.setMessage("nice");
        c7.setMessage("yes i know");
        c8.setMessage("so");
        c9.setMessage("nothing");
        c10.setMessage("ohh");
        c11.setMessage("yah i see");
        c12.setMessage("kjbdsjfb bsdifbfiusd bisdbfiusd biudsbfiusd budsbisd bisdbivsd bisdubvisd biudsbj bdibfisdu bisbdfifbs bibsdif biusbd f ibiusdbdfjns iubvissdb ind vijbvviusdbvuhb");

        c1.setTime("11:35");
        c2.setTime("1:35");
        c3.setTime("1:45");
        c4.setTime("21:35");
        c5.setTime("4:30");
        c6.setTime("1:35");
        c7.setTime("12:30");
        c8.setTime("11:50");
        c9.setTime("1:50");
        c10.setTime("6:35");
        c11.setTime("7:30");
        c12.setTime("1:60");

        c1.setType(0);
        c2.setType(1);
        c3.setType(0);
        c4.setType(1);
        c5.setType(1);
        c6.setType(0);
        c7.setType(0);
        c8.setType(1);
        c9.setType(0);
        c10.setType(1);
        c11.setType(0);
        c12.setType(1);

        chatModelList.add(c1);
        chatModelList.add(c2);
        chatModelList.add(c3);
        chatModelList.add(c4);
        chatModelList.add(c5);
        chatModelList.add(c6);
        chatModelList.add(c7);
        chatModelList.add(c8);
        chatModelList.add(c9);
        chatModelList.add(c10);
        chatModelList.add(c11);
        chatModelList.add(c12);
        chatModelList.add(c1);
        chatModelList.add(c1);
        chatModelList.add(c5);
        chatModelList.add(c12);


        ChatAdapter chatAdapter = new ChatAdapter(getActivity());
        chatBinding.chats.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        chatBinding.chats.setAdapter(chatAdapter);
        chatAdapter.setChatModelList(chatModelList);
        chatAdapter.notifyDataSetChanged();

    }
}