package com.nishiket.converse.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nishiket.converse.databinding.AddToGroupLayoutBinding;
import com.nishiket.converse.model.UserDetailModel;

import java.util.ArrayList;
import java.util.List;

public class AddToGroupAdapter extends RecyclerView.Adapter<AddToGroupAdapter.viewHolder> {
    private Context context;
    private List<UserDetailModel> userChatModelList = new ArrayList<>();
    private ArrayList<String> selectedUsers = new ArrayList<>();
    private AddToGroupLayoutBinding binding;
    private GetGroupUsers getGroupUsers;

    public ArrayList<String> getSelectedUsers() {
        return selectedUsers;
    }

    public void setGetGroupUsers(GetGroupUsers getGroupUsers) {
        this.getGroupUsers = getGroupUsers;
    }

    public void setUserChatModelList(List<UserDetailModel> userChatModelList) {
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
        UserDetailModel userChatModel = userChatModelList.get(position);
        binding.userName.setText(userChatModel.getName());
        binding.lastMessage.setText(userChatModel.getLastMessage());
        Glide.with(context).load(userChatModel.getUserImage()).into(binding.userImage);
        binding.activitiChcekBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                userChatModel.setChecked(b);
                if (b) {
                    if (!selectedUsers.contains(userChatModel.getDocumentId())){
                        selectedUsers.add(userChatModel.getDocumentId());
                        Log.d("grp","users:"+userChatModel.getDocumentId());
                    }
                } else {
                    selectedUsers.remove(userChatModel.getDocumentId());
                    Log.d("grp","users:"+userChatModel.getDocumentId());
                }
            }
        });
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

    public interface GetGroupUsers{
        void onClick(List<String> id);
    }
}
