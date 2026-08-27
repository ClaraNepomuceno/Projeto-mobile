package com.example.projeto.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public int userId;
    public String name;
    public String color; // Hex color string
    public String type; // "INCOME" or "EXPENSE"
    public String iconName; // Name of the icon resource
    public double budgetLimit;

    public Category(int userId, String name, String color, String type, String iconName, double budgetLimit) {
        this.userId = userId;
        this.name = name;
        this.color = color;
        this.type = type;
        this.iconName = iconName;
        this.budgetLimit = budgetLimit;
    }
}