package com.example.dailycals;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SavedFoodsActivity extends Activity {

    DatabaseHelper db;
    int userId;
    ArrayList<String> foodList;
    ArrayList<FoodItem> foodItems;
    ListView listView;
    Button btnCancelSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_foods);

        db = new DatabaseHelper(this);
        listView = findViewById(R.id.listViewFoods);
        btnCancelSelect = findViewById(R.id.btnCancelSelect);

        userId = getSharedPreferences("MyPrefs", MODE_PRIVATE).getInt("user_id", -1);
        foodList = new ArrayList<>();
        foodItems = new ArrayList<>();

        Cursor cursor = db.getSavedFoods(userId);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow("calories"));
                int carbs = cursor.getInt(cursor.getColumnIndexOrThrow("carbs"));
                int protein = cursor.getInt(cursor.getColumnIndexOrThrow("protein"));
                int fat = cursor.getInt(cursor.getColumnIndexOrThrow("fat"));

                foodItems.add(new FoodItem(name, calories, carbs, protein, fat));
                foodList.add(name + " - " + calories + " kcal");
            } while (cursor.moveToNext());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, foodList) {
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(android.graphics.Color.BLACK); // set to black
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            FoodItem selected = foodItems.get(position);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("food", selected.name);
            resultIntent.putExtra("calories", selected.calories);
            resultIntent.putExtra("carbs", selected.carbs);
            resultIntent.putExtra("protein", selected.protein);
            resultIntent.putExtra("fat", selected.fat);


            resultIntent.putExtra("mealType", getIntent().getStringExtra("mealType"));

            setResult(RESULT_OK, resultIntent);
            finish();
        });

        btnCancelSelect.setOnClickListener(v -> finish());
    }
}
