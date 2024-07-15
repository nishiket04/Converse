package com.nishiket.converse.view.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatFragment extends Fragment {
    private FragmentChatBinding chatBinding;
    private Socket mSocket;
    private AuthViewModel authViewModel;
    private List<ChatModel> chatModelListGlobal = new ArrayList<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String room;
    private String email;
    private Handler mTypingHandler = new Handler();
    private boolean mTyping = false;
    private ChatAdapter chatAdapter;
    private static final int TYPING_TIMER_LENGTH = 600;
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
        mSocket.on("typing",onTyping);
        mSocket.on("stop typing",onStopTyping);

        executorService.execute(()->{
            Bundle arguments = getArguments();
            if (arguments != null) {
                String name = arguments.getString("name");
                email = arguments.getString("email");
                String image = arguments.getString("image");
                room = arguments.getString("room",null);
                mSocket.emit("joinRoom",room);
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


         authViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AuthViewModel.class);
        ChatsViewModel chatsViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ChatsViewModel.class);
        chatAdapter = new ChatAdapter(getActivity());
        if(room!=null) {
            chatsViewModel.getChats(room, authViewModel.getCurrentUser().getEmail());
        }
        chatsViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<ChatModel>>() {
            @Override
            public void onChanged(List<ChatModel> chatModelList) {
                chatModelListGlobal = chatModelList;
                chatBinding.chats.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                chatBinding.chats.setAdapter(chatAdapter);
                chatAdapter.setChatModelList(chatModelListGlobal);
                chatAdapter.notifyDataSetChanged();
                scrollToBottom();
//                mSocket.emit("chat message", "nishiket04@gmail.com","abc04@gmail.com","This is Test Messesage","ZZJvPQhpVcYa3mAtfe3c");
            }
        });

        chatBinding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSocket.emit("chat message", authViewModel.getCurrentUser().getEmail(),email,chatBinding.sendEdt.getText().toString(),room);
                ChatModel chatModel = addToList(chatBinding.sendEdt.getText().toString(),authViewModel.getCurrentUser().getEmail(),email);
                chatModelListGlobal.add(chatModel);
                chatAdapter.setChatModelList(chatModelListGlobal);
                chatAdapter.notifyItemInserted(chatModelListGlobal.size()-1);
                scrollToBottom();
                chatBinding.sendEdt.setText("");
            }
        });

        chatBinding.sendEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!mSocket.connected()) return;

                if (!mTyping) {
                    mTyping = true;
                    mSocket.emit("typing",room);
                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void scrollToBottom() {
//        chatBinding.chats.smoothScrollToPosition(chatAdapter.getItemCount()-1);
        chatBinding.chats.post(new Runnable() {
            @Override
            public void run() {
                chatBinding.nestedScrollView.scrollTo(0,chatBinding.nestedScrollView.getChildAt(0).getBottom());
            }
        });
    }

    private void addTyping() {
        chatBinding.userStatus.setText("Typing...");
    }

    private void removeTyping() {
        chatBinding.userStatus.setText("Online");
    }

    private ChatModel addToList(String message,String from,String to){
        ChatModel chatModel = new ChatModel();
        chatModel.setMsg(message);
        chatModel.setFrom(from);
        chatModel.setTo(to);
        Timestamp timestamp = Timestamp.now();
        chatModel.setTime(timestamp);
        chatModel.determineType(authViewModel.getCurrentUser().getEmail());
        return chatModel;
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
                        String from;
                        String to;
                        try {
                            message = data.getString("message");
                            from = data.getString("from");
                            to = data.getString("to");
                            ChatModel chatModel = addToList(message,from,to);
                            chatModelListGlobal.add(chatModel);
                            chatAdapter.setChatModelList(chatModelListGlobal);
                            chatAdapter.notifyItemInserted(chatModelListGlobal.size()-1);
                            scrollToBottom();
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

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(args.length>0 && args[0] instanceof JSONObject){
                        JSONObject data = (JSONObject) args[0];
                        addTyping();
                    }
                    else {
                        Log.e("SocketIO", "Received data is not a JSONObject");
                    }
                }
            });
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(args.length>0 && args[0] instanceof JSONObject){
                        JSONObject data = (JSONObject) args[0];
                        removeTyping();
                    }
                    else {
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

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            mSocket.emit("stop typing",room);
        }
    };
}