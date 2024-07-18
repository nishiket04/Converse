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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.nishiket.converse.R;
import com.nishiket.converse.TopicUtils;
import com.nishiket.converse.adapter.UserChatAdapter;
import com.nishiket.converse.databinding.FragmentHomeBinding;
import com.nishiket.converse.model.UserChatModel;
import com.nishiket.converse.model.UserDetailModel;
import com.nishiket.converse.model.UserFriendsModel;
import com.nishiket.converse.viewmodel.AddToGroupViewModel;
import com.nishiket.converse.viewmodel.AuthViewModel;
import com.nishiket.converse.viewmodel.UserDataViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements UserChatAdapter.onClickedItem {

    private FragmentHomeBinding fragmentHomeBinding;
    private String room;
    private boolean firstCall = true;
    private List<UserDetailModel> userDetailModelListGlobal = new ArrayList<>();
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

        AuthViewModel authViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AuthViewModel.class);
        UserDataViewModel userDataViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(UserDataViewModel.class);
        AddToGroupViewModel addToGroupViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AddToGroupViewModel.class);
        try{ // don't know why but this method is invoking when we are in LoginSignUpActivity.. so i put it in a try catch block, also invoking from onbaring when we try to go to LoginSignupActivity
            if(firstCall) {
                userDataViewModel.getUserFriends(authViewModel.getCurrentUser().getEmail());
                addToGroupViewModel.getGroups(authViewModel.getCurrentUser().getEmail());
                firstCall = false;
            }
            FirebaseMessaging.getInstance().subscribeToTopic(TopicUtils.sanitizeTopicName(authViewModel.getCurrentUser().getEmail()));
        }catch (Exception e){
            Log.d("data", "onViewCreated: "+e.toString());
        }

        fragmentHomeBinding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userDataViewModel.getUserFriends(authViewModel.getCurrentUser().getEmail());
                addToGroupViewModel.getGroups(authViewModel.getCurrentUser().getEmail());
                Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();
                fragmentHomeBinding.refreshLayout.setRefreshing(false);
            }
        });

        UserChatAdapter userChatAdapter = new UserChatAdapter(getActivity());
        fragmentHomeBinding.chats.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        fragmentHomeBinding.chats.setAdapter(userChatAdapter);
        userChatAdapter.setOnClickedItem(HomeFragment.this);
        userDataViewModel.getUserFriendsMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<UserFriendsModel>>() {
            @Override
            public void onChanged(List<UserFriendsModel> userFriendsModelList) {
                    userDetailModelListGlobal.removeAll(userDetailModelListGlobal);
                    userDetailModelListGlobal.clear();
                    userChatAdapter.notifyDataSetChanged();
                    userDataViewModel.getFriendsDetails(userFriendsModelList);
                    userDataViewModel.getUserFriendsDetailsMutableLiveDara().observe(getViewLifecycleOwner(), new Observer<List<UserDetailModel>>() {
                        @Override
                        public void onChanged(List<UserDetailModel> userDetailModelList) {
                            userDetailModelListGlobal = userDetailModelList;
                            userChatAdapter.setChatModelList(userDetailModelListGlobal);
                            userChatAdapter.notifyDataSetChanged();
//                            Log.d("data1", "onChanged1: "+userDetailModelListGlobal.get(0).getName());
                            addToGroupViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<UserDetailModel>>() {
                                @Override
                                public void onChanged(List<UserDetailModel> userDetailModelList) {
                                    if(!userDetailModelListGlobal.containsAll(userDetailModelList)) {
                                        userDetailModelListGlobal.addAll(userDetailModelList);
                                        userChatAdapter.setChatModelList(userDetailModelListGlobal);
                                        userChatAdapter.notifyDataSetChanged();
                                        Log.d("data1", "onChanged2: "+userDetailModelListGlobal.get(0).getName());
                                        Log.d("data1", "onChanged3: "+userDetailModelList.get(0).getName());
                                        userChatAdapter.notifyItemChanged(0,userDetailModelListGlobal.get(0));
//                                        userChatAdapter.notifyItemRangeInserted(userDetailModelListGlobal.size() - userDetailModelList.size() - 1, userDetailModelListGlobal.size() - 1);
                                    }
                                }
                            });
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

//    @Override
//    public void onStop() {
//        super.onStop();
//        userDetailModelListGlobal.removeAll(userDetailModelListGlobal);
//        userDetailModelListGlobal.clear();
//    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        userDetailModelListGlobal.removeAll(userDetailModelListGlobal);
//        userDetailModelListGlobal.clear();
//    }

    @Override
    public void onCliced(int i, UserDetailModel userDetailModel) {
        executorService.execute(()->{
            Bundle bundle = new Bundle();
            bundle.putString("name",userDetailModel.getName());
            bundle.putString("email",userDetailModel.getDocumentId());
            bundle.putString("image",userDetailModel.getUserImage());
            bundle.putString("room",userDetailModel.getRoom());
            bundle.putBoolean("isGroup",userDetailModel.isGroup());
            bundle.putString("status",userDetailModel.getStatus());
            requireActivity().runOnUiThread(()->{
                Navigation.findNavController(fragmentHomeBinding.getRoot()).navigate(R.id.action_homeFragment_to_chatFragment,bundle);
            });
        });
    }
}