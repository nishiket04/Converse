package com.nishiket.converse.view.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.nishiket.converse.R;
import com.nishiket.converse.databinding.FragmentLoginBinding;
import com.nishiket.converse.view.user.MainActivity;
import com.nishiket.converse.viewmodel.AuthViewModel;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding fragmentLoginBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater,container,false);
        return fragmentLoginBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


       AuthViewModel viewModel =new ViewModelProvider((ViewModelStoreOwner) this,
                (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AuthViewModel.class);

        fragmentLoginBinding.LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginSignupActivity loginSignupActivity = (LoginSignupActivity) getActivity();
                Intent i = new Intent(loginSignupActivity, MainActivity.class);

                String email= fragmentLoginBinding.mailId.getText().toString(); // email and convert it to string
                String pass=fragmentLoginBinding.editText.getText().toString(); // password and convert it to string

                if(!email.isEmpty() && !pass.isEmpty()){ // if email and password is not empty then it will call viewModel signIn Method
                    viewModel.signIn(email,pass); // signIn method of viewModel

                    // now checking in viewModel MutableLiveData is changed or not
                    viewModel.getFirebaseUserMutableLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
                        @Override
                        public void onChanged(FirebaseUser firebaseUser) { // if its changed
                            if(firebaseUser != null){ // and firebaseUser its not Null then
                                startActivity(i);
                                loginSignupActivity.finish();
                            }
                        }
                    });
                }else { // else user name or password is empty then make a toast
                    Toast.makeText(getContext(), "Enter Email and Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fragmentLoginBinding.signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signupFragment);
            }
        });
    }
}