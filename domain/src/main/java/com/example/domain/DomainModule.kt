package com.example.domain

import com.example.domain.usecase.DeleteHabitUseCase
import com.example.domain.usecase.GetHabitUseCase
import com.example.domain.usecase.MarkHabitDoneUseCase
import com.example.domain.usecase.SaveHabitUseCase
import com.example.domain.usecase.SyncWithServerUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DomainModule {
    @Provides
    @Singleton
    fun provideGetHabitUseCase(repository: HabitRepository): GetHabitUseCase {
        return GetHabitUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteHabitUseCase(repository: HabitRepository): DeleteHabitUseCase {
        return DeleteHabitUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveHabitUseCase(repository: HabitRepository): SaveHabitUseCase {
        return SaveHabitUseCase(repository)
    }

    @Provides
    @Singleton
    fun markHabitDoneUseCase(
        repository: HabitRepository
    ): MarkHabitDoneUseCase {
        return MarkHabitDoneUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSyncWithServerUseCase(repository: HabitRepository): SyncWithServerUseCase {
        return SyncWithServerUseCase(repository)
    }
}