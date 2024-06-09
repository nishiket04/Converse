package com.nishiket.converse.view.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nishiket.converse.R;
import com.nishiket.converse.adapter.AddToGroupAdapter;
import com.nishiket.converse.adapter.UserChatAdapter;
import com.nishiket.converse.databinding.FragmentAddGroupBinding;
import com.nishiket.converse.model.ChatModel;
import com.nishiket.converse.model.UserChatModel;

import java.util.ArrayList;
import java.util.List;

public class AddGroupFragment extends Fragment {
    private FragmentAddGroupBinding addGroupBinding;
    private List<UserChatModel> userChatModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addGroupBinding = FragmentAddGroupBinding.inflate(inflater,container,false);
        return addGroupBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        UserChatModel c1 = new UserChatModel();
//        UserChatModel c2 = new UserChatModel();
//        UserChatModel c3 = new UserChatModel();
//
//        c1.setName("Nishiket");
//        c2.setName("Nishiket2");
//        c3.setName("Nishiket3");
//
//        c1.setLastMessage("Hi!");
//        c2.setLastMessage("Hi!");
//        c3.setLastMessage("Hi!");
//
//        c1.setImage("https://miro.medium.com/v2/resize:fit:450/1*9dbWWY4LzLIkjEHvDf4bDQ.jpeg");
//        c2.setImage("https://miro.medium.com/v2/resize:fit:450/1*9dbWWY4LzLIkjEHvDf4bDQ.jpeg");
//        c3.setImage("https://miro.medium.com/v2/resize:fit:450/1*9dbWWY4LzLIkjEHvDf4bDQ.jpeg");
//
//        userChatModelList.add(c1);
//        userChatModelList.add(c2);
//        userChatModelList.add(c3);
//        userChatModelList.add(c1);
//        userChatModelList.add(c3);
//        userChatModelList.add(c2);
//        userChatModelList.add(c3);
//        userChatModelList.add(c1);
//        userChatModelList.add(c2);
//        userChatModelList.add(c3);
//        userChatModelList.add(c1);
//        userChatModelList.add(c1);
//        userChatModelList.add(c2);
//        userChatModelList.add(c2);
//        userChatModelList.add(c2);

        AddToGroupAdapter userChatAdapter = new AddToGroupAdapter(getActivity());
        addGroupBinding.users.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        addGroupBinding.users.setAdapter(userChatAdapter);
        userChatAdapter.setUserChatModelList(userChatModelList);
        userChatAdapter.notifyDataSetChanged();
    }
}