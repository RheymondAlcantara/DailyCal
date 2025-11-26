package com.example.dailycals;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    LinearLayout historyContainer;
    DatabaseHelper db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyContainer = findViewById(R.id.historyContainer);
        db = new DatabaseHelper(this);

        SharedPreferences sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);

        loadFoodHistory();
        loadActivityHistory();
    }

    private void loadFoodHistory() {
        Cursor cursor = db.getHistory(userId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String food = cursor.getString(cursor.getColumnIndexOrThrow("food"));
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow("calories"));

                String title = "🍽 Food Log";
                String detail = "🗓 " + date + "\nFood: " + food + "\nCalories: " + calories + " kcal";
                addHistoryCard(title, detail);

            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void loadActivityHistory() {
        Cursor cursor = db.getActivityLog(userId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("activity_type"));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
                int burned = cursor.getInt(cursor.getColumnIndexOrThrow("calories_burned"));

                String title = "🏃 Activity Log";
                String detail = "🗓 " + date + "\nActivity: " + type + "\nDuration: " + duration + "\nCalories Burned: " + burned + " kcal";
                addHistoryCard(title, detail);

            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void addHistoryCard(String title, String detail) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 24, 24, 24);
        card.setElevation(4f);
        card.setBackgroundResource(R.drawable.card_bg_rounded);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 24);
        card.setLayoutParams(params);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(getColor(android.R.color.black));

        TextView tvDetail = new TextView(this);
        tvDetail.setText(detail);
        tvDetail.setTextSize(14);
        tvDetail.setTextColor(getColor(android.R.color.black));
        tvDetail.setPadding(0, 8, 0, 0);

        card.addView(tvTitle);
        card.addView(tvDetail);
        historyContainer.addView(card);
    }
}
