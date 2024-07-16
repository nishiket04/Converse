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
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.nishiket.converse.ChatApplication;
import com.nishiket.converse.KeyboardUtil;
import com.nishiket.converse.R;
import com.nishiket.converse.adapter.ChatAdapter;
import com.nishiket.converse.databinding.FragmentChatBinding;
import com.nishiket.converse.model.ChatModel;
import com.nishiket.converse.model.UserDetailModel;
import com.nishiket.converse.viewmodel.AddToGroupViewModel;
import com.nishiket.converse.viewmodel.AuthViewModel;
import com.nishiket.converse.viewmodel.ChatsViewModel;
import com.nishiket.converse.viewmodel.UserDataViewModel;

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
    private Boolean newChat = false;
    private Boolean isGroup = false;
    private UserDataViewModel userDataViewModel;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uri = null;
    private ImageView selectedImageView;
    private AddToGroupViewModel addToGroupViewModel;

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
        mSocket.on("joinRoom", onJoinRoom);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
        mSocket.on("new chat", onNewChat);
        mSocket.on("group message", onGroupMessage);

        executorService.execute(() -> {
            Bundle arguments = getArguments();
            if (arguments != null) {
                String name = arguments.getString("name");
                email = arguments.getString("email");
                String image = arguments.getString("image");
                room = arguments.getString("room", null);
                newChat = arguments.getBoolean("new", false);
                isGroup = arguments.getBoolean("isGroup", false);
                if (room != null) {
                    mSocket.emit("joinRoom", room);
                }
                if (isGroup) {
                    chatBinding.addImage.setVisibility(View.VISIBLE);
                    chatBinding.userStatus.setText("");
                }
                requireActivity().runOnUiThread(() -> {
                    chatBinding.userName.setText(name);
                    Glide.with(getContext()).load(image).error(R.drawable.user_image).into(chatBinding.userImgae);

                });
                // Use the retrieved arguments as needed
                Log.d("ChatFragment", "Name: " + name);
                Log.d("ChatFragment", "Email: " + email);
                Log.d("ChatFragment", "UserImage: " + image);
                Log.d("data", "room: " + room);
            }
        });


        addToGroupViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AddToGroupViewModel.class);
        userDataViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(UserDataViewModel.class);
        authViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AuthViewModel.class);
        ChatsViewModel chatsViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ChatsViewModel.class);
        chatAdapter = new ChatAdapter(getActivity());
        if (room != null) {
            if(!isGroup) {
                chatsViewModel.getChats(room, authViewModel.getCurrentUser().getEmail());
            }
            else {
                addToGroupViewModel.getChat(room,authViewModel.getCurrentUser().getEmail());
            }
        }
        chatsViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<ChatModel>>() {
            @Override
            public void onChanged(List<ChatModel> chatModelList) {
                chatModelListGlobal = chatModelList;
                chatBinding.chats.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                chatBinding.chats.setAdapter(chatAdapter);
                chatAdapter.setChatModelList(chatModelListGlobal);
                chatAdapter.notifyDataSetChanged();
                scrollToBottom();
//                mSocket.emit("chat message", "nishiket04@gmail.com","abc04@gmail.com","This is Test Messesage","ZZJvPQhpVcYa3mAtfe3c");
            }
        });

        if(isGroup) {
            addToGroupViewModel.getChatMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<ChatModel>>() {
                @Override
                public void onChanged(List<ChatModel> chatModelList) {
                    chatModelListGlobal = chatModelList;
                    chatBinding.chats.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    chatBinding.chats.setAdapter(chatAdapter);
                    chatAdapter.setChatModelList(chatModelListGlobal);
                    chatAdapter.notifyDataSetChanged();
                    scrollToBottom();
                }
            });
        }

        chatBinding.addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageDilog(authViewModel.getCurrentUser().getEmail(), userDataViewModel);
            }
        });

        chatBinding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newChat) {
                    mSocket.emit("new chat", authViewModel.getCurrentUser().getEmail(), email, chatBinding.sendEdt.getText().toString());
                    ChatModel chatModel = addToList(chatBinding.sendEdt.getText().toString(), authViewModel.getCurrentUser().getEmail(), email);
                    chatBinding.chats.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    chatModelListGlobal.add(chatModel);
                    chatBinding.chats.setAdapter(chatAdapter);
                    chatAdapter.setChatModelList(chatModelListGlobal);
                    chatAdapter.notifyDataSetChanged();
                    chatBinding.sendEdt.setText("");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            userDataViewModel.getNewChatRoom(authViewModel.getCurrentUser().getEmail(), email);
                            userDataViewModel.getRoomMutableLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    room = s;
                                    newChat = false;
                                    mSocket.emit("joinRoom", room);
                                }
                            });
                        }
                    }, 2000);
                } else if (isGroup) {
                    mSocket.emit("group message", authViewModel.getCurrentUser().getEmail(), chatBinding.sendEdt.getText().toString(), room);
                    ChatModel chatModel = addToList(chatBinding.sendEdt.getText().toString(), authViewModel.getCurrentUser().getEmail(),email);
                    chatModelListGlobal.add(chatModel);
                    chatAdapter.setChatModelList(chatModelListGlobal);
                    chatAdapter.notifyItemInserted(chatModelListGlobal.size() - 1);
                    scrollToBottom();
                    chatBinding.sendEdt.setText("");
                } else {
                    mSocket.emit("chat message", authViewModel.getCurrentUser().getEmail(), email, chatBinding.sendEdt.getText().toString(), room);
                    ChatModel chatModel = addToList(chatBinding.sendEdt.getText().toString(), authViewModel.getCurrentUser().getEmail(), email);
                    chatModelListGlobal.add(chatModel);
                    chatAdapter.setChatModelList(chatModelListGlobal);
                    chatAdapter.notifyItemInserted(chatModelListGlobal.size() - 1);
                    scrollToBottom();
                    chatBinding.sendEdt.setText("");
                }
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
                    if (room != null) {
                        mSocket.emit("typing", room);
                    }
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
                chatBinding.nestedScrollView.scrollTo(0, chatBinding.nestedScrollView.getChildAt(0).getBottom());
            }
        });
    }

    private void addTyping() {
        if(!isGroup)
            chatBinding.userStatus.setText("Typing...");
        else
            chatBinding.userStatus.setText("Someone is Typing...");
    }

    private void removeTyping() {
        if(!isGroup)
            chatBinding.userStatus.setText("Online");
        else
            chatBinding.userStatus.setText("");
    }

    private ChatModel addToList(String message, String from, String to) {
        if(!isGroup) {
            ChatModel chatModel = new ChatModel();
            chatModel.setMsg(message);
            chatModel.setFrom(from);
            chatModel.setTo(to);
            Timestamp timestamp = Timestamp.now();
            chatModel.setTime(timestamp);
            chatModel.determineType(authViewModel.getCurrentUser().getEmail());
            return chatModel;
        }
        else {
            ChatModel chatModel = new ChatModel();
            chatModel.setMsg(message);
            chatModel.setFrom(from);
            Timestamp timestamp = Timestamp.now();
            chatModel.setTime(timestamp);
            chatModel.determineGroupType(authViewModel.getCurrentUser().getEmail());
            return chatModel;
        }
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
                            ChatModel chatModel = addToList(message, from, to);
                            chatModelListGlobal.add(chatModel);
                            chatAdapter.setChatModelList(chatModelListGlobal);
                            chatAdapter.notifyItemInserted(chatModelListGlobal.size() - 1);
                            scrollToBottom();
                            Log.d("SocketIO", "Received message: " + message + "  " + data.getString("from"));
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

    private Emitter.Listener onNewChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args.length > 0 && args[0] instanceof JSONObject) {

                    }
                }
            });
        }
    };

    private Emitter.Listener onGroupMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(isAdded() && requireActivity() != null) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (args.length > 0 && args[0] instanceof JSONObject) {
                            JSONObject data = (JSONObject) args[0];
                            String message;
                            String from;
                            try {
                                message = data.getString("message");
                                from = data.getString("from");
                                ChatModel chatModel = addToList(message, from, "");
                                chatModelListGlobal.add(chatModel);
                                chatAdapter.setChatModelList(chatModelListGlobal);
                                chatAdapter.notifyItemInserted(chatModelListGlobal.size() - 1);
                                scrollToBottom();
                                Log.d("SocketIO", "Received message: " + message + "  " + data.getString("from"));
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
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (isAdded() && requireActivity() != null) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (args.length > 0 && args[0] instanceof JSONObject) {
                            JSONObject data = (JSONObject) args[0];
                            addTyping();
                        } else {
                            Log.e("SocketIO", "Received data is not a JSONObject");
                        }
                    }
                });
            }
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (isAdded() && requireActivity() != null) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (args.length > 0 && args[0] instanceof JSONObject) {
                            JSONObject data = (JSONObject) args[0];
                            removeTyping();
                        } else {
                            Log.e("SocketIO", "Received data is not a JSONObject");
                        }
                    }
                });
            }
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
            if (room != null) {
                mSocket.emit("stop typing", room);
            }
        }
    };

    private void openImageDilog(String email, UserDataViewModel userDataViewModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dilog_change_image, null);
        builder.setView(dialogView);

        selectedImageView = dialogView.findViewById(R.id.selected_image);
        Button chooseImageButton = dialogView.findViewById(R.id.choose_image_button);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (uri != null) {
                    addToGroupViewModel.setImage(uri,room);
                    addToGroupViewModel.getBooleanMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if (aBoolean == true) {
                                Glide.with(getActivity()).load(uri).into(chatBinding.userImgae);
                            } else {
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
}