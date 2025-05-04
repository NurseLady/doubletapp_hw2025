package com.doubletapp_hw.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.doubletapp_hw.Habit

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits")
    fun getAllHabits(): LiveData<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: String): Habit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<Habit>)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM habits WHERE isDeleted = 1")
    suspend fun getHabitsMarkedForDeletion(): List<Habit>

    @Transaction
    suspend fun updateHabit(habit: Habit, uid: String) {
        deleteHabit(habit)
        insertHabit(
            habit.copy(
                id = uid,
                isSynced = true,
                isNew = false
            )
        )
    }
}