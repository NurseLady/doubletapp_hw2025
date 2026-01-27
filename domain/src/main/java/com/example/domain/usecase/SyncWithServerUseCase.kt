package com.example.domain.usecase

import com.example.domain.HabitRepository
import javax.inject.Inject

class SyncWithServerUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke() = repository.syncWithServer()
}