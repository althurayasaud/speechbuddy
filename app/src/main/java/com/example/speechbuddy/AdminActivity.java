package com.example.speechbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

    public class AdminActivity extends AppCompatActivity {

        private TextView tvTotalUsers, tvTotalScore, tvActiveUsers;
        private Button btnViewUsers, btnManageWords, btnReports, btnSettings;
        private LinearLayout usersList;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin);

            // Initialize Toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setBackgroundColor(Color.parseColor("#9C27B0")); // Purple 500
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Admin Dashboard");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setTitleTextColor(Color.parseColor("#FFFFFF")); // White

            initViews();
            loadStatistics();
            setupListeners();
            applyColors();
        }

        private void initViews() {
            tvTotalUsers = findViewById(R.id.tvTotalUsers);
            tvTotalScore = findViewById(R.id.tvTotalScore);
            tvActiveUsers = findViewById(R.id.tvActiveUsers);
            btnViewUsers = findViewById(R.id.btnViewUsers);
            btnManageWords = findViewById(R.id.btnManageWords);
            btnReports = findViewById(R.id.btnReports);
            btnSettings = findViewById(R.id.btnSettings);
            usersList = findViewById(R.id.usersList);
        }

        private void applyColors() {
            // Background color
            findViewById(android.R.id.content).setBackgroundColor(Color.parseColor("#F5F5F5")); // Gray 100

            // Text colors
            tvTotalUsers.setTextColor(Color.parseColor("#9C27B0")); // Purple 500
            tvTotalScore.setTextColor(Color.parseColor("#4CAF50")); // Green 500
            tvActiveUsers.setTextColor(Color.parseColor("#2196F3")); // Blue 500

            // Button colors
            btnViewUsers.setBackgroundColor(Color.parseColor("#2196F3")); // Blue 500
            btnViewUsers.setTextColor(Color.parseColor("#FFFFFF")); // White

            btnManageWords.setBackgroundColor(Color.parseColor("#4CAF50")); // Green 500
            btnManageWords.setTextColor(Color.parseColor("#FFFFFF")); // White

            btnReports.setBackgroundColor(Color.parseColor("#FF9800")); // Orange 500
            btnReports.setTextColor(Color.parseColor("#FFFFFF")); // White

            btnSettings.setBackgroundColor(Color.parseColor("#9E9E9E")); // Gray 500
            btnSettings.setTextColor(Color.parseColor("#FFFFFF")); // White

            // Additional formatting
            btnViewUsers.setAllCaps(false);
            btnManageWords.setAllCaps(false);
            btnReports.setAllCaps(false);
            btnSettings.setAllCaps(false);
        }

        private void loadStatistics() {
            SharedPreferences prefs = getSharedPreferences("SpeechBuddy", MODE_PRIVATE);

            boolean isRegistered = prefs.getBoolean("isRegistered", false);
            int totalUsers = isRegistered ? 1 : 0;
            int totalScore = prefs.getInt("totalScore", 0);
            boolean isActive = prefs.getBoolean("isLoggedIn", false);
            int activeUsers = isActive ? 1 : 0;

            tvTotalUsers.setText(String.valueOf(totalUsers));
            tvTotalScore.setText(String.valueOf(totalScore));
            tvActiveUsers.setText(String.valueOf(activeUsers));

            if (isRegistered) {
                displayUserInfo(prefs);
            }
        }

        private void displayUserInfo(SharedPreferences prefs) {
            String fullName = prefs.getString("fullName", "N/A");
            String email = prefs.getString("email", "N/A");
            String userType = prefs.getString("userType", "user");
            int score = prefs.getInt("totalScore", 0);

            CardView userCard = new CardView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            userCard.setLayoutParams(params);
            userCard.setRadius(16);
            userCard.setCardElevation(4);
            userCard.setCardBackgroundColor(Color.parseColor("#FFFFFF")); // White
            userCard.setContentPadding(16, 16, 16, 16);

            LinearLayout cardContent = new LinearLayout(this);
            cardContent.setOrientation(LinearLayout.VERTICAL);

            TextView tvName = new TextView(this);
            tvName.setText("Name: " + fullName);
            tvName.setTextSize(16);
            tvName.setTextColor(Color.parseColor("#212121")); // Gray 900
            tvName.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView tvEmail = new TextView(this);
            tvEmail.setText("Email: " + email);
            tvEmail.setTextSize(14);
            tvEmail.setTextColor(Color.parseColor("#757575")); // Gray 600

            TextView tvType = new TextView(this);
            String displayType = userType.equals("parent") ? "Parent" :
                    userType.equals("teacher") ? "Teacher" : "User";
            tvType.setText("Type: " + displayType);
            tvType.setTextSize(14);
            tvType.setTextColor(Color.parseColor("#757575")); // Gray 600

            TextView tvScore = new TextView(this);
            tvScore.setText("Score: " + score + " ⭐");
            tvScore.setTextSize(14);
            tvScore.setTextColor(Color.parseColor("#9C27B0")); // Purple 500
            tvScore.setTypeface(null, android.graphics.Typeface.BOLD);

            cardContent.addView(tvName);
            cardContent.addView(tvEmail);
            cardContent.addView(tvType);
            cardContent.addView(tvScore);
            userCard.addView(cardContent);

            usersList.addView(userCard);
        }

        private void setupListeners() {
            btnViewUsers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(com.example.speechbuddy.AdminActivity.this,
                            "View Users clicked", Toast.LENGTH_SHORT).show();
                }
            });

            btnManageWords.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(com.example.speechbuddy.AdminActivity.this,
                            "Manage Words clicked", Toast.LENGTH_SHORT).show();
                }
            });

            btnReports.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(com.example.speechbuddy.AdminActivity.this,
                            "Reports clicked", Toast.LENGTH_SHORT).show();
                }
            });

            btnSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(com.example.speechbuddy.AdminActivity.this,
                            "Settings clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.admin_menu, menu);

            // Color menu items
            MenuItem logoutItem = menu.findItem(R.id.action_logout);
            if (logoutItem != null && logoutItem.getIcon() != null) {
                logoutItem.getIcon().setTint(Color.parseColor("#F44336")); // Red 500
            }

            MenuItem refreshItem = menu.findItem(R.id.action_refresh);
            if (refreshItem != null && refreshItem.getIcon() != null) {
                refreshItem.getIcon().setTint(Color.parseColor("#2196F3")); // Blue 500
            }

            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.action_logout) {
                showLogoutDialog();
                return true;
            }
            else if (id == R.id.action_refresh) {
                loadStatistics();
                Toast.makeText(this, "Data refreshed", Toast.LENGTH_SHORT).show();
                return true;
            }
            else if (id == R.id.action_add_user) {
                showAddUserDialog();
                return true;
            }
            else if (id == R.id.action_settings) {
                openSettings();
                return true;
            }
            else if (id == android.R.id.home) {
                onBackPressed();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        private void showLogoutDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        performLogout();
                    })
                    .setNegativeButton("No", null)
                    .setIcon(android.R.drawable.ic_dialog_alert);

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(Color.parseColor("#F44336")); // Red 500
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(Color.parseColor("#2196F3")); // Blue 500
            });
            dialog.show();
        }

        private void performLogout() {
            SharedPreferences.Editor editor = getSharedPreferences("SpeechBuddy", MODE_PRIVATE).edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            Intent intent = new Intent(com.example.speechbuddy.AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            Toast.makeText(com.example.speechbuddy.AdminActivity.this,
                    "Logged out successfully", Toast.LENGTH_SHORT).show();
        }

        private void showAddUserDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add New User")
                    .setMessage("This feature allows you to add new users to the system.")
                    .setPositiveButton("Add", (dialog, which) -> {
                        Toast.makeText(com.example.speechbuddy.AdminActivity.this,
                                "Adding new user...", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void openSettings() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("System Settings")
                    .setItems(new String[]{
                            "Account Settings",
                            "App Settings",
                            "Notification Settings",
                            "Privacy & Security"
                    }, (dialog, which) -> {
                        String[] options = {
                                "Account Settings",
                                "App Settings",
                                "Notification Settings",
                                "Privacy & Security"
                        };
                        Toast.makeText(com.example.speechbuddy.AdminActivity.this,
                                "Opening: " + options[which], Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        @Override
        protected void onResume() {
            super.onResume();
            loadStatistics();
        }
}