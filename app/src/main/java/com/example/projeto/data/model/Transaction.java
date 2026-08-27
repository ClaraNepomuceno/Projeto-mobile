package com.example.projeto.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public double value;
    public String description;
    public long date;
    public String category;
    public String type; // "INCOME" or "EXPENSE"
    public String accountName;
    public String observation;
    public Integer linkedGoalId; // Link opcional para uma meta

    public Transaction(double value, String description, long date, String category, String type, String accountName, String observation, Integer linkedGoalId) {
        this.value = value;
        this.description = description;
        this.date = date;
        this.category = category;
        this.type = type;
        this.accountName = accountName;
        this.observation = observation;
        this.linkedGoalId = linkedGoalId;
    }
}