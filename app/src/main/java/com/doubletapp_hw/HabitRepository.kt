package com.doubletapp_hw

import androidx.lifecycle.LiveData
import com.doubletapp_hw.db.HabitDao

class HabitRepository(private val habitDao: HabitDao) {
    val habits: LiveData<List<Habit>> = habitDao.getAllHabits()

    suspend fun getHabitById(id: String): Habit? {
        return habitDao.getHabitById(id)
    }

    suspend fun saveHabit(habit: Habit) {
        habitDao.insertHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit)
    }
}