package com.doubletapp_hw.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doubletapp_hw.Habit
import com.doubletapp_hw.HabitRepository
import kotlinx.coroutines.launch

class HabitEditViewModel(
    private val habitRepository: HabitRepository,
    private val habitId: String
) : ViewModel() {
    private val _habit = MutableLiveData<Habit>()
    val habit: LiveData<Habit> = _habit

    init {
        viewModelScope.launch {
            _habit.value = habitRepository.getHabitById(habitId) ?: Habit()
        }
    }

    fun saveHabit(habit: Habit) {
        viewModelScope.launch {
            habit.isSynced = false
            habitRepository.saveHabit(habit)
        }
    }
}