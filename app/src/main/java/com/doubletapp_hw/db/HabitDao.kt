package com.doubletapp_hw.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.doubletapp_hw.Habit

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits")
    fun getAllHabits(): LiveData<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabitById(id: String): Habit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHabit(habit: Habit)

    @Delete
    fun deleteHabit(habit: Habit)
}