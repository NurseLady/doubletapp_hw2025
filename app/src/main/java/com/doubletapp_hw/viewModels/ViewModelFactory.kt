package com.doubletapp_hw.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.doubletapp_hw.HabitRepository

class ViewModelFactory(private val habitRepository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HabitListViewModel::class.java) -> {
                HabitListViewModel(habitRepository) as T
            }

            modelClass.isAssignableFrom(HabitEditViewModel::class.java) -> {
                HabitEditViewModel(habitRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}