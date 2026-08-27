package com.example.projeto.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.projeto.data.dao.CategoryDao;
import com.example.projeto.data.dao.GoalDao;
import com.example.projeto.data.dao.TransactionDao;
import com.example.projeto.data.dao.UserDao;
import com.example.projeto.data.model.Category;
import com.example.projeto.data.model.Goal;
import com.example.projeto.data.model.Transaction;
import com.example.projeto.data.model.User;

@Database(entities = {Transaction.class, Goal.class, User.class, Category.class}, version = 12)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract TransactionDao transactionDao();
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract GoalDao goalDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "finance_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}