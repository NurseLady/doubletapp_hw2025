package com.doubletapp_hw.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doubletapp_hw.Habit
import com.doubletapp_hw.HabitRepository
import kotlinx.coroutines.launch

class HabitEditViewModel(private val habitRepository: HabitRepository) : ViewModel() {
    private val _habit = MutableLiveData<Habit>()
    val habit: LiveData<Habit> = _habit

    fun loadHabitById(id: String) {
        viewModelScope.launch {
            _habit.value = habitRepository.getHabitById(id) ?: Habit()
        }
    }

    fun saveHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.saveHabit(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habit)
        }
    }
}