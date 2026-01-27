package com.doubletapp_hw.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Habit
import com.example.domain.usecase.GetHabitUseCase
import com.example.domain.usecase.SaveHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitEditViewModel @Inject constructor(
    private val saveHabitUseCase: SaveHabitUseCase,
    private val getHabitsUseCase: GetHabitUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _habit = MutableLiveData<Habit>()
    val habit: LiveData<Habit> = _habit
    val habitIdLiveData = savedStateHandle.getLiveData<String>("id")

    init {
        habitIdLiveData.observeForever { id ->
            viewModelScope.launch {
                _habit.value = getHabitsUseCase(id) ?: Habit()
            }
        }
    }

    fun saveHabit(habit: Habit) {
        viewModelScope.launch {
            habit.isSynced = false
            saveHabitUseCase(habit)
        }
    }
}