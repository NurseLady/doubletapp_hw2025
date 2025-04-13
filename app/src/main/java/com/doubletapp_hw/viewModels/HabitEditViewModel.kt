package com.doubletapp_hw.viewModels

import androidx.lifecycle.ViewModel
import com.doubletapp_hw.Habit
import com.doubletapp_hw.HabitRepository

class HabitEditViewModel(private val habitRepository: HabitRepository) : ViewModel() {
    fun getHabitById(id: String): Habit? {
        return habitRepository.getHabitById(id)
    }

    fun saveHabit(habit: Habit) {
        habitRepository.saveHabit(habit)
    }

    fun deleteHabit(habit: Habit) {
        habitRepository.deleteHabit(habit)
    }
}