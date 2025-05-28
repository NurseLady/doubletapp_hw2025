package com.doubletapp_hw.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doubletapp_hw.enums.SortingType
import com.example.domain.Habit
import com.example.domain.usecase.DeleteHabitUseCase
import com.example.domain.usecase.GetHabitUseCase
import com.example.domain.usecase.MarkHabitDoneUseCase
import com.example.domain.usecase.SyncWithServerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitListViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitUseCase,
    private val syncWithServerUseCase: SyncWithServerUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val markHabitDoneUseCase: MarkHabitDoneUseCase
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val sortOption = MutableStateFlow(SortingType.DATE)
    private val ascending = MutableStateFlow(false)

    val syncState = mutableStateOf(false)

    private val _toastMessage = mutableStateOf<String?>(null)
    val toastMessage: State<String?> = _toastMessage

    val filteredHabits: StateFlow<List<Habit>> = combine(
        getHabitsUseCase(), query, sortOption, ascending
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
            else filteredList.sortedByDescending { it.priority }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, listOf())

    init {
        syncHabitsWithServer()
    }

    fun applyFilters(newQuery: String, newSortOption: SortingType, newAscending: Boolean) {
        query.value = newQuery
        sortOption.value = newSortOption
        ascending.value = newAscending
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            deleteHabitUseCase(habit)
        }
    }

    fun syncHabitsWithServer() {
        syncState.value = true
        viewModelScope.launch {
            try {
                syncWithServerUseCase()
            } finally {
                syncState.value = false
            }
        }
    }

    fun markHabitDone(habit: Habit) {
        viewModelScope.launch {
            markHabitDoneUseCase(habit) { message ->
                _toastMessage.value = message
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}