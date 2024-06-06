package com.nishiket.converse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nishiket.converse.databinding.UserChatUiBinding;
import com.nishiket.converse.model.ChatModel;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.viewHolder> {
    private List<ChatModel> chatModelList = new ArrayList<>();
    private Context context;
    private UserChatUiBinding binding;

    public ChatAdapter(Context context) {
        this.context = context;
    }

    public void setChatModelList(List<ChatModel> chatModelList) {
        this.chatModelList = chatModelList;
    }

    @NonNull
    @Override
    public ChatAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = UserChatUiBinding.inflate(LayoutInflater.from(context), parent, false);
        return new viewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.viewHolder holder, int position) {
        ChatModel chatModel = chatModelList.get(position);
        binding.userName.setText(chatModel.getName());
        binding.lastMessage.setText(chatModel.getLastMessage());
        Glide.with(context).load(chatModel.getImage()).into(binding.userImage);
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private UserChatUiBinding binding;
        public viewHolder(@NonNull UserChatUiBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
