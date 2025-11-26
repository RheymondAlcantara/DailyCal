package com.example.dailycals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


public class ActivityAddActivity extends AppCompatActivity {

    EditText etActivityName, etDuration, etCaloriesBurned;
    Button btnSaveActivity, btnActivityLog;
    DatabaseHelper db;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        etActivityName = findViewById(R.id.etActivityName);
        etDuration = findViewById(R.id.etDuration);
        etCaloriesBurned = findViewById(R.id.etCaloriesBurned);
        btnSaveActivity = findViewById(R.id.btnSaveActivity);
        btnActivityLog = findViewById(R.id.btnActivityLog);
        db = new DatabaseHelper(this);
        sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        btnSaveActivity.setOnClickListener(v -> {
            String name = etActivityName.getText().toString().trim();
            String duration = etDuration.getText().toString().trim();

            if (name.isEmpty() || duration.isEmpty() || etCaloriesBurned.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int calories;
            try {
                calories = Integer.parseInt(etCaloriesBurned.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number for calories", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = sp.getInt("user_id", -1);
            if (userId == -1) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean inserted = db.addActivityLog(userId, name, duration, calories);
            if (inserted) {
                Toast.makeText(this, "Activity saved", Toast.LENGTH_SHORT).show();

                // Start ActivityLogActivity and pass user_id immediately
                Intent intent = new Intent(ActivityAddActivity.this, ActivityLogActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
                finish();  // Close the add activity screen
            } else {
                Toast.makeText(this, "Failed to save activity", Toast.LENGTH_SHORT).show();
            }
        });


        btnActivityLog.setOnClickListener(v -> {
            Toast.makeText(this, "Activity Log clicked!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ActivityAddActivity.this, ActivityLogActivity.class);
            int userId = sp.getInt("user_id", -1);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

    }
}
