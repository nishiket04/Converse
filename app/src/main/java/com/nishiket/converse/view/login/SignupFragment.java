package com.nishiket.converse.view.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nishiket.converse.R;
import com.nishiket.converse.databinding.FragmentSignupBinding;
import com.nishiket.converse.view.user.MainActivity;


public class SignupFragment extends Fragment {

    private FragmentSignupBinding signupBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        signupBinding = FragmentSignupBinding.inflate(inflater,container,false);
        return signupBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signupBinding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginSignupActivity loginSignupActivity = (LoginSignupActivity) getActivity();
                Intent i = new Intent(loginSignupActivity, MainActivity.class);
                startActivity(i);
                loginSignupActivity.finish();
            }
        });

        signupBinding.logInTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_loginFragment);
            }
        });

    }
}