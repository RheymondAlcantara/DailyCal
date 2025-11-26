package com.example.dailycals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView tvCaloriesEaten, tvCaloriesBurned, tvBMIStatus;
    TextView tvBreakfastProgress, tvLunchProgress, tvDinnerProgress, tvSnacksProgress;
    TextView tvCarbs, tvProtein, tvFat;
    PieChart pieChart, pieChartCalories;
    DatabaseHelper db;
    int userId;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);

        tvCaloriesEaten = findViewById(R.id.tvCaloriesEaten);
        tvCaloriesBurned = findViewById(R.id.tvCaloriesBurned);
        tvBMIStatus = findViewById(R.id.tvBMIStatus);

        tvCarbs = findViewById(R.id.tvCarbs);
        tvProtein = findViewById(R.id.tvProtein);
        tvFat = findViewById(R.id.tvFat);

        pieChart = findViewById(R.id.pieChartCalories);  // ✅ This ID matches the <com.github.mikephil.charting.charts.PieChart>
        pieChartCalories = findViewById(R.id.pieChartCalories); // New: Calories per meal pie chart

        setupMealCard(R.id.breakfastSection, "Breakfast", R.drawable.ic_breakfast, 1);
        setupMealCard(R.id.lunchSection, "Lunch", R.drawable.ic_lunch, 2);
        setupMealCard(R.id.dinnerSection, "Dinner", R.drawable.ic_dinner, 3);
        setupMealCard(R.id.snacksSection, "Snacks", R.drawable.ic_snacks, 4);

        findViewById(R.id.btnBMIResult).setOnClickListener(v -> startActivity(new Intent(this, BMIActivity.class)));
        findViewById(R.id.btnTrackCalorie).setOnClickListener(v -> startActivity(new Intent(this, TrackCalorieActivity.class)));
        findViewById(R.id.btnHistory).setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        findViewById(R.id.btnAddActivity).setOnClickListener(v -> startActivity(new Intent(this, ActivityAddActivity.class)));

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            sp.edit().remove("user_id").apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        updateDashboard();
    }

    private void setupMealCard(int sectionId, String label, int iconResId, int requestCode) {
        ViewGroup section = findViewById(sectionId);
        TextView title = section.findViewById(R.id.tvMealTitle);
        ImageView icon = section.findViewById(R.id.ivMealIcon);
        ImageButton btnAdd = section.findViewById(R.id.btnAddMeal);
        TextView progress = section.findViewById(R.id.tvMealProgress);

        title.setText(label);
        icon.setImageResource(iconResId);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SavedFoodsActivity.class);
            intent.putExtra("mealType", label);
            startActivityForResult(intent, requestCode);
        });

        switch (label) {
            case "Breakfast": tvBreakfastProgress = progress; break;
            case "Lunch": tvLunchProgress = progress; break;
            case "Dinner": tvDinnerProgress = progress; break;
            case "Snacks": tvSnacksProgress = progress; break;
        }
    }

    private void updateDashboard() {
        int total = db.getTodayTotalCalories(userId);
        int burned = db.getTodayCaloriesBurned(userId);
        String bmiRaw = db.getLatestBMI(userId);
        try {
            float bmiValue = Float.parseFloat(bmiRaw);
            String category;

            if (bmiValue < 18.5) {
                category = "Underweight";
            } else if (bmiValue < 25) {
                category = "Normal";
            } else if (bmiValue < 30) {
                category = "Overweight";
            } else {
                category = "Obese";
            }

            tvBMIStatus.setText(String.format("BMI: %.2f (%s)", bmiValue, category));
        } catch (NumberFormatException e) {
            tvBMIStatus.setText("BMI: N/A");
        }

        tvCaloriesEaten.setText("Calories Eaten: " + total + " kcal");
        tvCaloriesBurned.setText("Calories Burned: " + burned + " kcal");

        int carbs = db.getTodayTotalMacro(userId, "carbs");
        int protein = db.getTodayTotalMacro(userId, "protein");
        int fat = db.getTodayTotalMacro(userId, "fat");

        tvCarbs.setText("Carbs: " + carbs + "g");
        tvProtein.setText("Protein: " + protein + "g");
        tvFat.setText("Fat: " + fat + "g");

        updateMealProgress("Breakfast", tvBreakfastProgress);
        updateMealProgress("Lunch", tvLunchProgress);
        updateMealProgress("Dinner", tvDinnerProgress);
        updateMealProgress("Snacks", tvSnacksProgress);

        updatePieChart(carbs, protein, fat);
        updateCaloriePieChart(); // 👈 Call for calories pie chart
    }

    private void updateMealProgress(String mealType, TextView view) {
        int mealTotal = db.getTodayMealCalories(userId, mealType);
        view.setText(mealTotal + " kcal");
    }

    private void updatePieChart(int carbs, int protein, int fat) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (carbs > 0) entries.add(new PieEntry(carbs, "Carbs"));
        if (protein > 0) entries.add(new PieEntry(protein, "Protein"));
        if (fat > 0) entries.add(new PieEntry(fat, "Fat"));

        PieDataSet dataSet = new PieDataSet(entries, "Macronutrient Breakdown");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);

        pieChart.setData(data);
        pieChart.setCenterText("Macros");
        pieChart.setEntryLabelTextSize(12f);
        pieChart.invalidate();
    }

    private void updateCaloriePieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        int breakfast = db.getTodayMealCalories(userId, "Breakfast");
        int lunch = db.getTodayMealCalories(userId, "Lunch");
        int dinner = db.getTodayMealCalories(userId, "Dinner");
        int snacks = db.getTodayMealCalories(userId, "Snacks");

        int total = breakfast + lunch + dinner + snacks;

        if (breakfast > 0) entries.add(new PieEntry(breakfast, "Breakfast"));
        if (lunch > 0) entries.add(new PieEntry(lunch, "Lunch"));
        if (dinner > 0) entries.add(new PieEntry(dinner, "Dinner"));
        if (snacks > 0) entries.add(new PieEntry(snacks, "Snacks"));

        PieDataSet dataSet = new PieDataSet(entries, "Calories by Meal");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);

        pieChartCalories.setData(data);
        pieChartCalories.setCenterText("Calories\n" + total + " kcal");
        pieChartCalories.setEntryLabelTextSize(12f);
        pieChartCalories.invalidate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String food = data.getStringExtra("food");
            int calories = data.getIntExtra("calories", 0);
            int carbs = data.getIntExtra("carbs", 0);
            int protein = data.getIntExtra("protein", 0);
            int fat = data.getIntExtra("fat", 0);
            String mealType = data.getStringExtra("mealType");
            db.addFullCalorieEntry(userId, food, calories, carbs, protein, fat, mealType);
            updateDashboard();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboard();
    }
}
