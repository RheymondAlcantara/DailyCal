package com.example.dailycals;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PickFoodActivity extends AppCompatActivity {

    DatabaseHelper db;
    ListView listView;
    int userId;
    String mealType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_food);

        listView = findViewById(R.id.listSavedFoods);
        db = new DatabaseHelper(this);

        userId = getSharedPreferences("MyPrefs", MODE_PRIVATE).getInt("user_id", -1);
        mealType = getIntent().getStringExtra("mealType");
        if (mealType == null) mealType = "Unspecified";

        loadSavedFoods();

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

            String food = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
            int cal = cursor.getInt(cursor.getColumnIndexOrThrow("calories"));
            int carb = cursor.getInt(cursor.getColumnIndexOrThrow("carbs"));
            int prot = cursor.getInt(cursor.getColumnIndexOrThrow("protein"));
            int fat = cursor.getInt(cursor.getColumnIndexOrThrow("fat"));

            db.addFullCalorieEntry(userId, food, cal, carb, prot, fat, mealType);

            Toast.makeText(this, food + " added to " + mealType, Toast.LENGTH_SHORT).show();
            finish(); // Close and return
        });
    }

    private void loadSavedFoods() {
        Cursor cursor = db.getSavedFoods(userId); // Use the correct method

        String[] from = {"food_name", "calories", "carbs", "protein", "fat"};
        int[] to = {
                R.id.tvFoodName,
                R.id.tvCal,
                R.id.tvCarbs,
                R.id.tvProtein,
                R.id.tvFat
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.food_list_item,
                cursor,
                from,
                to,
                0
        );

        listView.setAdapter(adapter);
    }
}
