package com.example.domain

import kotlinx.coroutines.flow.Flow


interface HabitRepository {
    fun getHabits(): Flow<List<Habit>>
    suspend fun addHabit(habit: Habit)
    suspend fun syncWithServer()
    suspend fun deleteHabit(habit: Habit)
    suspend fun getHabit(id: String): Habit?
    suspend fun markHabitDone(habit: Habit)
}