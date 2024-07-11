package com.nishiket.converse.view.user;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nishiket.converse.R;
import com.nishiket.converse.adapter.UserChatAdapter;
import com.nishiket.converse.databinding.FragmentHomeBinding;
import com.nishiket.converse.model.UserChatModel;
import com.nishiket.converse.model.UserDetailModel;
import com.nishiket.converse.model.UserFriendsModel;
import com.nishiket.converse.sqlite.Helper;
import com.nishiket.converse.viewmodel.AuthViewModel;
import com.nishiket.converse.viewmodel.UserDataViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements UserChatAdapter.onClickedItem {

    private FragmentHomeBinding fragmentHomeBinding;
    private String room;
    private List<UserChatModel> userChatModelList = new ArrayList<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
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
        Helper helper = new Helper(getActivity());
        helper.getReadableDatabase();

        AuthViewModel authViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AuthViewModel.class);
        UserDataViewModel userDataViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(UserDataViewModel.class);
        userDataViewModel.getUserFriends(authViewModel.getCurrentUser().getEmail());
        userDataViewModel.getUserFriendsMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<UserFriendsModel>>() {
            @Override
            public void onChanged(List<UserFriendsModel> userFriendsModelList) {
                    userDataViewModel.getFriendsDetails(userFriendsModelList);
                    userDataViewModel.getUserFriendsDetailsMutableLiveDara().observe(getViewLifecycleOwner(), new Observer<List<UserDetailModel>>() {
                        @Override
                        public void onChanged(List<UserDetailModel> userDetailModelList) {
                            UserChatAdapter userChatAdapter = new UserChatAdapter(getActivity());
                            fragmentHomeBinding.chats.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                            fragmentHomeBinding.chats.setAdapter(userChatAdapter);
                            userChatAdapter.setOnClickedItem(HomeFragment.this);
                            userChatAdapter.setChatModelList(userDetailModelList);
                            userChatAdapter.notifyDataSetChanged();
                        }
                    });
            }
        });

        fragmentHomeBinding.addChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_newChatFragment);
            }
        });

//        MenuInflater menuInflater = getActivity().getMenuInflater();
//        menuInflater.inflate(R.menu.menu, fragmentHomeBinding.toolbar.getMenu());
        fragmentHomeBinding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.setting) {
                    Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_settingFragment);
                }
                return false;
            }
        });
    }

    @Override
    public void onCliced(int i, UserDetailModel userDetailModel) {
        executorService.execute(()->{
            Helper helper = new Helper(getActivity());
            Cursor cursor = helper.getReadableDatabase().query("chat_rooms",null,"user = ?", new String[]{userDetailModel.getDocumentId()},null,null,null);
            if(cursor.moveToFirst()){
                room = cursor.getString(cursor.getColumnIndexOrThrow("room"));
                Log.d("data", "home room: "+room);
            }
            else {
                room = null;
                Log.d("data", "home room: "+room);
            }
            cursor.close();
            Bundle bundle = new Bundle();
            bundle.putString("name",userDetailModel.getName());
            bundle.putString("email",userDetailModel.getDocumentId());
            bundle.putString("image",userDetailModel.getUserImage());
            bundle.putString("room",room);
            requireActivity().runOnUiThread(()->{
                Navigation.findNavController(fragmentHomeBinding.getRoot()).navigate(R.id.action_homeFragment_to_chatFragment,bundle);
            });
        });
    }
}