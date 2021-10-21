package com.example.animelistapp.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task")
    List<Task> getAll();

    @Insert(onConflict = REPLACE)
    void insert(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT EXISTS (SELECT 1 FROM task WHERE title = :title)")
    boolean exists(String title);


}
