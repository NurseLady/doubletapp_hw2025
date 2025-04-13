package com.doubletapp_hw.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.doubletapp_hw.HabitRepository

//Тут я не стала исправлять по причине не нагугглила тот хороший вариант
@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val habitRepository: HabitRepository,
    private val habitId: String = ""
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HabitListViewModel::class.java) -> {
                HabitListViewModel(habitRepository) as T
            }

            modelClass.isAssignableFrom(HabitEditViewModel::class.java) -> {
                HabitEditViewModel(habitRepository, habitId) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}