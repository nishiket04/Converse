package com.nishiket.converse.view.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nishiket.converse.R;
import com.nishiket.converse.adapter.ChatAdapter;
import com.nishiket.converse.databinding.FragmentHomeBinding;
import com.nishiket.converse.model.ChatModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding fragmentHomeBinding;
    private List<ChatModel> chatModelList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return fragmentHomeBinding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ChatAdapter chatAdapter = new ChatAdapter(getActivity());
        fragmentHomeBinding.chats.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        fragmentHomeBinding.chats.setAdapter(chatAdapter);
        chatAdapter.setChatModelList(chatModelList);
        chatAdapter.notifyDataSetChanged();
    }
}