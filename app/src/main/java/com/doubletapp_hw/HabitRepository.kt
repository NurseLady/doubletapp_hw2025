package com.doubletapp_hw

import androidx.lifecycle.LiveData
import com.doubletapp_hw.db.HabitDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitRepository(private val habitDao: HabitDao) {
    val habits: LiveData<List<Habit>> = habitDao.getAllHabits()

    suspend fun getHabitById(id: String): Habit? {
        return withContext(Dispatchers.IO) {
            habitDao.getHabitById(id)
        }
    }

    suspend fun saveHabit(habit: Habit) {
        withContext(Dispatchers.IO) {
            habitDao.insertHabit(habit)
        }
    }

    suspend fun deleteHabit(habit: Habit) {
        withContext(Dispatchers.IO) {
            habitDao.deleteHabit(habit)
        }
    }
}