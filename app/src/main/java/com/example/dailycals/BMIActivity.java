package com.example.dailycals;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BMIActivity extends AppCompatActivity {

    EditText etHeight, etWeight;
    Button btnCalculateBMI, btnEditBMI, btnMetric, btnImperial;
    TextView tvBMIResult;
    boolean isMetric = true;

    DatabaseHelper db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        db = new DatabaseHelper(this);
        SharedPreferences sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);

        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        btnCalculateBMI = findViewById(R.id.btnCalculateBMI);
        btnEditBMI = findViewById(R.id.btnEditBMI);
        btnMetric = findViewById(R.id.btnMetric);
        btnImperial = findViewById(R.id.btnImperial);
        tvBMIResult = findViewById(R.id.tvBMIResult);

        etHeight.setEnabled(true);
        etWeight.setEnabled(true);

        btnCalculateBMI.setOnClickListener(v -> calculateBMI());

        btnEditBMI.setOnClickListener(v -> {
            etHeight.setEnabled(true);
            etWeight.setEnabled(true);
            tvBMIResult.setText("BMI Result");
            etHeight.requestFocus();
        });

        btnMetric.setOnClickListener(v -> {
            isMetric = true;
            etHeight.setHint("Enter height (cm)");
            etWeight.setHint("Enter weight (kg)");
            tvBMIResult.setText("BMI Result");
        });

        btnImperial.setOnClickListener(v -> {
            isMetric = false;
            etHeight.setHint("Enter height (in)");
            etWeight.setHint("Enter weight (lbs)");
            tvBMIResult.setText("BMI Result");
        });
    }

    private void calculateBMI() {
        String heightStr = etHeight.getText().toString();
        String weightStr = etWeight.getText().toString();

        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            tvBMIResult.setText("Please enter height and weight.");
            return;
        }

        float height, weight, bmi;

        try {
            height = Float.parseFloat(heightStr);
            weight = Float.parseFloat(weightStr);

            if (height <= 0 || weight <= 0) {
                tvBMIResult.setText("Values must be positive.");
                return;
            }

            if (isMetric) {
                height = height / 100f; // convert cm to meters
                bmi = weight / (height * height);
            } else {
                bmi = (weight / (height * height)) * 703f;
            }

            String bmiText = String.format("%.2f", bmi);
            String result;

            if (bmi < 18.5) {
                result = "Underweight (BMI: " + bmiText + ")";
            } else if (bmi < 25) {
                result = "Normal (BMI: " + bmiText + ")";
            } else if (bmi < 30) {
                result = "Overweight (BMI: " + bmiText + ")";
            } else {
                result = "Overweight (BMI: " + bmiText + ")";
            }

            tvBMIResult.setText(result);
            etHeight.setEnabled(false);
            etWeight.setEnabled(false);


            db.addBMIResult(userId, bmi);

        } catch (NumberFormatException e) {
            tvBMIResult.setText("Invalid number input.");
        }
    }
}
