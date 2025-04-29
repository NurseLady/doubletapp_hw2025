package com.doubletapp_hw.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.doubletapp_hw.Habit
import com.doubletapp_hw.HabitRepository
import com.doubletapp_hw.enums.SortingType
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

    // Флаг синхронизации
    val syncState = mutableStateOf<Boolean?>(null)

    val filteredHabits: StateFlow<List<Habit>> = combine(
        habitRepository.habits.asFlow(),
        query,
        sortOption,
        ascending
    ) { currentHabits, searchQuery, sortingType, isAscending ->
        val filteredList = currentHabits.filter {
            it.title.contains(searchQuery, ignoreCase = true) && !it.isDeleted
        }
        when (sortingType) {
            SortingType.NAME -> if (isAscending) filteredList.sortedBy { it.title.lowercase() }
            else filteredList.sortedByDescending { it.title.lowercase() }

            SortingType.DATE -> if (isAscending) filteredList.sortedBy { it.date }
            else filteredList.sortedByDescending { it.date }

            SortingType.PRIORITY -> if (isAscending) filteredList.sortedBy { it.priority }
            else filteredList.sortedByDescending { it.priority.ordinal }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, listOf())

    init {
        viewModelScope.launch {
            syncHabitsWithServer()
        }
    }

    fun applyFilters(newQuery: String, newSortOption: SortingType, newAscending: Boolean) {
        query.value = newQuery
        sortOption.value = newSortOption
        ascending.value = newAscending
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habit)
        }
    }

    fun syncHabitsWithServer() {
        viewModelScope.launch {
            syncState.value = true
            try {
                habitRepository.syncWithServer()
            } finally {
                syncState.value = false
            }
        }
    }
}