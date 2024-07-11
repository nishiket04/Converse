package com.nishiket.converse.view.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.nishiket.converse.KeyboardUtil;
import com.nishiket.converse.R;
import com.nishiket.converse.adapter.ChatAdapter;
import com.nishiket.converse.databinding.FragmentChatBinding;
import com.nishiket.converse.model.ChatModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ChatFragment extends Fragment {
    private FragmentChatBinding chatBinding;
    private List<ChatModel> chatModelList = new ArrayList<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
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


        ChatAdapter chatAdapter = new ChatAdapter(getActivity());
        chatBinding.chats.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        chatBinding.chats.setAdapter(chatAdapter);
        chatAdapter.setChatModelList(chatModelList);
        chatAdapter.notifyDataSetChanged();

        executorService.execute(()->{
            Bundle arguments = getArguments();
            if (arguments != null) {
                String name = arguments.getString("name");
                String email = arguments.getString("email");
                String image = arguments.getString("image");
                requireActivity().runOnUiThread(()->{
                    chatBinding.userName.setText(name);
                    Glide.with(getContext()).load(image).error(R.drawable.user_image).into(chatBinding.userImgae);

                });
                // Use the retrieved arguments as needed
                Log.d("ChatFragment", "Name: " + name);
                Log.d("ChatFragment", "Email: " + email);
                Log.d("ChatFragment", "UserImage: " + image);
            }
        });

    }
}