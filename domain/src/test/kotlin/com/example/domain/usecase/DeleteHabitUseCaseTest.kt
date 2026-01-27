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
class DeleteHabitUseCaseTest {
    private lateinit var repository: HabitRepository
    private lateinit var deleteHabitUseCase: DeleteHabitUseCase

    @Before
    fun setup() {
        repository = mockk()
        deleteHabitUseCase = DeleteHabitUseCase(repository)
    }

    @Test
    fun `deleteHabitUseCase должен вызвать deleteHabit с верным аргументом`() = runTest {
        val testHabit = Habit()
        coEvery { repository.deleteHabit(testHabit) } just Runs

        deleteHabitUseCase(testHabit)

        coVerify { repository.deleteHabit(testHabit) }
    }
}