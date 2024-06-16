package com.nishiket.converse;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.nishiket.converse.databinding.ActivitySplashScreenBinding;
import com.nishiket.converse.view.onboarding.OnBoardingActivity;
import com.nishiket.converse.view.user.MainActivity;
import com.nishiket.converse.viewmodel.AuthViewModel;

public class SplashScreenActivity extends AppCompatActivity {
   private ActivitySplashScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // for full screen
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Window window = getWindow(); // get os status bar and navigation bar and screen
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, window.getDecorView()); // gets its view and controller

        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(false); // set font color white
            windowInsetsController.setAppearanceLightNavigationBars(false); // set font color white
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars()); // hide status bar and navigation bar
        }
        AuthViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(AuthViewModel.class);
        Intent i = new Intent(SplashScreenActivity.this, OnBoardingActivity.class);
        Intent i1 = new Intent(SplashScreenActivity.this, MainActivity.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (viewModel.getCurrentUser() != null) { // if there is any user logged In then
                    startActivity(i1); // goto home
                } else {
                    startActivity(i); // goto login page
                }
                finish();
            }
        },3000);
    }
}