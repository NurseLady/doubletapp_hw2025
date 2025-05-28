package com.example.domain.usecase

import com.example.domain.Habit
import com.example.domain.HabitRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class SaveHabitUseCaseTest {
    private lateinit var repository: HabitRepository
    private lateinit var saveHabitUseCase: SaveHabitUseCase

    @Before
    fun setup() {
        repository = mockk()
        saveHabitUseCase = SaveHabitUseCase(repository)
    }

    @Test
    fun `saveHabitUseCase должен вызвать addHabit  корректным аргументом`() = runTest {
        val testHabit = Habit(title = "Test Habit")
        coEvery { repository.addHabit(testHabit) } just Runs

        saveHabitUseCase(testHabit)

        coVerify { repository.addHabit(testHabit) }
    }
}