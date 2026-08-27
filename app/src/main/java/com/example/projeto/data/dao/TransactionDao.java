package com.example.projeto.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.projeto.data.model.Transaction;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE id = :id")
    LiveData<Transaction> getTransactionById(int id);

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT SUM(value) FROM transactions WHERE type = 'INCOME'")
    LiveData<Double> getTotalIncome();

    @Query("SELECT SUM(value) FROM transactions WHERE type = 'EXPENSE'")
    LiveData<Double> getTotalExpense();

    @Query("SELECT SUM(value) FROM transactions WHERE type = 'INCOME' AND date BETWEEN :start AND :end")
    LiveData<Double> getTotalIncomeInRange(long start, long end);

    @Query("SELECT SUM(value) FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :start AND :end")
    LiveData<Double> getTotalExpenseInRange(long start, long end);

    @Query("SELECT category as name, SUM(value) as value FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :start AND :end GROUP BY category")
    LiveData<List<CategorySpending>> getSpendingByCategoryInRange(long start, long end);

    @Query("SELECT category as name, SUM(value) as value FROM transactions WHERE type = 'INCOME' AND date BETWEEN :start AND :end GROUP BY category")
    LiveData<List<CategorySpending>> getIncomeByCategoryInRange(long start, long end);

    @Query("SELECT date, SUM(value) as total FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :start AND :end GROUP BY date ORDER BY date ASC")
    LiveData<List<DailySpending>> getDailySpendingInRange(long start, long end);

    @Query("SELECT date, SUM(value) as total FROM transactions WHERE type = 'INCOME' AND date BETWEEN :start AND :end GROUP BY date ORDER BY date ASC")
    LiveData<List<DailySpending>> getDailyIncomeInRange(long start, long end);

    @Query("SELECT category as name, SUM(value) as value FROM transactions WHERE type = 'EXPENSE' GROUP BY category")
    LiveData<List<CategorySpending>> getSpendingByCategory();

    @Query("SELECT date, category as name, SUM(value) as value FROM transactions WHERE type = :type AND date BETWEEN :start AND :end GROUP BY date, category ORDER BY date ASC")
    LiveData<List<DailyCategorySpending>> getDailyCategorySpendingInRange(String type, long start, long end);

    class CategorySpending {
        public String name;
        public double value;
    }

    class DailySpending {
        public long date;
        public double total;
    }

    class DailyCategorySpending {
        public long date;
        public String name;
        public double value;
    }
}