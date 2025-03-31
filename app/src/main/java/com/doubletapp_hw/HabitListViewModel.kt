package com.doubletapp_hw

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HabitListViewModel: ViewModel() {
    private val _habits = MutableStateFlow<List<Habit>>(listOf())
    val habits: StateFlow<List<Habit>> get() = _habits.asStateFlow()

    fun saveHabit(habit: Habit) {
        _habits.value.let { habitsList ->
            val updatedList = habitsList.toMutableList()
            val index = updatedList.indexOfFirst { it.id == habit.id }
            if (index != -1) {
                updatedList[index] = habit
            } else {
                updatedList.add(habit)
            }
            _habits.value = updatedList
        }
    }

/*    fun addHabit(habit: Habit): String {
        val updatedList = _habits.value.toMutableList()
        updatedList.add(habit)
        _habits.value = updatedList
        return habit.id
    }

    fun updateHabit(habit: Habit): Boolean {
        _habits.value.let { habitsList ->
            val updatedList = habitsList.toMutableList()
            val index = updatedList.indexOfFirst { it.id == habit.id }
            if (index != -1) {
                updatedList[index] = habit
                _habits.value = updatedList
                return true
            }
        }
        return false
    }*/

/*    fun deleteHabit(habitId: String) {
        _habits.value.let { habitsList ->
            val updatedList = habitsList.filter { it.id != habitId }
            _habits.value = updatedList
        }
    }
    }*/

    fun getById(habitId: String): Habit? {
        return _habits.value.find { it.id == habitId }
    }
}