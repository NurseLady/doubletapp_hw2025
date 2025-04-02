package com.doubletapp_hw.viewModels

import com.doubletapp_hw.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object HabitRepository {
    private val habits = MutableStateFlow<List<Habit>>(mutableListOf())

    fun getHabitsFlow(): StateFlow<List<Habit>> = habits

    fun getHabitById(id: String): Habit? {
        return habits.value.find { it.id == id }
    }

    fun saveHabit(habit: Habit) {
        val updatedHabits = habits.value.toMutableList().apply {
            val index = indexOfFirst { it.id == habit.id }
            if (index != -1) {
                set(index, habit)
            } else {
                add(habit)
            }
        }
        habits.value = updatedHabits
    }
}