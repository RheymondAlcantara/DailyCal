package com.example.dailycals;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ActivityLogActivity extends AppCompatActivity {

    ListView listViewActivities;
    DatabaseHelper dbHelper;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        dbHelper = new DatabaseHelper(this);
        listViewActivities = findViewById(R.id.listViewActivities);

        userId = getSharedPreferences("MyPrefs", MODE_PRIVATE).getInt("user_id", -1);
        if (userId == -1) {
            finish();
            return;
        }

        loadActivityLog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadActivityLog();
    }

    private void loadActivityLog() {
        Cursor cursor = dbHelper.getActivityLog(userId);
        ArrayList<String> activityList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String type = cursor.getString(cursor.getColumnIndexOrThrow("activity_type"));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow("calories_burned"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                activityList.add(date + " - " + type + ": " + duration + ", -" + calories + " kcal");
            } while (cursor.moveToNext());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activityList) {
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(android.graphics.Color.BLACK); // force black color
                return view;
            }
        };

        listViewActivities.setAdapter(adapter);
    }
}
