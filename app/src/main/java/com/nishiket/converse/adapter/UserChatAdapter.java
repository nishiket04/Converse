package com.nishiket.converse.adapter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nishiket.converse.R;
import com.nishiket.converse.databinding.UserChatUiBinding;
import com.nishiket.converse.model.UserChatModel;
import com.nishiket.converse.model.UserDetailModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.viewHolder> implements Filterable {
    private List<UserDetailModel> userChatModelList = new ArrayList<>();
    private List<UserDetailModel> userChatModelListFull = new ArrayList<>();
    private Context context;
    private onClickedItem onClickedItem;
    private UserChatUiBinding binding;

    public UserChatAdapter(Context context) {
        this.context = context;
    }

    public void setOnClickedItem(UserChatAdapter.onClickedItem onClickedItem) {
        this.onClickedItem = onClickedItem;
    }

    @Override
    public Filter getFilter() {
        return onFilter;
    }

    public interface onClickedItem{
        void onCliced(int i,UserDetailModel userDetailModel);
    }

    public void setChatModelList(List<UserDetailModel> userChatModelList) {
//        this.userChatModelList = userChatModelList;
        this.userChatModelList = userChatModelList;
        this.userChatModelListFull = new ArrayList<>(userChatModelList);
    }

    @NonNull
    @Override
    public UserChatAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = UserChatUiBinding.inflate(LayoutInflater.from(context), parent, false);
        return new viewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChatAdapter.viewHolder holder, int position) {
        UserDetailModel userChatModel = userChatModelList.get(position);
        binding.userName.setText(userChatModel.getName());
        Glide.with(context).load(userChatModel.getUserImage()).into(binding.userImage);
        binding.lastMessage.setText(userChatModel.getLastMessage());
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickedItem.onCliced(holder.getAdapterPosition(),userChatModelList.get(holder.getAdapterPosition()));
//                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_chatFragment);
            }
        });
//        Glide.with(context).load(userChatModel.getUserImage()).into(binding.userImage);
    }

    private Filter onFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<UserDetailModel> userFilter = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                userFilter.addAll(userChatModelListFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (UserDetailModel item : userChatModelList) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        userFilter.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = userFilter;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            userChatModelList.clear();
            userChatModelList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

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
