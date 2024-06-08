package com.nishiket.converse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nishiket.converse.databinding.SelfChatBinding;
import com.nishiket.converse.databinding.UserChatBinding;
import com.nishiket.converse.model.ChatModel;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private UserChatBinding userChatBinding;
    private SelfChatBinding selfChatBinding;
    private List<ChatModel> chatModelList = new ArrayList<>();

    public ChatAdapter(Context context) {
        this.context = context;
    }

    public void setChatModelList(List<ChatModel> chatModelList) {
        this.chatModelList = chatModelList;
    }

    @Override
    public int getItemViewType(int position) {
        return chatModelList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            userChatBinding = UserChatBinding.inflate(LayoutInflater.from(context), parent, false);
            return new UserChatViewHolder(userChatBinding);
        } else {
            selfChatBinding = SelfChatBinding.inflate(LayoutInflater.from(context), parent, false);
            return new SelfChatViewHolder(selfChatBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModel chatModel = chatModelList.get(position);
        if (holder instanceof UserChatViewHolder) {
            ((UserChatViewHolder) holder).bind(chatModel);
        } else if (holder instanceof SelfChatViewHolder) {
            ((SelfChatViewHolder) holder).bind(chatModel);
        }
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        public viewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class UserChatViewHolder extends RecyclerView.ViewHolder {
        private final UserChatBinding binding;

        public UserChatViewHolder(UserChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChatModel chatModel) {
            // Bind data to the user chat layout
            binding.message.setText(chatModel.getMessage());
            binding.time.setText(chatModel.getTime());
        }
    }

    public class SelfChatViewHolder extends RecyclerView.ViewHolder {
        private final SelfChatBinding binding;

        public SelfChatViewHolder(SelfChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChatModel chatModel) {
            // Bind data to the self chat layout
            binding.message.setText(chatModel.getMessage());
            binding.time.setText(chatModel.getTime());
        }
    }
}
