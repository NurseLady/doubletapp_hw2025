package com.doubletapp_hw.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doubletapp_hw.Habit
import com.doubletapp_hw.SortingType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HabitListViewModel : ViewModel() {
    private val habitRepository = HabitRepository

    private val habits: StateFlow<List<Habit>> = habitRepository.getHabitsFlow()

    private val query = MutableStateFlow("")
    private val sortOption = MutableStateFlow(SortingType.NAME)
    private val ascending = MutableStateFlow(true)

    val filteredHabits: StateFlow<List<Habit>> = combine(
        habits, query, sortOption, ascending
    ) { currentHabits, currentQuery, currentSortOption, isAscending ->
        val filteredList = currentHabits.filter {
            it.name.contains(currentQuery, ignoreCase = true)
        }

        when (currentSortOption) {
            SortingType.NAME -> {
                if (isAscending) filteredList.sortedBy { it.name }
                else filteredList.sortedByDescending { it.name }
            }

            SortingType.DATE -> {
                if (isAscending) filteredList.sortedBy { it.lastEdited }
                else filteredList.sortedByDescending { it.lastEdited }
            }

            SortingType.PRIORITY -> {
                if (isAscending) filteredList.sortedBy { it.priority }
                else filteredList.sortedByDescending { it.priority.ordinal }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, listOf())

    fun applyFilters(newQuery: String, newSortOption: SortingType, newAscending: Boolean) {
        query.value = newQuery
        sortOption.value = newSortOption
        ascending.value = newAscending
    }
}

