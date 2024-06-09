package com.nishiket.converse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nishiket.converse.databinding.AddToGroupLayoutBinding;
import com.nishiket.converse.model.UserChatModel;

import java.util.ArrayList;
import java.util.List;

public class AddToGroupAdapter extends RecyclerView.Adapter<AddToGroupAdapter.viewHolder> {
    private Context context;
    private List<UserChatModel> userChatModelList = new ArrayList<>();
    private AddToGroupLayoutBinding binding;

    public void setUserChatModelList(List<UserChatModel> userChatModelList) {
        this.userChatModelList = userChatModelList;
    }

    public AddToGroupAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AddToGroupAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = AddToGroupLayoutBinding.inflate(LayoutInflater.from(context),parent,false);
        return new viewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddToGroupAdapter.viewHolder holder, int position) {
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
        public viewHolder(@NonNull AddToGroupLayoutBinding itemView) {
            super(itemView.getRoot());
        }
    }
}
