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

import com.bumptech.glide.Glide;
import com.nishiket.converse.ChatApplication;
import com.nishiket.converse.KeyboardUtil;
import com.nishiket.converse.R;
import com.nishiket.converse.adapter.ChatAdapter;
import com.nishiket.converse.databinding.FragmentChatBinding;
import com.nishiket.converse.model.ChatModel;
import com.nishiket.converse.viewmodel.AuthViewModel;
import com.nishiket.converse.viewmodel.ChatsViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatFragment extends Fragment {
    private FragmentChatBinding chatBinding;
    private Socket mSocket;
//    private List<ChatModel> chatModelList = new ArrayList<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String room;
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
        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();

        mSocket.on("chat message", onNewMessage);
        mSocket.on("joinRoom",onJoinRoom);

        executorService.execute(()->{
            Bundle arguments = getArguments();
            if (arguments != null) {
                String name = arguments.getString("name");
                String email = arguments.getString("email");
                String image = arguments.getString("image");
                room = arguments.getString("room",null);
//                mSocket.emit("joinRoom",room);
                requireActivity().runOnUiThread(()->{
                    chatBinding.userName.setText(name);
                    Glide.with(getContext()).load(image).error(R.drawable.user_image).into(chatBinding.userImgae);

                });
                // Use the retrieved arguments as needed
                Log.d("ChatFragment", "Name: " + name);
                Log.d("ChatFragment", "Email: " + email);
                Log.d("ChatFragment", "UserImage: " + image);
                Log.d("data", "room: "+room);
            }
        });


        AuthViewModel authViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AuthViewModel.class);
        ChatsViewModel chatsViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ChatsViewModel.class);

        if(room!=null) {
            chatsViewModel.getChats(room, authViewModel.getCurrentUser().getEmail());
        }
        chatsViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<ChatModel>>() {
            @Override
            public void onChanged(List<ChatModel> chatModelList) {
                ChatAdapter chatAdapter = new ChatAdapter(getActivity());
                chatBinding.chats.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                chatBinding.chats.setAdapter(chatAdapter);
                chatAdapter.setChatModelList(chatModelList);
                chatAdapter.notifyDataSetChanged();
//                mSocket.emit("chat message", "nishiket04@gmail.com","abc04@gmail.com","4","ZZJvPQhpVcYa3mAtfe3c");
            }
        });
    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args.length > 0 && args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        String message;
                        try {
                            message = data.getString("message");
                            Log.d("SocketIO", "Received message: " + message+ "  " +data.getString("from"));
                            // Handle the message as needed
                        } catch (JSONException e) {
                            Log.e("SocketIO", "JSON parsing error: " + e.getMessage());
                        }
                    } else {
                        Log.e("SocketIO", "Received data is not a JSONObject");
                    }
                }
            });
        }
    };

    private Emitter.Listener onJoinRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args.length > 0 && args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        String message;
                        try {
                            message = data.getString("room");
                            Log.d("SocketIO", "Received message: " + message);
                            // Handle the message as needed
                        } catch (JSONException e) {
                            Log.e("SocketIO", "JSON parsing error: " + e.getMessage());
                        }
                    } else {
                        Log.e("SocketIO", "Received data is not a JSONObject");
                    }
                }
            });
        }
    };
}