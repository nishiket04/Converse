package com.nishiket.converse.view.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.nishiket.converse.R;
import com.nishiket.converse.databinding.FragmentSignupBinding;
import com.nishiket.converse.view.user.MainActivity;
import com.nishiket.converse.viewmodel.AuthViewModel;


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

       AuthViewModel viewModel =new ViewModelProvider((ViewModelStoreOwner) this,
                (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AuthViewModel.class);
        signupBinding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginSignupActivity loginSignupActivity = (LoginSignupActivity) getActivity();
                Intent i = new Intent(loginSignupActivity, MainActivity.class);


                String email= signupBinding.mailId.getText().toString(); // email and convert it to string
                String pass=signupBinding.editText.getText().toString(); // password and convert it to string
                String conPass=signupBinding.editText1.getText().toString(); // confirm password and convert it to string
                String name=signupBinding.name.getText().toString(); // name and convert it to string

                if(!email.isEmpty() && !pass.isEmpty() && !conPass.isEmpty() && !name.isEmpty()) { // if any of the fields are not empty then it will call viewModel signUn Method
                    if (pass.equals(conPass)) { // if the password equals to confirm password
                        viewModel.signUp(email, pass,name); // signUp method of viewModel

                        // now checking in viewModel MutableLiveData is changed or not
                        viewModel.getFirebaseUserMutableLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
                            @Override
                            public void onChanged(FirebaseUser firebaseUser) { // if its changed
                                if (firebaseUser != null) {  // and firebaseUser its not Null then
                                    startActivity(i);
                                    loginSignupActivity.finish();
                                }
                            }
                        });
                    }else { // else make a toast that password doesn't match
                        Toast.makeText(getContext(), "Password doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }else { // else toast to enter all fields
                    Toast.makeText(getContext(), "Enter All Fields", Toast.LENGTH_SHORT).show();
                }
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