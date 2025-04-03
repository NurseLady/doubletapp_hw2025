package com.doubletapp_hw.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doubletapp_hw.Habit
import com.doubletapp_hw.R
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

    fun applyFilters(query: String, sortOption: SortingType, ascending: Boolean) {
        val filteredList = habits.value.filter {
            it.name.contains(query, ignoreCase = true)
        }
        _filteredHabits.value = when (sortOption) {
            SortingType.NAME -> {
                if (ascending) filteredList.sortedBy { it.name } else filteredList.sortedByDescending { it.name }
            }

            SortingType.DATE -> {
                if (ascending) filteredList.sortedBy { it.lastEdited } else filteredList.sortedByDescending { it.lastEdited }
            }

            SortingType.PRIORITY -> {
                if (ascending) filteredList.sortedBy { it.priority } else filteredList.sortedByDescending { it.priority.ordinal }
            }
        }
    }
}

enum class SortingType(val labelResId: Int) {
    NAME(R.string.name),
    DATE(R.string.date),
    PRIORITY(R.string.priority)
}

