package com.example.speechbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//  Firebase imports
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LearningActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView tvCategory, tvWord, tvScore, tvFeedback;
    private Button btnListen, btnSpeak, btnNext;
    private ImageView ivWordImage;
    private LinearLayout wordsContainer;

    private TextToSpeech textToSpeech;
    private int score = 0;
    private int currentWordIndex = 0;
    private String currentCategory;
    private WordItem currentWordItem;

    private Map<String, List<WordItem>> wordCategories;
    private List<WordItem> currentWordList;

    private final int SPEECH_REQUEST_CODE = 1001;

    //  Firebase variable
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        currentCategory = getIntent().getStringExtra("category");
        if(currentCategory == null) currentCategory = "animals";

        //  Initialize Firebase
        db = FirebaseFirestore.getInstance();

        initViews();
        initData();
        initTextToSpeech();
        loadWord();
        setupListeners();
    }

    private void initViews() {
        tvCategory = findViewById(R.id.tvCategory);
        tvWord = findViewById(R.id.tvWord);
        tvScore = findViewById(R.id.tvScore);
        tvFeedback = findViewById(R.id.tvFeedback);
        btnListen = findViewById(R.id.btnListen);
        btnSpeak = findViewById(R.id.btnSpeak);
        btnNext = findViewById(R.id.btnNext);
        ivWordImage = findViewById(R.id.ivWordImage);
        wordsContainer = findViewById(R.id.wordsContainer);
    }

    private void initData() {
        wordCategories = new HashMap<>();


        List<WordItem> animals = new ArrayList<>();
        animals.add(new WordItem("Cat", R.drawable.animal_cat));
        animals.add(new WordItem("Dog", R.drawable.animal_dog));
        animals.add(new WordItem("Lion", R.drawable.animal_lion));
        animals.add(new WordItem("Bird", R.drawable.animal_bird));
        animals.add(new WordItem("Fish", R.drawable.animal_fish));
        animals.add(new WordItem("Monkey", R.drawable.animal_monkey));
        wordCategories.put("animals", animals);


        List<WordItem> colors = new ArrayList<>();
        colors.add(new WordItem("Blue", R.drawable.color_blue));
        colors.add(new WordItem("Green", R.drawable.color_green));
        colors.add(new WordItem("Yellow", R.drawable.color_yellow));
        colors.add(new WordItem("Orange", R.drawable.color_orange));

        wordCategories.put("colors", colors);


        List<WordItem> numbers = new ArrayList<>();
        numbers.add(new WordItem("One", R.drawable.number_one));
        numbers.add(new WordItem("Two", R.drawable.number_two));
        numbers.add(new WordItem("Three", R.drawable.number_three));
        numbers.add(new WordItem("Four", R.drawable.number_four));
        numbers.add(new WordItem("Five", R.drawable.number_five));
        wordCategories.put("numbers", numbers);


        List<WordItem> food = new ArrayList<>();
        food.add(new WordItem("Apple", R.drawable.food_apple));
        food.add(new WordItem("Banana", R.drawable.food_banana));
        food.add(new WordItem("Milk", R.drawable.food_milk));
        food.add(new WordItem("Cake", R.drawable.food_cake));
        wordCategories.put("food", food);

        currentWordList = wordCategories.get(currentCategory);
        tvCategory.setText("Category: " + capitalize(currentCategory));
    }

    private void setupListeners() {
        btnListen.setOnClickListener(v -> playWordSound());
        btnSpeak.setOnClickListener(v -> startSpeechRecognition());
        btnNext.setOnClickListener(v -> nextWord());
    }

    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(this, this);
    }

    private void loadWord() {
        if(currentWordList == null || currentWordList.isEmpty()) return;
        if(currentWordIndex >= currentWordList.size()) currentWordIndex = 0;

        currentWordItem = currentWordList.get(currentWordIndex);

        tvWord.setText(currentWordItem.word);
        ivWordImage.setImageResource(currentWordItem.imageResId);
        tvScore.setText("Score: " + score);
        tvFeedback.setText("");

        updateOtherWords();
    }

    private void playWordSound() {
        if(textToSpeech != null && currentWordItem != null){
            textToSpeech.speak(currentWordItem.word, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void nextWord() {
        currentWordIndex++;
        if(currentWordIndex >= currentWordList.size()) currentWordIndex = 0;
        loadWord();
    }

    private void updateOtherWords() {
        wordsContainer.removeAllViews();
        for(int i=0; i<currentWordList.size(); i++){
            if(i == currentWordIndex) continue;
            WordItem w = currentWordList.get(i);
            Button btn = new Button(this);
            btn.setText(w.word);
            btn.setAllCaps(false);
            btn.setBackgroundColor(Color.parseColor("#9C27B0"));
            btn.setTextColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0,0,0,8);
            btn.setLayoutParams(params);

            int index = i;
            btn.setOnClickListener(v -> {
                currentWordIndex = index;
                loadWord();
            });
            wordsContainer.addView(btn);
        }
    }

    // === Speech Recognition ===
    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Speech recognition not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(result != null && !result.isEmpty()){
                String spokenWord = result.get(0);
                checkPronunciation(spokenWord);
            }
        }
    }

    //  Firebase
    private void checkPronunciation(String spokenWord){
        if(spokenWord.equalsIgnoreCase(currentWordItem.word)){
            score += 10;
            tvScore.setText("Score: " + score);
            tvFeedback.setText("Perfect! Great job! 🎉");
            tvFeedback.setTextColor(Color.parseColor("#4CAF50"));

            // Save score locally in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("SpeechBuddy", MODE_PRIVATE);
            int totalScore = prefs.getInt("totalScore", 0);
            prefs.edit().putInt("totalScore", totalScore + 10).apply();

            // ✅ Save score to Firebase Firestore
            String userId = prefs.getString("userId", "");
            if (!userId.isEmpty()) {
                // Use FieldValue.increment to add 10 to the current totalScore
                db.collection("users").document(userId)
                        .update("totalScore", FieldValue.increment(10))
                        .addOnSuccessListener(aVoid -> {
                            // Score updated successfully in Firebase
                            // Optionally show a success message or log
                        })
                        .addOnFailureListener(e -> {
                            // Handle error silently or log it
                            // The score is still saved locally in SharedPreferences
                        });
            }

        } else {
            tvFeedback.setText("Try again! You can do it! 💪");
            tvFeedback.setTextColor(Color.parseColor("#F44336"));
        }
    }
    // ==========================

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            textToSpeech.setLanguage(Locale.US);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private String capitalize(String text){
        if(text == null || text.isEmpty()) return text;
        return text.substring(0,1).toUpperCase() + text.substring(1);
    }

    private static class WordItem {
        String word;
        int imageResId;

        WordItem(String word, int imageResId){
            this.word = word;
            this.imageResId = imageResId;
        }
    }
}
