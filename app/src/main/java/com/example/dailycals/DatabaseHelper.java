package com.example.dailycals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "dailycals.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, password TEXT)");

        db.execSQL("CREATE TABLE calories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "food TEXT, " +
                "calories INTEGER, " +
                "carbs INTEGER, " +
                "protein INTEGER, " +
                "fat INTEGER, " +
                "meal_type TEXT, " +
                "date TEXT)");

        db.execSQL("CREATE TABLE food_list (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "food_name TEXT, " +
                "serving INTEGER, " +
                "unit TEXT, " +
                "calories INTEGER, " +
                "carbs INTEGER, " +
                "protein INTEGER, " +
                "fat INTEGER)");

        db.execSQL("CREATE TABLE activity_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "activity_type TEXT, " +
                "duration TEXT, " +
                "calories_burned INTEGER, " +
                "date TEXT)");

        db.execSQL("CREATE TABLE bmi_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "bmi REAL, " +
                "date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS calories");
        db.execSQL("DROP TABLE IF EXISTS food_list");
        db.execSQL("DROP TABLE IF EXISTS activity_log");
        db.execSQL("DROP TABLE IF EXISTS bmi_log");
        onCreate(db);
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // ----------------- USER METHODS -----------------

    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    public int loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username=? AND password=?",
                new String[]{username, password});
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return -1;
    }

    // ----------------- CALORIE TRACKING -----------------

    public void addFullCalorieEntry(int userId, String food, int calories, int carbs, int protein, int fat, String mealType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("food", food);
        values.put("calories", calories);
        values.put("carbs", carbs);
        values.put("protein", protein);
        values.put("fat", fat);
        values.put("meal_type", mealType);
        values.put("date", getTodayDate());
        db.insert("calories", null, values);
        db.close();
    }

    // Simpler calorie method without macros
    public void addCalorie(int userId, String food, int calories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("food", food);
        values.put("calories", calories);
        values.put("carbs", 0);
        values.put("protein", 0);
        values.put("fat", 0);
        values.put("meal_type", "Unspecified");
        values.put("date", getTodayDate());
        db.insert("calories", null, values);
        db.close();
    }

    public int getTodayMealCalories(int userId, String mealType) {
        SQLiteDatabase db = this.getReadableDatabase();
        String today = getTodayDate();
        Cursor cursor = db.rawQuery("SELECT SUM(calories) FROM calories WHERE user_id = ? AND meal_type = ? AND date = ?",
                new String[]{String.valueOf(userId), mealType, today});
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public int getTodayTotalCalories(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String today = getTodayDate();
        Cursor cursor = db.rawQuery("SELECT SUM(calories) FROM calories WHERE user_id = ? AND date = ?",
                new String[]{String.valueOf(userId), today});
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public int getTodayTotalMacro(int userId, String macro) {
        SQLiteDatabase db = this.getReadableDatabase();
        String today = getTodayDate();
        Cursor cursor = db.rawQuery("SELECT SUM(" + macro + ") FROM calories WHERE user_id = ? AND date = ?",
                new String[]{String.valueOf(userId), today});
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    // ----------------- ACTIVITY LOG -----------------

    public boolean addActivityLog(int userId, String activityType, String duration, int caloriesBurned) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("activity_type", activityType);
        values.put("duration", duration);
        values.put("calories_burned", caloriesBurned);
        values.put("date", getTodayDate());
        long result = db.insert("activity_log", null, values);
        db.close();
        return result != -1;
    }

    public int getTodayCaloriesBurned(int userId) {
        return getTodayTotalBurned(userId);
    }

    public int getTodayTotalBurned(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String today = getTodayDate();
        Cursor cursor = db.rawQuery("SELECT SUM(calories_burned) FROM activity_log WHERE user_id = ? AND date = ?",
                new String[]{String.valueOf(userId), today});
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public Cursor getActivityLog(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM activity_log WHERE user_id = ? ORDER BY date DESC", new String[]{String.valueOf(userId)});
    }

    // ----------------- BMI LOG -----------------

    public boolean addBMIResult(int userId, double bmi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("bmi", bmi);
        values.put("date", getTodayDate());
        long result = db.insert("bmi_log", null, values);
        db.close();
        return result != -1;
    }

    public String getLatestBMI(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String bmiResult = "Not Calculated";
        Cursor cursor = db.rawQuery("SELECT bmi FROM bmi_log WHERE user_id = ? ORDER BY date DESC LIMIT 1",
                new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            bmiResult = String.format("%.2f", cursor.getDouble(0));
        }
        cursor.close();
        db.close();
        return bmiResult;
    }

    // ----------------- FOOD LIST -----------------

    public Cursor getSavedFoods(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM food_list WHERE user_id = ?", new String[]{String.valueOf(userId)});
    }

    public Cursor getAllSavedFoods() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM food_list", null);
    }

    public boolean insertFoodItem(int userId, String name, int serving, String unit, int calories, int carbs, int protein, int fat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("food_name", name);
        values.put("serving", serving);
        values.put("unit", unit);
        values.put("calories", calories);
        values.put("carbs", carbs);
        values.put("protein", protein);
        values.put("fat", fat);
        long result = db.insert("food_list", null, values);
        db.close();
        return result != -1;
    }

    // ----------------- HISTORY -----------------

    public Cursor getHistory(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM calories WHERE user_id = ? ORDER BY date DESC", new String[]{String.valueOf(userId)});
    }
}
