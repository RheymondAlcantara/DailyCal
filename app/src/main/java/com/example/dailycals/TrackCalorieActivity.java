package com.example.dailycals;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.content.Intent;

public class TrackCalorieActivity extends AppCompatActivity {

    EditText etFood, etCalories;
    Button btnAdd, btnFoodLog, btnFoodList;
    DatabaseHelper db;
    int userId;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_calorie);

        etFood = findViewById(R.id.etFood);
        etCalories = findViewById(R.id.etCalories);
        btnAdd = findViewById(R.id.btnAdd);
        btnFoodLog = findViewById(R.id.btnFoodLog);
        btnFoodList = findViewById(R.id.btnFoodList);

        db = new DatabaseHelper(this);
        sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);

        btnAdd.setOnClickListener(v -> {
            String food = etFood.getText().toString();
            String calStr = etCalories.getText().toString();

            if (!food.isEmpty() && !calStr.isEmpty()) {
                int calories = Integer.parseInt(calStr);
                db.addCalorie(userId, food, calories);
                etFood.setText("");
                etCalories.setText("");
            }
        });

        btnFoodLog.setOnClickListener(v -> {
            Intent intent = new Intent(TrackCalorieActivity.this, AddFoodActivity.class);
            startActivity(intent);
        });


        btnFoodList.setOnClickListener(v -> {
            Intent intent = new Intent(TrackCalorieActivity.this, SavedFoodsActivity.class);
            startActivity(intent);
        });
    }
}
