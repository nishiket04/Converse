package com.nishiket.converse.view.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.nishiket.converse.R;
import com.nishiket.converse.databinding.ActivityLoginSignupBinding;

public class LoginSignupActivity extends AppCompatActivity {
    private ActivityLoginSignupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginSignupBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Window window = getWindow();
        WindowInsetsControllerCompat windowInsetsControllerCompat = new WindowInsetsControllerCompat(window,window.getDecorView());
        if(windowInsetsControllerCompat != null){
            windowInsetsControllerCompat.setAppearanceLightStatusBars(false);
            windowInsetsControllerCompat.setAppearanceLightNavigationBars(false);
        }
        Intent i = getIntent();
        SharedPreferences data = getSharedPreferences("data",MODE_PRIVATE);
        boolean signin = i.getBooleanExtra("signin",false);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        NavController navController = navHostFragment.getNavController();
        if(signin){
            data.edit().putBoolean("second",true).commit();
            navController.navigate(R.id.loginFragment);
        }
        else {
            data.edit().putBoolean("second", true).commit();
            navController.navigate(R.id.signupFragment);
        }

    }
}