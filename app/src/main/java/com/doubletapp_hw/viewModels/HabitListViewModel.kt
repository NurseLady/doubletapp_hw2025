package com.doubletapp_hw.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doubletapp_hw.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitListViewModel : ViewModel() {
    private val habitRepository = HabitRepository

    private val habits: StateFlow<List<Habit>> = habitRepository.getHabitsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, listOf())

    private val _filteredHabits = MutableStateFlow<List<Habit>>(listOf())
    val filteredHabits: StateFlow<List<Habit>> = _filteredHabits

    init {
        viewModelScope.launch {
            habits.collect { currentHabits ->
                // При изменении списка привычек обновляем фильтры
                _filteredHabits.value = currentHabits
            }
        }
    }

    fun applyFilters(query: String) {
        _filteredHabits.value  = habits.value.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    fun sortByName(ascending: Boolean) {
        _filteredHabits.value = if (ascending) {
            habits.value.sortedBy { it.name }
        } else {
            habits.value.sortedByDescending { it.name }
        }
    }

    fun sortByDate(ascending: Boolean) {
        _filteredHabits.value = if (ascending) {
            habits.value.sortedBy { it.lastEdited }
        } else {
            habits.value.sortedByDescending { it.lastEdited }
        }
    }
}