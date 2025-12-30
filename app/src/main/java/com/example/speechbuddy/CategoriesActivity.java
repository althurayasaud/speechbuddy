package com.example.speechbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CategoriesActivity extends AppCompatActivity {

    private Button btnAnimals, btnColors, btnNumbers, btnFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Choose a category");

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnAnimals = findViewById(R.id.btnAnimals);
        btnColors = findViewById(R.id.btnColors);
        btnNumbers = findViewById(R.id.btnNumbers);
        btnFood = findViewById(R.id.btnFood);
    }

    private void setupListeners() {
        btnAnimals.setOnClickListener(v -> openLearning("animals"));
        btnColors.setOnClickListener(v -> openLearning("colors"));
        btnNumbers.setOnClickListener(v -> openLearning("numbers"));
        btnFood.setOnClickListener(v -> openLearning("food"));
    }

    private void openLearning(String category) {
        Intent intent = new Intent(this, LearningActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}