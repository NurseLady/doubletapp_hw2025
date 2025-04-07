package com.doubletapp_hw.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.doubletapp_hw.Habit
import com.doubletapp_hw.HabitRepository
import com.doubletapp_hw.SortingType
import kotlinx.coroutines.flow.MutableStateFlow

class HabitListViewModel(private val habitRepository: HabitRepository) : ViewModel() {
    val habits: LiveData<List<Habit>> = habitRepository.habits
    private val query = MutableStateFlow("")
    private val sortOption = MutableStateFlow(SortingType.NAME)
    private val ascending = MutableStateFlow(true)

    val filteredHabits: LiveData<List<Habit>> = habits.map { currentHabits ->
        val filteredList = currentHabits.filter { it.name.contains(query.value, ignoreCase = true) }
        when (sortOption.value) {
            SortingType.NAME -> if (ascending.value) filteredList.sortedBy { it.name.lowercase() }
            else filteredList.sortedByDescending { it.name.lowercase() }

            SortingType.DATE -> if (ascending.value) filteredList.sortedBy { it.lastEdited }
            else filteredList.sortedByDescending { it.lastEdited }

            SortingType.PRIORITY -> if (ascending.value) filteredList.sortedBy { it.priority }
            else filteredList.sortedByDescending { it.priority.ordinal }
        }
    }

    fun applyFilters(newQuery: String, newSortOption: SortingType, newAscending: Boolean) {
        query.value = newQuery
        sortOption.value = newSortOption
        ascending.value = newAscending
    }
}

