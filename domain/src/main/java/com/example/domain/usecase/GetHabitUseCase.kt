package com.example.domain.usecase

import com.example.domain.Habit
import com.example.domain.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(id: String): Habit? = repository.getHabit(id)
    operator fun invoke(): Flow<List<Habit>> = repository.getHabits()
}