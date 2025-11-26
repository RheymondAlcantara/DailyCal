package com.example.dailycals;

public class FoodItem {
    public String name;
    public int calories;
    public int carbs;
    public int protein;
    public int fat;

    public FoodItem(String name, int calories, int carbs, int protein, int fat) {
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }
}
