package com.example.projeto.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "goals")
public class Goal {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String name;
    public double targetAmount;
    public double currentAmount;

    public Goal(String name, double targetAmount, double currentAmount) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
    }
}