package com.example.domain.usecase

import com.example.domain.Habit
import com.example.domain.HabitRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(habit: Habit) = repository.deleteHabit(habit)
}