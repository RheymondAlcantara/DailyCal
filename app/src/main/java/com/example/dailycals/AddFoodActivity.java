package com.example.dailycals;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class AddFoodActivity extends AppCompatActivity {

    EditText etFoodName, etServing, etCalories, etCarbs, etProtein, etFat;
    Spinner spinnerUnit;
    Button btnSaveFood;
    DatabaseHelper db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        // Initialize UI
        etFoodName = findViewById(R.id.etFoodName);
        etServing = findViewById(R.id.etServing);
        etCalories = findViewById(R.id.etCalories);
        etCarbs = findViewById(R.id.etCarbs);
        etProtein = findViewById(R.id.etProtein);
        etFat = findViewById(R.id.etFat);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        btnSaveFood = findViewById(R.id.btnSaveFood);

        // Spinner values
        String[] units = {"gram (g)", "piece", "ml", "cup"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, units);
        spinnerUnit.setAdapter(adapter);

        db = new DatabaseHelper(this);
        userId = getSharedPreferences("MyPrefs", MODE_PRIVATE).getInt("user_id", -1);

        btnSaveFood.setOnClickListener(view -> saveFoodEntry());
    }

    private void saveFoodEntry() {
        String foodName = etFoodName.getText().toString().trim();
        String servingStr = etServing.getText().toString().trim();
        String caloriesStr = etCalories.getText().toString().trim();
        String carbsStr = etCarbs.getText().toString().trim();
        String proteinStr = etProtein.getText().toString().trim();
        String fatStr = etFat.getText().toString().trim();
        String unit = spinnerUnit.getSelectedItem().toString();

        if (foodName.isEmpty() || servingStr.isEmpty() || caloriesStr.isEmpty()
                || carbsStr.isEmpty() || proteinStr.isEmpty() || fatStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int serving = Integer.parseInt(servingStr);
        int calories = Integer.parseInt(caloriesStr);
        int carbs = Integer.parseInt(carbsStr);
        int protein = Integer.parseInt(proteinStr);
        int fat = Integer.parseInt(fatStr);

        boolean success = db.insertFoodItem(userId, foodName, serving, unit, calories, carbs, protein, fat);
        if (success) {
            Toast.makeText(this, "Food saved!", Toast.LENGTH_SHORT).show();

            // Navigate to SavedFoodsActivity
            Intent intent = new Intent(AddFoodActivity.this, SavedFoodsActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error saving food", Toast.LENGTH_SHORT).show();
        }
    }

}
