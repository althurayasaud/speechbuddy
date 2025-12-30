package com.example.speechbuddy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProgressActivity extends AppCompatActivity {

    private TextView tvTotalScore, tvLevel, tvStars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your progress");

        initViews();
        loadProgress();
    }

    private void initViews() {
        tvTotalScore = findViewById(R.id.tvTotalScore);
        tvLevel = findViewById(R.id.tvLevel);
        tvStars = findViewById(R.id.tvStars);
    }

    private void loadProgress() {
        SharedPreferences prefs = getSharedPreferences("SpeechBuddy", MODE_PRIVATE);
        int totalScore = prefs.getInt("totalScore", 0);

        tvTotalScore.setText(String.valueOf(totalScore));

        int level = (totalScore / 100) + 1;
        tvLevel.setText("Level " + level);

        int stars = Math.min(5, totalScore / 50);
        StringBuilder starsText = new StringBuilder();
        for (int i = 0; i < stars; i++) {
            starsText.append("⭐");
        }
        tvStars.setText(starsText.toString());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}