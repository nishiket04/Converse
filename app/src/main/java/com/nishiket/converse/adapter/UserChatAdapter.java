package com.nishiket.converse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nishiket.converse.databinding.UserChatUiBinding;
import com.nishiket.converse.model.UserChatModel;

import java.util.ArrayList;
import java.util.List;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.viewHolder> {
    private List<UserChatModel> userChatModelList = new ArrayList<>();
    private Context context;
    private UserChatUiBinding binding;

    public UserChatAdapter(Context context) {
        this.context = context;
    }

    public void setChatModelList(List<UserChatModel> userChatModelList) {
        this.userChatModelList = userChatModelList;
    }

    @NonNull
    @Override
    public UserChatAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = UserChatUiBinding.inflate(LayoutInflater.from(context), parent, false);
        return new viewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChatAdapter.viewHolder holder, int position) {
        UserChatModel userChatModel = userChatModelList.get(position);
        binding.userName.setText(userChatModel.getName());
        binding.lastMessage.setText(userChatModel.getLastMessage());
        Glide.with(context).load(userChatModel.getImage()).into(binding.userImage);
    }

    @Override
    public int getItemCount() {
        return userChatModelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private UserChatUiBinding binding;
        public viewHolder(@NonNull UserChatUiBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
