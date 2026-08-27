package com.example.projeto.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.projeto.data.model.Goal;
import java.util.List;

@Dao
public interface GoalDao {
    @Insert
    void insert(Goal goal);

    @Update
    void update(Goal goal);

    @Delete
    void delete(Goal goal);

    @Query("SELECT * FROM goals")
    LiveData<List<Goal>> getAll();

    @Query("SELECT * FROM goals WHERE id = :id LIMIT 1")
    LiveData<Goal> getGoalById(int id);
}