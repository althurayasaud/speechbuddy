package com.example.speechbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private TextView tvPrompt;
    private Button btnStart, btnProgress, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        setupListeners();
        loadUserInfo();
        loadWordFromApi();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvPrompt = findViewById(R.id.tvPrompt);
        btnStart = findViewById(R.id.btnStart);
        btnProgress = findViewById(R.id.btnProgress);
        btnSettings = findViewById(R.id.btnSettings);
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getSharedPreferences("SpeechBuddy", MODE_PRIVATE);
        String fullName = prefs.getString("fullName", getString(R.string.student));
        tvWelcome.setText(getString(R.string.welcome_message, fullName)); // ✅ Fixed
    }

    private void setupListeners() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);
                startActivity(intent);
            }
        });

        btnProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProgressActivity.class);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    // ================= API INTEGRATION =================

    private void loadWordFromApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://random-word-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WordApi api = retrofit.create(WordApi.class);

        api.getRandomWord().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String word = response.body().get(0);
                    tvPrompt.setText(getString(R.string.say_word, word)); // ✅ Fixed
                } else {
                    tvPrompt.setText(getString(R.string.no_word)); // ✅ Fixed
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                tvPrompt.setText(getString(R.string.failed_load)); // ✅ Fixed
            }
        });
    }
}
