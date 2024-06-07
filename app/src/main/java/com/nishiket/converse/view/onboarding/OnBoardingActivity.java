package com.nishiket.converse.view.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nishiket.converse.R;
import com.nishiket.converse.databinding.ActivityOnBoardingBinding;
import com.nishiket.converse.view.login.LoginSignupActivity;

public class OnBoardingActivity extends AppCompatActivity {
    private ActivityOnBoardingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnBoardingBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent i = new Intent(this, LoginSignupActivity.class);
        SharedPreferences data = getSharedPreferences("data",MODE_PRIVATE);
        boolean second = data.getBoolean("second",false);

        //if user in not opening this app for first time then redirect to signin
        if (second){
            i.putExtra("signin",true);
            startActivity(i); // passing activity
            finish(); // after reaching sigInUp activity destroy this activity
        }

        binding.startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("signin",false);
                startActivity(i);
                finish();
            }
        });
    }
}