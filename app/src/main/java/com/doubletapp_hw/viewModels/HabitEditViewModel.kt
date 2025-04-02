package com.doubletapp_hw.viewModels

import androidx.lifecycle.ViewModel
import com.doubletapp_hw.Habit

class HabitEditViewModel : ViewModel() {
    private val habitRepository = HabitRepository

    fun getHabitById(id: String): Habit? {
        return habitRepository.getHabitById(id)
    }

    fun saveHabit(habit: Habit) {
        habitRepository.saveHabit(habit)
    }
}