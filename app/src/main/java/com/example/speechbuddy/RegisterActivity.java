package com.example.speechbuddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;

// ✅ Firebase imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilFullName, tilEmail, tilPhone, tilPassword, tilConfirmPassword;
    private EditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword;
    private RadioGroup rgUserType;
    private RadioButton rbParent;
    private Button btnRegister;
    private TextView tvLogin;

    // ✅ Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // ✅ Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPhone = findViewById(R.id.tilPhone);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        rgUserType = findViewById(R.id.rgUserType);
        rbParent = findViewById(R.id.rbParent);

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                performRegistration();
            }
        });

        tvLogin.setOnClickListener(v -> finish());
    }

    // ✅ Check internet connection (API 23+ only)
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate Full Name
        String fullName = etFullName.getText().toString().trim();
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Full name required");
            isValid = false;
        } else if (fullName.length() < 3) {
            tilFullName.setError("The name must be at least 3 letters long.");
            isValid = false;
        } else if (!fullName.matches("^[a-zA-Z\\u0621-\\u064A\\s]+$")) {
            tilFullName.setError("The name must contain only letters.");
            isValid = false;
        } else {
            tilFullName.setError(null);
        }

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

        // Validate Phone
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("Phone number required");
            isValid = false;
        } else if (!Patterns.PHONE.matcher(phone).matches()) {
            tilPhone.setError("Invalid phone number");
            isValid = false;
        } else if (phone.length() < 8) {
            tilPhone.setError("The phone number must be at least 8 digits long.");
            isValid = false;
        } else {
            tilPhone.setError(null);
        }

        // Validate Password
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("The password must be at least 6 characters long.");
            isValid = false;
        } else if (!password.matches(".*[A-Z].*")) {
            tilPassword.setError("The password must contain at least one capital letter.");
            isValid = false;
        } else if (!password.matches(".*[0-9].*")) {
            tilPassword.setError("The password must contain at least one number.");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        // Validate Confirm Password
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Password confirmation is required");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("The password does not match");
            isValid = false;
        } else {
            tilConfirmPassword.setError(null);
        }

        // Validate User Type
        if (rgUserType.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a user type", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    // ✅ Firebase Registration with network check
    private void performRegistration() {
        // ✅ Check internet connection before registration
        if (!isNetworkAvailable()) {
            Toast.makeText(this,
                    "No internet connection. Please check your network and try again.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String userType = rbParent.isChecked() ? "parent" : "teacher";

        // Show progress
        setRegisterButtonState(false, "Registering...");

        // Create user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();
                            saveUserToFirestore(userId, fullName, email, phone, userType);
                        } else {
                            handleRegistrationError("User creation failed");
                        }
                    } else {
                        String errorMessage = getRegistrationErrorMessage(task.getException());
                        handleRegistrationError(errorMessage);
                    }
                });
    }

    // ✅ Extract method for error messages
    private String getRegistrationErrorMessage(Exception exception) {
        String errorMessage = "Registration failed";
        if (exception != null) {
            String error = exception.getMessage();
            if (error != null) {
                if (error.contains("email address is already in use")) {
                    errorMessage = "This email is already registered";
                } else if (error.contains("network")) {
                    errorMessage = "Network error. Please try again.";
                } else if (error.contains("weak-password")) {
                    errorMessage = "Password is too weak";
                } else {
                    errorMessage = error;
                }
            }
        }
        return errorMessage;
    }

    // ✅ Save user data to Firestore
    private void saveUserToFirestore(String userId, String fullName, String email,
                                     String phone, String userType) {
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("email", email);
        user.put("phone", phone);
        user.put("userType", userType);
        user.put("totalScore", 0);
        user.put("createdAt", System.currentTimeMillis());

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    saveUserToPreferences(userId, fullName, email, phone, userType);
                    Toast.makeText(RegisterActivity.this,
                            "Registration successful! You can now log in",
                            Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    String errorMsg = "Error saving user data";
                    if (e.getMessage() != null && e.getMessage().contains("offline")) {
                        errorMsg = "No internet connection. Please try again.";
                    } else if (e.getMessage() != null) {
                        errorMsg = e.getMessage();
                    }
                    handleRegistrationError(errorMsg);
                });
    }

    // ✅ Save user to SharedPreferences
    private void saveUserToPreferences(String userId, String fullName, String email,
                                       String phone, String userType) {
        SharedPreferences prefs = getSharedPreferences("SpeechBuddy", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId", userId);
        editor.putString("fullName", fullName);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.putString("userType", userType);
        editor.putInt("totalScore", 0);
        editor.putBoolean("isRegistered", true);
        editor.apply();
    }

    // ✅ Handle registration errors
    private void handleRegistrationError(String errorMessage) {
        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        setRegisterButtonState(true, "Register");
    }

    // ✅ Set button state (extracted to avoid string literals in setText)
    private void setRegisterButtonState(boolean enabled, String text) {
        btnRegister.setEnabled(enabled);
        btnRegister.setText(text);
    }
}