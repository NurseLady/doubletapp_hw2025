package com.doubletapp_hw.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.doubletapp_hw.Habit
import com.doubletapp_hw.HabitRepository
import com.doubletapp_hw.SortingType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitListViewModel(private val habitRepository: HabitRepository) : ViewModel() {
    private val query = MutableStateFlow("")
    private val sortOption = MutableStateFlow(SortingType.NAME)
    private val ascending = MutableStateFlow(true)

    val filteredHabits: StateFlow<List<Habit>> = combine(
        habitRepository.habits.asFlow(),
        query,
        sortOption,
        ascending
    ) { currentHabits, searchQuery, sortingType, isAscending ->
        val filteredList = currentHabits.filter { it.name.contains(searchQuery, ignoreCase = true) }
        when (sortingType) {
            SortingType.NAME -> if (isAscending) filteredList.sortedBy { it.name.lowercase() }
            else filteredList.sortedByDescending { it.name.lowercase() }

            SortingType.DATE -> if (isAscending) filteredList.sortedBy { it.lastEdited }
            else filteredList.sortedByDescending { it.lastEdited }

            SortingType.PRIORITY -> if (isAscending) filteredList.sortedBy { it.priority }
            else filteredList.sortedByDescending { it.priority.ordinal }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, listOf())

    fun applyFilters(newQuery: String, newSortOption: SortingType, newAscending: Boolean) {
        query.value = newQuery
        sortOption.value = newSortOption
        ascending.value = newAscending
    }

    //Насколько окэй так делать?
    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habit)
        }
    }
}

