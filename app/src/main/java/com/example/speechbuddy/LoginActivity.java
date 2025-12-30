package com.example.speechbuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;

// ✅ Firebase imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;

    // ✅ Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ✅ Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();

        // ✅ Load saved email if exists
        loadSavedEmail();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    // ✅ Load saved email from SharedPreferences
    private void loadSavedEmail() {
        SharedPreferences prefs = getSharedPreferences("SpeechBuddy", MODE_PRIVATE);
        String savedEmail = prefs.getString("savedEmail", "");
        if (!TextUtils.isEmpty(savedEmail)) {
            etEmail.setText(savedEmail);
        }
    }

    // ✅ Save email to SharedPreferences
    private void saveEmail(String email) {
        SharedPreferences prefs = getSharedPreferences("SpeechBuddy", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("savedEmail", email);
        editor.apply();
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            if (validateInputs()) {
                performLogin();
            }
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Forgot Password - Firebase
        tvForgotPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginActivity.this,
                        "Please enter your email address first",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Check internet connection
            if (isOffline()) {
                Toast.makeText(LoginActivity.this,
                        "No internet connection",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Send password reset email via Firebase
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Password reset email sent to: " + email,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            String errorMessage = "Error sending reset email";
                            if (task.getException() != null) {
                                errorMessage = task.getException().getMessage();
                            }
                            Toast.makeText(LoginActivity.this,
                                    errorMessage,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    // ✅ Check if device is offline
    private boolean isOffline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities == null ||
                    (!capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) &&
                            !capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return true;
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate Email
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email is incorrect");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        // Validate Password
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        return isValid;
    }

    // ✅ Login with Firebase
    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Check for admin credentials
        if (email.equals("admin@speechbuddy.com") && password.equals("admin123")) {
            Toast.makeText(this, "Administrator login successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // ✅ Check internet connection before login
        if (isOffline()) {
            Toast.makeText(this,
                    "No internet connection. Please check your network and try again.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Show progress
        setLoginButtonState(false, "Logging in...");

        // Sign in with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful - Save email
                        saveEmail(email);

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            loadUserData(user.getUid());
                        }
                    } else {
                        // Login failed
                        String errorMessage = getLoginErrorMessage(task.getException());
                        handleLoginError(errorMessage);
                    }
                });
    }

    // ✅ Extract method for error messages
    private String getLoginErrorMessage(Exception exception) {
        String errorMessage = "Login failed";
        if (exception != null) {
            String error = exception.getMessage();
            if (error != null) {
                if (error.contains("no user record") || error.contains("user not found")) {
                    errorMessage = "This account does not exist";
                } else if (error.contains("password is invalid") || error.contains("wrong-password")) {
                    errorMessage = "Incorrect password";
                } else if (error.contains("network")) {
                    errorMessage = "Network error";
                } else {
                    errorMessage = error;
                }
            }
        }
        return errorMessage;
    }

    // ✅ Load user data from Firestore
    private void loadUserData(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Save user data to SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("SpeechBuddy", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString("userId", userId);
                        editor.putString("fullName", documentSnapshot.getString("fullName"));
                        editor.putString("email", documentSnapshot.getString("email"));
                        editor.putString("phone", documentSnapshot.getString("phone"));
                        editor.putString("userType", documentSnapshot.getString("userType"));

                        // Get totalScore (handle both Long and Integer)
                        Long totalScoreLong = documentSnapshot.getLong("totalScore");
                        int totalScore = (totalScoreLong != null) ? totalScoreLong.intValue() : 0;
                        editor.putInt("totalScore", totalScore);

                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                        mAuth.signOut(); // Sign out if no user data
                        setLoginButtonState(true, "Login");
                    }
                })
                .addOnFailureListener(e -> {
                    String errorMsg = "Error loading user data";
                    if (e.getMessage() != null && e.getMessage().contains("offline")) {
                        errorMsg = "No internet connection. Please try again.";
                    }
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                    mAuth.signOut(); // Sign out on failure
                    setLoginButtonState(true, "Login");
                });
    }

    // ✅ Handle login errors
    private void handleLoginError(String errorMessage) {
        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        setLoginButtonState(true, "Login");
    }

    // ✅ Set button state (extracted to avoid string literals in setText)
    private void setLoginButtonState(boolean enabled, String text) {
        btnLogin.setEnabled(enabled);
        btnLogin.setText(text);
    }
}














