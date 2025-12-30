package com.example.speechbuddy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private SeekBar seekSpeed, seekVolume;
    private TextView tvSpeed, tvVolume;
    private Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        initViews();
        loadSettings();
        setupListeners();
    }

    private void initViews() {
        seekSpeed = findViewById(R.id.seekSpeed);
        seekVolume = findViewById(R.id.seekVolume);
        tvSpeed = findViewById(R.id.tvSpeed);
        tvVolume = findViewById(R.id.tvVolume);
        btnReset = findViewById(R.id.btnReset);
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences("SpeechBuddy", MODE_PRIVATE);
        int speed = prefs.getInt("speed", 75);
        int volume = prefs.getInt("volume", 80);

        seekSpeed.setProgress(speed);
        seekVolume.setProgress(volume);
        tvSpeed.setText(speed + "%");
        tvVolume.setText(volume + "%");
    }

    private void setupListeners() {
        seekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSpeed.setText(progress + "%");
                saveSettings();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvVolume.setText(progress + "%");
                saveSettings();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("SpeechBuddy", MODE_PRIVATE);
                prefs.edit().clear().apply();
                Toast.makeText(SettingsActivity.this,
                        "All data has been reset!", Toast.LENGTH_SHORT).show();
                loadSettings();
            }
        });
    }

    private void saveSettings() {
        SharedPreferences prefs = getSharedPreferences("SpeechBuddy", MODE_PRIVATE);
        prefs.edit()
                .putInt("speed", seekSpeed.getProgress())
                .putInt("volume", seekVolume.getProgress())
                .apply();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}