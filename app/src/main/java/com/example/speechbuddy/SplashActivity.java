package com.example.speechbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3500; // 3.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.d("SplashActivity", "Splash screen started");


        SharedPreferences prefs = getSharedPreferences("SpeechBuddy", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        new Handler().postDelayed(() -> {
            Intent intent;
            if (isLoggedIn) {
                Log.d("SplashActivity", "User is logged in. Going to MainActivity");
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                Log.d("SplashActivity", "User not logged in. Going to LoginActivity");
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
